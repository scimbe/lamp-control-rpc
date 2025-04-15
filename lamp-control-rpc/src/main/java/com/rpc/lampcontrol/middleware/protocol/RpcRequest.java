package com.rpc.lampcontrol.middleware.protocol;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an RPC request from client to server.
 */
public class RpcRequest extends Message {
    private String functionName;
    private List<Object> arguments;

    public RpcRequest() {
        super("request");
        this.arguments = new ArrayList<>();
    }

    public RpcRequest(String functionName) {
        this();
        this.functionName = functionName;
    }

    public RpcRequest(String functionName, List<Object> arguments) {
        this(functionName);
        this.arguments = arguments;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public List<Object> getArguments() {
        return arguments;
    }

    public void setArguments(List<Object> arguments) {
        this.arguments = arguments;
    }

    public void addArgument(Object argument) {
        this.arguments.add(argument);
    }
}