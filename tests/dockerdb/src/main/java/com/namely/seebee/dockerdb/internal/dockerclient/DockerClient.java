/**
 *
 * Copyright (c) 2017-2018, Namely, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); You may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.namely.seebee.dockerdb.internal.dockerclient;


import com.namely.seebee.dockerdb.DockerException;
import com.namely.seebee.dockerdb.internal.dockerclient.unixdomainsocket.ConnectionManager;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
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
import java.text.MessageFormat;
import java.util.*;

import static java.lang.System.Logger.Level.DEBUG;
import static java.util.stream.Collectors.joining;


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
                String streamOfResults = Arrays.stream(lines.split("\n"))
                        .map(String::trim)
                        .filter(stripper::match)
                        .map(stripper::strip)
                        .collect(joining("\n"));
                String others = Arrays.stream(lines.split("\n"))
                        .map(String::trim)
                        .filter(s -> !stripper.match(s))
                        .collect(joining("\n"));
                throw new DockerException(MessageFormat.format("Unable to build image, no ID: {0}\n{1}", others, streamOfResults));
            }

        } catch (IOException e) {
            throw new DockerException("Image build failed", e);
        }
    }

    public DockerContainer create(DockerImage image, String name, int port, List<String> binds, List<String> links) throws DockerException {
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

            Map<String, Object> hostConfig = new HashMap<>();
            List<Map<String, String>> hostPortList = new ArrayList<>();
            Map<String, String> hostPortMap = new HashMap<>();
            hostPortMap.put("HostPort", "" + port);
            hostPortList.add(hostPortMap);
            Map<String, Object> portBindings = new HashMap<>();
            portBindings.put("" + port + "/tcp", hostPortList);
            hostConfig.put("PortBindings", portBindings);
            if (!binds.isEmpty()) {
                hostConfig.put("Binds", binds);
            }
            if (!links.isEmpty()) {
                hostConfig.put("Links", links);
            }
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

    public String exec(DockerContainer container, String... cmd) throws DockerException {
        try {

            String jsonBodyString = "{\n" +
                    "  \"AttachStdin\": false,\n" +
                    "  \"AttachStdout\": true,\n" +
                    "  \"AttachStderr\": true,\n" +
                    "  \"DetachKeys\": \"ctrl-p,ctrl-q\",\n" +
                    "  \"Tty\": true,\n" +
                    "  \"Cmd\": "  +
                    "    " + quotedJsonStringList(cmd) +
                    "  \n" +
                    "}";

            HttpPost request = new HttpPost(BASE_RESOURCE + "/containers/" + container.getId() + "/exec");
            request.setEntity(new StringEntity(jsonBodyString, ContentType.APPLICATION_JSON));
            CloseableHttpResponse result = httpClient.execute(request);
            int code = result.getStatusLine().getStatusCode();
            if (code != 201) {
                switch (code) {
                    case 400: throw new DockerException("Bad parameter");
                    case 404: throw new DockerException("No such container");
                    case 409: throw new DockerException("Container is paused");
                    case 500: throw new DockerException("Server error");
                    default: throw new DockerException("Unknown error " + code);
                }
            }

            String json = EntityUtils.toString(result.getEntity(), "UTF-8");
            JSONParser parser = new JSONParser();
            JSONObject jresult = (JSONObject) parser.parse(json);
            String id = (String) jresult.get("Id");
            if (id == null) {
                throw new IOException("Unable to find Id of exec: " + json);
            }

            jsonBodyString = "{\n" +
                    "  \"Detach\": false,\n" +
                    "  \"Tty\": true\n" +
                    "}";

            request = new HttpPost(BASE_RESOURCE + "/exec/" + id + "/start");
            request.setEntity(new StringEntity(jsonBodyString, ContentType.APPLICATION_JSON));
            result = httpClient.execute(request);
            code = result.getStatusLine().getStatusCode();
            if (code != 200) {
                switch (code) {
                    case 404: throw new DockerException("No such exec");
                    case 409: throw new DockerException("Container is stopped or paused");
                    case 500: throw new DockerException("Server error");
                    default: throw new DockerException("Unknown error " + code);
                }
            }

            return EntityUtils.toString(result.getEntity(), "UTF-8").replace("\r\n", "\n");
        } catch (IOException | ParseException e) {
            throw new DockerException("Failed to exec", e);
        }
    }

    private String quotedJsonStringList(String[] cmd) {
        return "[ " + Arrays.stream(cmd)
                .map(s -> '"' + s + '"')
                .collect(joining(", ")) + " ]";
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
                    default: throw new DockerException("Unknown error" + code);
                }
            }
        } catch (IOException e) {
            throw new DockerException("Failed to kill container " + containerNameOrId, e);
        }
    }
}
