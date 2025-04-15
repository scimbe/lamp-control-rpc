package com.rpc.lampcontrol.application.client;

import com.rpc.lampcontrol.middleware.registry.RegistryClient;
import com.rpc.lampcontrol.middleware.rpc.RpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

/**
 * Main class for the lamp client application.
 * Provides a simple console interface to control a remote lamp.
 */
public class ClientMain {
    private static final Logger logger = LoggerFactory.getLogger(ClientMain.class);
    private static final String DEFAULT_REGISTRY_HOST = "localhost";
    private static final int DEFAULT_REGISTRY_PORT = 7777;
    
    public static void main(String[] args) {
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
        
        try {
            // Create registry client with specified host and port
            RegistryClient registryClient = new RegistryClient(registryHost, registryPort);
            
            // Create RPC client using the registry client
            RpcClient rpcClient = new RpcClient(registryClient);
            
            // Display welcome message
            System.out.println("==================================");
            System.out.println("  Lamp Control RPC Client");
            System.out.println("==================================");
            System.out.println("Registry: " + registryHost + ":" + registryPort);
            System.out.println("Commands:");
            System.out.println("  toggle - Toggle the lamp on/off");
            System.out.println("  status - Check lamp status");
            System.out.println("  exit   - Exit the application");
            System.out.println("==================================");
            
            // Start interactive console
            Scanner scanner = new Scanner(System.in);
            boolean running = true;
            
            while (running) {
                System.out.print("\nEnter command: ");
                String command = scanner.nextLine().trim().toLowerCase();
                
                try {
                    switch (command) {
                        case "toggle":
                            String result = (String) rpcClient.invoke("toggle");
                            System.out.println("Result: " + result);
                            break;
                            
                        case "status":
                            Boolean isOn = (Boolean) rpcClient.invoke("isOn");
                            System.out.println("Lamp is currently: " + (isOn ? "ON" : "OFF"));
                            break;
                            
                        case "exit":
                            System.out.println("Exiting...");
                            running = false;
                            break;
                            
                        default:
                            System.out.println("Unknown command. Available commands: toggle, status, exit");
                    }
                } catch (RpcClient.RpcException e) {
                    System.out.println("Error: " + e.getMessage());
                    logger.error("RPC error: {}", e.getMessage(), e);
                }
            }
            
        } catch (Exception e) {
            logger.error("Client error: {}", e.getMessage(), e);
            System.out.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}