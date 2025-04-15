package com.rpc.lampcontrol.middleware.protocol;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Represents an RPC response from server to client.
 */
public class RpcResponse extends Message {
    private String status;
    private Object result;
    private String errorMessage;
    private Boolean success; // Added to handle deserialization

    public RpcResponse() {
        super("response");
        this.status = "success";
    }

    public RpcResponse(Object result) {
        this();
        this.result = result;
    }

    public static RpcResponse success(Object result) {
        RpcResponse response = new RpcResponse();
        response.setStatus("success");
        response.setResult(result);
        return response;
    }

    public static RpcResponse error(String errorMessage) {
        RpcResponse response = new RpcResponse();
        response.setStatus("error");
        response.setErrorMessage(errorMessage);
        return response;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @JsonIgnore // Don't serialize this method's return value
    public boolean isSuccess() {
        return "success".equals(status);
    }

    // Added for JSON deserialization
    public Boolean getSuccess() {
        return isSuccess();
    }

    // Added for JSON deserialization
    public void setSuccess(Boolean success) {
        // We don't need to store this, as we use status for the actual state
        // This is just for deserialization compatibility
    }
}