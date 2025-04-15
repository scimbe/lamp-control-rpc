package com.rpc.lampcontrol.middleware.registry;

import com.rpc.lampcontrol.middleware.common.JsonSerializer;
import com.rpc.lampcontrol.middleware.protocol.*;
import com.rpc.lampcontrol.middleware.transport.ConnectionHandler;
import com.rpc.lampcontrol.middleware.transport.ServerTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Registry server that handles service registration and lookup requests.
 */
public class RegistryServer {
    private static final Logger logger = LoggerFactory.getLogger(RegistryServer.class);
    private static final int DEFAULT_PORT = 7777;
    
    private final int port;
    private final ServiceRegistry registry;
    private final ServerTransport serverTransport;
    
    /**
     * Creates a new RegistryServer on the default port.
     */
    public RegistryServer() {
        this(DEFAULT_PORT);
    }
    
    /**
     * Creates a new RegistryServer on the specified port.
     *
     * @param port The port to listen on
     */
    public RegistryServer(int port) {
        this.port = port;
        this.registry = new ServiceRegistry();
        this.serverTransport = new ServerTransport(port);
    }
    
    /**
     * Starts the registry server.
     *
     * @throws IOException if the server cannot be started
     */
    public void start() throws IOException {
        logger.info("Starting registry server on port {}", port);
        serverTransport.start(this::handleConnection);
        logger.info("Registry server started on port {}", serverTransport.getPort());
    }
    
    /**
     * Stops the registry server.
     */
    public void stop() {
        logger.info("Stopping registry server");
        serverTransport.stop();
        registry.clear();
        logger.info("Registry server stopped");
    }
    
    /**
     * Gets the port the registry server is running on.
     *
     * @return the port
     */
    public int getPort() {
        return serverTransport.getPort();
    }
    
    private void handleConnection(ConnectionHandler connection) {
        try {
            // Read the raw message
            String json = connection.readRawMessage();
            
            // Deserialize to determine message type
            Message message = JsonSerializer.deserialize(json);
            
            // Process based on message type
            if (message instanceof RegisterRequest) {
                handleRegisterRequest(connection, (RegisterRequest) message);
            } else if (message instanceof LookupRequest) {
                handleLookupRequest(connection, (LookupRequest) message);
            } else {
                logger.warn("Received unknown message type: {}", message.getClass().getSimpleName());
                // Send error response
                RpcResponse errorResponse = RpcResponse.error("Unknown message type");
                errorResponse.setTraceId(message.getTraceId());
                connection.sendMessage(errorResponse);
            }
        } catch (Exception e) {
            logger.error("Error handling connection: {}", e.getMessage());
        } finally {
            connection.close();
        }
    }
    
    private void handleRegisterRequest(ConnectionHandler connection, RegisterRequest request) 
            throws ConnectionHandler.ConnectionException {
        String functionName = request.getFunctionName();
        String host = request.getHost();
        int port = request.getPort();
        
        logger.info("Received register request for function '{}' at {}:{}", functionName, host, port);
        
        RegisterResponse response = new RegisterResponse();
        response.setTraceId(request.getTraceId());
        
        boolean registered = registry.registerService(functionName, host, port);
        if (registered) {
            logger.info("Function '{}' registered successfully at {}:{}", functionName, host, port);
            response.setStatus("success");
            response.setMessage("Function '" + functionName + "' registered successfully");
        } else {
            logger.info("Function '{}' already registered, updating to {}:{}", functionName, host, port);
            registry.updateService(functionName, host, port);
            response.setStatus("success");
            response.setMessage("Function '" + functionName + "' registration updated");
        }
        
        connection.sendMessage(response);
    }
    
    private void handleLookupRequest(ConnectionHandler connection, LookupRequest request) 
            throws ConnectionHandler.ConnectionException {
        String functionName = request.getFunctionName();
        
        logger.info("Received lookup request for function '{}'", functionName);
        
        LookupResponse response = new LookupResponse();
        response.setTraceId(request.getTraceId());
        
        ServiceRegistry.ServiceInfo serviceInfo = registry.lookupService(functionName);
        if (serviceInfo != null) {
            logger.info("Function '{}' found at {}:{}", functionName, serviceInfo.getHost(), serviceInfo.getPort());
            response.setStatus("success");
            response.setHost(serviceInfo.getHost());
            response.setPort(serviceInfo.getPort());
        } else {
            logger.warn("Function '{}' not found in registry", functionName);
            response.setStatus("error");
            response.setErrorMessage("Function '" + functionName + "' not found");
        }
        
        connection.sendMessage(response);
    }
    
    /**
     * Main method to start the registry server.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        RegistryServer registryServer = new RegistryServer();
        
        // Add shutdown hook to cleanly shutdown the server
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down registry server...");
            registryServer.stop();
        }));
        
        try {
            registryServer.start();
            logger.info("Registry server running on port {}. Press Ctrl+C to exit.", registryServer.getPort());
        } catch (IOException e) {
            logger.error("Failed to start registry server: {}", e.getMessage());
            System.exit(1);
        }
    }
}