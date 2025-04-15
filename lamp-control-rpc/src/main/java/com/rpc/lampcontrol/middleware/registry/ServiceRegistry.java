package com.rpc.lampcontrol.middleware.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service registry that stores information about available services.
 */
public class ServiceRegistry {
    private final Map<String, ServiceInfo> services = new ConcurrentHashMap<>();

    /**
     * Registers a service with the registry.
     *
     * @param functionName The name of the function to register
     * @param host The host where the function is available
     * @param port The port where the function is available
     * @return true if registration was successful, false if it already existed
     */
    public boolean registerService(String functionName, String host, int port) {
        ServiceInfo newService = new ServiceInfo(host, port);
        return services.putIfAbsent(functionName, newService) == null;
    }

    /**
     * Updates an existing service registration.
     *
     * @param functionName The name of the function to update
     * @param host The new host
     * @param port The new port
     * @return true if update was successful, false if service wasn't registered
     */
    public boolean updateService(String functionName, String host, int port) {
        ServiceInfo newService = new ServiceInfo(host, port);
        return services.replace(functionName, newService) != null;
    }

    /**
     * Unregisters a service from the registry.
     *
     * @param functionName The name of the function to unregister
     * @return true if unregistration was successful, false if service wasn't registered
     */
    public boolean unregisterService(String functionName) {
        return services.remove(functionName) != null;
    }

    /**
     * Looks up a service in the registry.
     *
     * @param functionName The name of the function to look up
     * @return ServiceInfo containing host and port, or null if not found
     */
    public ServiceInfo lookupService(String functionName) {
        return services.get(functionName);
    }

    /**
     * Gets the number of registered services.
     *
     * @return the number of registered services
     */
    public int getServiceCount() {
        return services.size();
    }

    /**
     * Clears all registered services.
     */
    public void clear() {
        services.clear();
    }

    /**
     * Represents information about a registered service.
     */
    public static class ServiceInfo {
        private final String host;
        private final int port;

        public ServiceInfo(String host, int port) {
            this.host = host;
            this.port = port;
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }

        @Override
        public String toString() {
            return host + ":" + port;
        }
    }
}