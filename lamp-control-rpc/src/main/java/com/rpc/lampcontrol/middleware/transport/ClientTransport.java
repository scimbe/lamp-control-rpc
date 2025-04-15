package com.rpc.lampcontrol.middleware.transport;

import java.io.IOException;
import java.net.Socket;
import java.net.ConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client-side transport layer that handles establishing connections to servers.
 */
public class ClientTransport {
    private static final Logger logger = LoggerFactory.getLogger(ClientTransport.class);
    private static final int DEFAULT_TIMEOUT_MS = 5000;

    /**
     * Connects to a server at the specified host and port.
     *
     * @param host The hostname or IP address to connect to
     * @param port The port to connect to
     * @return A ConnectionHandler for the established connection
     * @throws IOException if the connection cannot be established
     */
    public ConnectionHandler connect(String host, int port) throws IOException {
        return connect(host, port, DEFAULT_TIMEOUT_MS);
    }

    /**
     * Connects to a server at the specified host and port with a custom timeout.
     *
     * @param host The hostname or IP address to connect to
     * @param port The port to connect to
     * @param timeoutMs The connection timeout in milliseconds
     * @return A ConnectionHandler for the established connection
     * @throws IOException if the connection cannot be established
     */
    public ConnectionHandler connect(String host, int port, int timeoutMs) throws IOException {
        logger.info("Connecting to {}:{} (timeout: {} ms)", host, port, timeoutMs);
        
        try {
            Socket socket = new Socket(host, port);
            socket.setSoTimeout(timeoutMs);
            return new ConnectionHandler(socket);
        } catch (ConnectException e) {
            logger.error("Failed to connect to {}:{}: {}", host, port, e.getMessage());
            throw new IOException("Failed to connect to " + host + ":" + port, e);
        }
    }
}