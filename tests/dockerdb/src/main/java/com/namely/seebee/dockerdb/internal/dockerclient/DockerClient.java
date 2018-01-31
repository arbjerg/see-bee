package com.namely.seebee.dockerdb.internal.dockerclient;


import com.namely.seebee.dockerdb.DockerException;
import com.namely.seebee.dockerdb.internal.dockerclient.unixdomainsocket.ConnectionManager;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.*;

import static java.lang.System.Logger.Level.DEBUG;


public class DockerClient implements AutoCloseable {
    private static final System.Logger LOGGER = System.getLogger(DockerClient.class.getName());

    private static final String DOCKER_SOCKET = "/var/run/docker.sock";
    private static final String BASE_RESOURCE = "unix://localhost:80";

    private final CloseableHttpClient httpClient;
    private final PoolingHttpClientConnectionManager connManager;


    public DockerClient() {
        connManager = new ConnectionManager(DOCKER_SOCKET);
        httpClient = HttpClients.custom().setConnectionManager(connManager).build();
    }

    @Override
    public void close() throws IOException {
        connManager.close();
        httpClient.close();
    }

    private class StringStripper {
        private final String head;
        private final String tail;

        private StringStripper(String head, String tail) {
            this.head = head;
            this.tail = tail;
        }

        private boolean match(String s) {
            return s.startsWith(head) && s.endsWith(tail);
        }

        private String strip(String s) {
            return s.substring(head.length(), s.length() - tail.length());
        }
    }

    public DockerImage buildImage(byte[] sourceArchive, String tag) throws DockerException {
        HttpPost request = new HttpPost(BASE_RESOURCE + "/build?t=" + tag);
        request.setEntity(new ByteArrayEntity(sourceArchive));
        try {
            CloseableHttpResponse result = httpClient.execute(request);
            final String lines = EntityUtils.toString(result.getEntity(), "UTF-8");
            final StringStripper stripper = new StringStripper("{\"stream\":\"", "\\n\"}");
            Optional<String> id = Arrays.stream(lines.split("\n"))
                    .map(String::trim)
                    .filter(stripper::match)
                    .map(stripper::strip)
                    .filter(line -> line.startsWith("Successfully built "))
                    .findFirst()
                    .map(line -> line.substring(19));
            if (id.isPresent()) {
                return new DockerImage(id.get());
            } else {
                throw new DockerException("Unable to build image");
            }

        } catch (IOException e) {
            throw new DockerException("Image build failed", e);
        }
    }

    public DockerContainer create(DockerImage image, String name, int port) throws DockerException {
        try {
            HttpPost request = new HttpPost(BASE_RESOURCE + "/containers/create?name=" + name);

            Map<String, Object> body = new HashMap<>();
            body.put("Image", image.getId());
            body.put("AttachStdin", true);
            body.put("Tty", true);
            body.put("OpenStdin", true);
            body.put("StdinOnce", true);
            Map<String, Object> ports = new HashMap<>();
            ports.put("" + port + "/tcp", new JSONObject());
            body.put("ExposedPorts", ports);

            List<Map<String, String>> hostPortList = new ArrayList<>();
            Map<String, String> hostPortMap = new HashMap<>();
            hostPortMap.put("HostPort", "" + port);
            hostPortList.add(hostPortMap);
            Map<String, Object> portBindings = new HashMap<>();
            portBindings.put("" + port + "/tcp", hostPortList);
            Map<String, Object> hostConfig = new HashMap<>();
            hostConfig.put("PortBindings", portBindings);
            body.put("HostConfig", hostConfig);
            JSONObject jsonBody = new JSONObject(body);
            String jsonBodyString = jsonBody.toJSONString();
            LOGGER.log(DEBUG, "Request body " + jsonBodyString);
            request.setEntity(new StringEntity(jsonBodyString, ContentType.APPLICATION_JSON));
            CloseableHttpResponse result = httpClient.execute(request);
            int code = result.getStatusLine().getStatusCode();
            if (code != 201) {
                switch (code) {
                    case 400: throw new DockerException("Bad parameter");
                    case 404: throw new DockerException("No such container");
                    case 409: throw new DockerException("Conflict");
                    case 500: throw new DockerException("Server error");
                    default: throw new DockerException("Unknown error");
                }
            }
            String json = EntityUtils.toString(result.getEntity(), "UTF-8");
            JSONParser parser = new JSONParser();
            JSONObject jresult = (JSONObject) parser.parse(json);
            String id = (String) jresult.get("Id");
            if (id == null) {
                throw new IOException("Unable to find Id of new container");
            }
            return new DockerContainer(id, name, port);
        } catch (IOException | ParseException | ClassCastException e) {
            throw new DockerException("Failed to create container", e);
        }
    }


