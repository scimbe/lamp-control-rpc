package com.rpc.lampcontrol.middleware.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Launcher for the Registry Server.
 * This class provides a simple way to start the registry server as a standalone application.
 */
public class RegistryLauncher {
    private static final Logger logger = LoggerFactory.getLogger(RegistryLauncher.class);
    
    public static void main(String[] args) {
        int port = 7777; // Default port
        
        // Parse port from command line arguments if provided
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                logger.warn("Invalid port number provided: {}. Using default port: {}", args[0], port);
            }
        }
        
        RegistryServer registryServer = new RegistryServer(port);
        
        // Add shutdown hook to cleanly shutdown the server
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down registry server...");
            registryServer.stop();
        }));
        
        try {
            registryServer.start();
            logger.info("Registry server running on port {}. Press Ctrl+C to exit.", registryServer.getPort());
            
            // Keep the main thread alive
            Thread.currentThread().join();
            
        } catch (Exception e) {
            logger.error("Failed to start registry server: {}", e.getMessage());
            System.exit(1);
        }
    }
}