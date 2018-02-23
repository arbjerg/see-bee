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
package com.namely.seebee.dockerdb.internal.dockerclient.unixdomainsocket;

import com.spotify.docker.client.ApacheUnixSocket;
import org.apache.http.HttpHost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.protocol.HttpContext;
import org.newsclub.net.unix.AFUNIXSocketAddress;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class UnixDomainConnectionSocketFactory implements ConnectionSocketFactory {

    private File socketFile;

    UnixDomainConnectionSocketFactory(final String socketPath) {
        this.socketFile = new File(socketPath);
    }

    @Override
    public Socket createSocket(final HttpContext context) throws IOException {
        return new ApacheUnixSocket();
    }

    @Override
    public Socket connectSocket(final int connectTimeout,
                                final Socket socket,
                                final HttpHost host,
                                final InetSocketAddress remoteAddress,
                                final InetSocketAddress localAddress,
                                final HttpContext context)
            throws IOException {
        try {
            socket.connect(new AFUNIXSocketAddress(socketFile), connectTimeout);
        } catch (SocketTimeoutException e) {
            throw new ConnectTimeoutException(e, null, remoteAddress.getAddress());
        }

        return socket;
    }
}
