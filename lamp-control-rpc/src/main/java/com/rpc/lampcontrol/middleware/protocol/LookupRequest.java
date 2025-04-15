package com.rpc.lampcontrol.middleware.protocol;

/**
 * Represents a service lookup request sent to the registry.
 */
public class LookupRequest extends Message {
    private String functionName;

    public LookupRequest() {
        super("lookup");
    }

    public LookupRequest(String functionName) {
        this();
        this.functionName = functionName;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }
}