package com.rpc.lampcontrol.application.server;

import com.rpc.lampcontrol.middleware.registry.RegistryClient;
import com.rpc.lampcontrol.middleware.rpc.RpcServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Main class for starting the lamp server.
 * This server exposes the lamp functionality as RPC services.
 */
public class ServerMain {
    private static final Logger logger = LoggerFactory.getLogger(ServerMain.class);
    private static final int DEFAULT_PORT = 0; // Use a dynamically assigned port
    private static final String DEFAULT_REGISTRY_HOST = "localhost";
    private static final int DEFAULT_REGISTRY_PORT = 7777;
    
    public static void main(String[] args) {
        int serverPort = DEFAULT_PORT;
        String registryHost = DEFAULT_REGISTRY_HOST;
        int registryPort = DEFAULT_REGISTRY_PORT;
        
        // Parse command-line arguments
        if (args.length > 0) {
            try {
                registryPort = Integer.parseInt(args[0]);
                logger.info("Using registry port: {}", registryPort);
            } catch (NumberFormatException e) {
                logger.warn("Invalid registry port '{}', using default: {}", args[0], DEFAULT_REGISTRY_PORT);
            }
        }
        
        if (args.length > 1) {
            registryHost = args[1];
            logger.info("Using registry host: {}", registryHost);
        }
        
        if (args.length > 2) {
            try {
                serverPort = Integer.parseInt(args[2]);
                logger.info("Using server port: {}", serverPort);
            } catch (NumberFormatException e) {
                logger.warn("Invalid server port '{}', using default: {}", args[2], DEFAULT_PORT);
            }
        }
        
        try {
            // Create registry client with specified host and port
            RegistryClient registryClient = new RegistryClient(registryHost, registryPort);
            
            // Create and start the RPC server with the custom registry client
            RpcServer rpcServer = new RpcServer(serverPort, "localhost", registryClient);
            rpcServer.start();
            
            // Create the lamp service
            LampImpl lamp = new LampImpl("Main Lamp");
            
            // Initialize the lamp GUI
            lamp.initializeGUI();
            
            // Register the lamp service with the RPC server
            rpcServer.registerFunction("toggle", lamp, "toggle");
            rpcServer.registerFunction("isOn", lamp, "isOn");
            
            logger.info("Lamp server started on port {}. Press Ctrl+C to exit.", rpcServer.getPort());
            
            // Add shutdown hook to cleanly shutdown the server
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Shutting down lamp server...");
                rpcServer.stop();
            }));
            
            // Keep the main thread alive
            Thread.currentThread().join();
            
        } catch (IOException e) {
            logger.error("Failed to start server: {}", e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            logger.error("Error registering lamp functions: {}", e.getMessage());
            System.exit(1);
        }
    }
}