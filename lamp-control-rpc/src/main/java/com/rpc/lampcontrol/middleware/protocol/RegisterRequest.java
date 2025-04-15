package com.rpc.lampcontrol.middleware.protocol;

/**
 * Represents a service registration request sent to the registry.
 */
public class RegisterRequest extends Message {
    private String functionName;
    private String host;
    private int port;

    public RegisterRequest() {
        super("register");
    }

    public RegisterRequest(String functionName, String host, int port) {
        this();
        this.functionName = functionName;
        this.host = host;
        this.port = port;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}