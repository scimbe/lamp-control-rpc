package com.rpc.lampcontrol.middleware.transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Server-side transport layer that handles incoming connections.
 */
public class ServerTransport {
    private static final Logger logger = LoggerFactory.getLogger(ServerTransport.class);
    private final int port;
    private final ExecutorService executorService;
    private ServerSocket serverSocket;
    private volatile boolean running = false;

    /**
     * Creates a new ServerTransport that will listen on a specific port.
     *
     * @param port Port to listen on, or 0 for a system-assigned port
     */
    public ServerTransport(int port) {
        this.port = port;
        this.executorService = Executors.newCachedThreadPool();
    }

    /**
     * Starts the server and begins accepting connections.
     * Each connection is processed by the provided connection handler.
     *
     * @param connectionHandler The handler for processing each new connection
     * @throws IOException if the server socket cannot be created
     */
    public void start(Consumer<ConnectionHandler> connectionHandler) throws IOException {
        serverSocket = new ServerSocket(port);
        running = true;
        
        int boundPort = serverSocket.getLocalPort();
        logger.info("Server started on port {}", boundPort);
        
        // Start accepting connections in a separate thread
        executorService.submit(() -> acceptConnections(connectionHandler));
    }

    /**
     * Gets the port this server is bound to.
     *
     * @return the bound port
     */
    public int getPort() {
        if (serverSocket != null && serverSocket.isBound()) {
            return serverSocket.getLocalPort();
        }
        return -1;
    }

    /**
     * Stops the server and cleans up resources.
     */
    public void stop() {
        running = false;
        if (serverSocket != null) {
            try {
                serverSocket.close();
                logger.info("Server stopped");
            } catch (IOException e) {
                logger.error("Error stopping server: {}", e.getMessage());
            }
        }
        executorService.shutdown();
    }

    private void acceptConnections(Consumer<ConnectionHandler> connectionHandler) {
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                logger.info("Accepted connection from {}:{}", 
                    clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort());
                
                // Handle each connection in a separate thread
                executorService.submit(() -> {
                    try {
                        ConnectionHandler handler = new ConnectionHandler(clientSocket);
                        connectionHandler.accept(handler);
                    } catch (IOException e) {
                        logger.error("Error handling connection: {}", e.getMessage());
                    }
                });
            } catch (IOException e) {
                if (running) {
                    logger.error("Error accepting connection: {}", e.getMessage());
                }
            }
        }
    }
}