    public void start(DockerContainer container) throws DockerException {
        try {
            HttpPost request = new HttpPost(BASE_RESOURCE + "/containers/" + container.getId() + "/start");
            request.setEntity(new StringEntity("", ContentType.APPLICATION_JSON));
            CloseableHttpResponse result = httpClient.execute(request);
            int codeFamily = result.getStatusLine().getStatusCode() / 100;
            if (codeFamily != 2) {
                switch (codeFamily) {
                    case 3: throw new DockerException("Container already started");
                    case 4: throw new DockerException("No such container");
                    case 5: throw new DockerException("Server error");
                    default: throw new DockerException("Unknown error");
                }
            }
        } catch (IOException e) {
            throw new DockerException("Failed to start container", e);
        }
    }


    public void stop(DockerContainer container) throws DockerException {
        try {
            HttpPost request = new HttpPost(BASE_RESOURCE + "/containers/" + container.getId() + "/stop");
            request.setEntity(new StringEntity("", ContentType.APPLICATION_JSON));
            CloseableHttpResponse result = httpClient.execute(request);
            int codeFamily = result.getStatusLine().getStatusCode() / 100;
            if (codeFamily != 2) {
                switch (codeFamily) {
                    case 3: throw new DockerException("Container already stopped");
                    case 4: throw new DockerException("No such container");
                    case 5: throw new DockerException("Server error");
                    default: throw new DockerException("Unknown error");
                }
            }
        } catch (IOException e) {
            throw new DockerException("Failed to stop container", e);
        }
    }


    public void killContainer(String containerNameOrId) throws DockerException {
        try {
            HttpPost request = new HttpPost(BASE_RESOURCE + "/containers/" + containerNameOrId + "/kill");
            request.setEntity(new StringEntity("", ContentType.APPLICATION_JSON));
            CloseableHttpResponse result = httpClient.execute(request);
            int codeFamily = result.getStatusLine().getStatusCode() / 100;
            if (codeFamily != 2) {
                switch (codeFamily) {
                    case 4: throw new DockerException("No such container");
                    case 5: throw new DockerException("Server error");
                    default: throw new DockerException("Unknown error");
                }
            }
        } catch (IOException e) {
            throw new DockerException("Failed to kill container " + containerNameOrId, e);
        }
    }

    public void kill(DockerContainer container) throws DockerException {
        killContainer(container.getId());
    }

    public void remove(DockerContainer container) throws DockerException {
        removeContainer(container.getId());
    }

    public void removeContainer(String containerNameOrId) throws DockerException {
        try {
            HttpDelete request = new HttpDelete(BASE_RESOURCE + "/containers/" + containerNameOrId);
            CloseableHttpResponse result = httpClient.execute(request);
            int code = result.getStatusLine().getStatusCode();
            if (code != 204) {
                switch (code) {
                    case 400: throw new DockerException("Bad parameter");
                    case 404: throw new DockerException("No such container");
                    case 409: throw new DockerException("Conflict");
                    case 500: throw new DockerException("Server error");
                    default: throw new DockerException("Unknown error");
                }
            }
        } catch (IOException e) {
            throw new DockerException("Failed to kill container " + containerNameOrId, e);
        }
    }
}
