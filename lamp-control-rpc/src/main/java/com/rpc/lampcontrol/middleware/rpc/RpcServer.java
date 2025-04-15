package com.rpc.lampcontrol.middleware.rpc;

import com.rpc.lampcontrol.middleware.protocol.RpcRequest;
import com.rpc.lampcontrol.middleware.protocol.RpcResponse;
import com.rpc.lampcontrol.middleware.registry.RegistryClient;
import com.rpc.lampcontrol.middleware.transport.ConnectionHandler;
import com.rpc.lampcontrol.middleware.transport.ServerTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RPC server that handles remote procedure calls.
 */
public class RpcServer {
    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);
    
    private final int port;
    private final ServerTransport serverTransport;
    private final RegistryClient registryClient;
    private final Map<String, FunctionInfo> functions = new ConcurrentHashMap<>();
    private final String host;
    
    /**
     * Creates a new RPC server on a dynamically assigned port with localhost as host.
     */
    public RpcServer() {
        this(0, "localhost");
    }
    
    /**
     * Creates a new RPC server on the specified port with the specified host name.
     *
     * @param port The port to listen on, or 0 for a dynamically assigned port
     * @param host The host name or IP address to register with
     */
    public RpcServer(int port, String host) {
        this(port, host, new RegistryClient());
    }
    
    /**
     * Creates a new RPC server with a specific registry client.
     *
     * @param port The port to listen on, or 0 for a dynamically assigned port
     * @param host The host name or IP address to register with
     * @param registryClient The registry client to use
     */
    public RpcServer(int port, String host, RegistryClient registryClient) {
        this.port = port;
        this.host = host;
        this.serverTransport = new ServerTransport(port);
        this.registryClient = registryClient;
    }
    
    /**
     * Starts the RPC server.
     *
     * @throws IOException if the server fails to start
     */
    public void start() throws IOException {
        logger.info("Starting RPC server on port {}", port);
        serverTransport.start(this::handleConnection);
        
        int boundPort = serverTransport.getPort();
        logger.info("RPC server started on port {}", boundPort);
    }
    
    /**
     * Stops the RPC server.
     */
    public void stop() {
        logger.info("Stopping RPC server");
        serverTransport.stop();
        logger.info("RPC server stopped");
    }
    
    /**
     * Gets the port this server is bound to.
     *
     * @return the bound port
     */
    public int getPort() {
        return serverTransport.getPort();
    }
    
    /**
     * Registers a function with this server.
     *
     * @param functionName The name to register the function under
     * @param object The object containing the method to call
     * @param methodName The name of the method to call
     * @throws Exception if the method cannot be found or if registration fails
     */
    public void registerFunction(String functionName, Object object, String methodName) throws Exception {
        // Find the method
        Method method = findMethod(object.getClass(), methodName);
        if (method == null) {
            throw new IllegalArgumentException("Method '" + methodName + "' not found in " + object.getClass().getName());
        }
        
        // Register the function locally
        FunctionInfo functionInfo = new FunctionInfo(object, method);
        functions.put(functionName, functionInfo);
        logger.info("Registered function '{}' locally", functionName);
        
        // Register with the registry server
        int serverPort = serverTransport.getPort();
        if (serverPort == -1) {
            throw new IllegalStateException("Cannot register function: server not started");
        }
        
        registryClient.registerService(functionName, host, serverPort);
        logger.info("Registered function '{}' with registry at {}:{}", functionName, host, serverPort);
    }
    
    private Method findMethod(Class<?> clazz, String methodName) {
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }
    
    private void handleConnection(ConnectionHandler connection) {
        try {
            // Read request
            RpcRequest request = connection.receiveMessage(RpcRequest.class);
            String functionName = request.getFunctionName();
            List<Object> args = request.getArguments();
            
            logger.info("Received RPC request for function '{}' with {} arguments", 
                    functionName, args.size());
            
            // Process request
            RpcResponse response = processRequest(request);
            response.setTraceId(request.getTraceId());
            
            // Send response
            connection.sendMessage(response);
            
            logger.info("Sent RPC response for function '{}'", functionName);
        } catch (ConnectionHandler.ConnectionException e) {
            logger.error("Error handling RPC connection: {}", e.getMessage());
        } finally {
            connection.close();
        }
    }
    
    private RpcResponse processRequest(RpcRequest request) {
        String functionName = request.getFunctionName();
        List<Object> args = request.getArguments();
        
        // Look up the function
        FunctionInfo functionInfo = functions.get(functionName);
        if (functionInfo == null) {
            logger.warn("Function '{}' not found", functionName);
            return RpcResponse.error("Function '" + functionName + "' not found");
        }
        
        try {
            // Invoke the function
            Object result = functionInfo.method.invoke(functionInfo.object, args.toArray());
            logger.info("Function '{}' invoked successfully", functionName);
            return RpcResponse.success(result);
        } catch (IllegalAccessException e) {
            logger.error("Access error invoking function '{}': {}", functionName, e.getMessage());
            return RpcResponse.error("Access error: " + e.getMessage());
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            logger.error("Error invoking function '{}': {}", functionName, cause.getMessage());
            return RpcResponse.error("Invocation error: " + cause.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error invoking function '{}': {}", functionName, e.getMessage());
            return RpcResponse.error("Unexpected error: " + e.getMessage());
        }
    }
    
    /**
     * Information about a registered function.
     */
    private static class FunctionInfo {
        private final Object object;
        private final Method method;
        
        public FunctionInfo(Object object, Method method) {
            this.object = object;
            this.method = method;
        }
    }
}