package com.rpc.lampcontrol.middleware.registry;

import com.rpc.lampcontrol.middleware.protocol.*;
import com.rpc.lampcontrol.middleware.transport.ClientTransport;
import com.rpc.lampcontrol.middleware.transport.ConnectionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Client for interacting with the Registry server.
 */
public class RegistryClient {
    private static final Logger logger = LoggerFactory.getLogger(RegistryClient.class);
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 7777;
    
    private final String registryHost;
    private final int registryPort;
    private final ClientTransport transport;
    
    /**
     * Creates a new RegistryClient using default host and port.
     */
    public RegistryClient() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }
    
    /**
     * Creates a new RegistryClient with the specified registry host and port.
     *
     * @param registryHost The registry host
     * @param registryPort The registry port
     */
    public RegistryClient(String registryHost, int registryPort) {
        this.registryHost = registryHost;
        this.registryPort = registryPort;
        this.transport = new ClientTransport();
        logger.info("RegistryClient configured to use registry at {}:{}", registryHost, registryPort);
    }
    
    /**
     * Gets the host of the registry server.
     *
     * @return the registry host
     */
    public String getRegistryHost() {
        return registryHost;
    }
    
    /**
     * Gets the port of the registry server.
     *
     * @return the registry port
     */
    public int getRegistryPort() {
        return registryPort;
    }
    
    /**
     * Registers a service with the registry.
     *
     * @param functionName The name of the function to register
     * @param host The host where the function is available
     * @param port The port where the function is available
     * @return The registry response
     * @throws RegistryException if there is an error communicating with the registry
     */
    public RegisterResponse registerService(String functionName, String host, int port) throws RegistryException {
        logger.info("Registering service '{}' at {}:{} with registry at {}:{}", 
            functionName, host, port, registryHost, registryPort);
        
        RegisterRequest request = new RegisterRequest(functionName, host, port);
        
        try (ConnectionHandler connection = transport.connect(registryHost, registryPort)) {
            connection.sendMessage(request);
            RegisterResponse response = connection.receiveMessage(RegisterResponse.class);
            
            if (response.isSuccess()) {
                logger.info("Service '{}' registered successfully: {}", functionName, response.getMessage());
            } else {
                logger.warn("Failed to register service '{}': {}", functionName, response.getMessage());
            }
            
            return response;
        } catch (IOException | ConnectionHandler.ConnectionException e) {
            logger.error("Error registering service: {}", e.getMessage());
            throw new RegistryException("Failed to register service: " + e.getMessage(), e);
        }
    }
    
    /**
     * Looks up a service in the registry.
     *
     * @param functionName The name of the function to look up
     * @return The lookup response
     * @throws RegistryException if there is an error communicating with the registry
     */
    public LookupResponse lookupService(String functionName) throws RegistryException {
        logger.info("Looking up service '{}' in registry at {}:{}", 
            functionName, registryHost, registryPort);
        
        LookupRequest request = new LookupRequest(functionName);
        
        try (ConnectionHandler connection = transport.connect(registryHost, registryPort)) {
            connection.sendMessage(request);
            LookupResponse response = connection.receiveMessage(LookupResponse.class);
            
            if (response.isSuccess()) {
                logger.info("Service '{}' found at {}:{}", functionName, response.getHost(), response.getPort());
            } else {
                logger.warn("Service '{}' not found: {}", functionName, response.getErrorMessage());
            }
            
            return response;
        } catch (IOException | ConnectionHandler.ConnectionException e) {
            logger.error("Error looking up service: {}", e.getMessage());
            throw new RegistryException("Failed to look up service: " + e.getMessage(), e);
        }
    }
    
    /**
     * Exception thrown when there is an error communicating with the registry.
     */
    public static class RegistryException extends Exception {
        public RegistryException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}