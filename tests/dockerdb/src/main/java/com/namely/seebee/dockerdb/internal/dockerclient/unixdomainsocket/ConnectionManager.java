package com.namely.seebee.dockerdb.internal.dockerclient.unixdomainsocket;

import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class ConnectionManager extends PoolingHttpClientConnectionManager {
    public ConnectionManager(String unixSocketPath) {
        super(getSchemeRegistry(unixSocketPath));
    }

    @Override
    public void close() {
        super.shutdown();
    }

    private static org.apache.http.config.Registry<ConnectionSocketFactory> getSchemeRegistry(String unixSocketPath) {
        RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.create();
        registryBuilder.register("http", PlainConnectionSocketFactory.getSocketFactory());
        registryBuilder.register("unix", new UnixDomainConnectionSocketFactory(unixSocketPath));
        return registryBuilder.build();
    }
}
