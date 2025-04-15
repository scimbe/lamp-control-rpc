package com.rpc.lampcontrol.middleware.rpc;

import com.rpc.lampcontrol.middleware.protocol.RpcRequest;
import com.rpc.lampcontrol.middleware.protocol.RpcResponse;
import com.rpc.lampcontrol.middleware.registry.RegistryClient;
import com.rpc.lampcontrol.middleware.transport.ClientTransport;
import com.rpc.lampcontrol.middleware.transport.ConnectionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * RPC client for invoking remote functions.
 */
public class RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);
    
    private final RegistryClient registryClient;
    private final ClientTransport transport;
    
    /**
     * Creates a new RPC client with default registry settings.
     */
    public RpcClient() {
        this(new RegistryClient());
    }
    
    /**
     * Creates a new RPC client with a specific registry client.
     *
     * @param registryClient The registry client to use for service lookup
     */
    public RpcClient(RegistryClient registryClient) {
        this.registryClient = registryClient;
        this.transport = new ClientTransport();
    }
    
    /**
     * Invokes a remote function with no arguments.
     *
     * @param functionName The name of the function to invoke
     * @return The result of the function call
     * @throws RpcException if there is an error invoking the function
     */
    public Object invoke(String functionName) throws RpcException {
        return invoke(functionName, new ArrayList<>());
    }
    
    /**
     * Invokes a remote function with the specified arguments.
     *
     * @param functionName The name of the function to invoke
     * @param args The arguments to pass to the function
     * @return The result of the function call
     * @throws RpcException if there is an error invoking the function
     */
    public Object invoke(String functionName, List<Object> args) throws RpcException {
        try {
            // Look up service in registry
            var lookupResponse = registryClient.lookupService(functionName);
            
            if (!lookupResponse.isSuccess()) {
                throw new RpcException("Function not found: " + lookupResponse.getErrorMessage());
            }
            
            String host = lookupResponse.getHost();
            int port = lookupResponse.getPort();
            
            logger.info("Invoking function '{}' at {}:{}", functionName, host, port);
            
            // Prepare request
            RpcRequest request = new RpcRequest(functionName, args);
            
            // Send request and receive response
            try (ConnectionHandler connection = transport.connect(host, port)) {
                connection.sendMessage(request);
                RpcResponse response = connection.receiveMessage(RpcResponse.class);
                
                // Check for success
                if (response.isSuccess()) {
                    logger.info("Function '{}' invoked successfully", functionName);
                    return response.getResult();
                } else {
                    logger.error("Function '{}' invocation failed: {}", functionName, response.getErrorMessage());
                    throw new RpcException("Function invocation failed: " + response.getErrorMessage());
                }
            }
        } catch (IOException e) {
            logger.error("Network error invoking function: {}", e.getMessage());
            throw new RpcException("Network error: " + e.getMessage(), e);
        } catch (ConnectionHandler.ConnectionException e) {
            logger.error("Communication error invoking function: {}", e.getMessage());
            throw new RpcException("Communication error: " + e.getMessage(), e);
        } catch (RegistryClient.RegistryException e) {
            logger.error("Registry error looking up function: {}", e.getMessage());
            throw new RpcException("Registry error: " + e.getMessage(), e);
        }
    }
    
    /**
     * Exception thrown when there is an error invoking a remote function.
     */
    public static class RpcException extends Exception {
        public RpcException(String message) {
            super(message);
        }
        
        public RpcException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}