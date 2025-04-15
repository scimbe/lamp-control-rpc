package com.rpc.lampcontrol.middleware.protocol;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Represents a service lookup response from the registry.
 */
public class LookupResponse extends Message {
    private String status;
    private String host;
    private int port;
    private String errorMessage;
    private Boolean success;  // Added to handle deserialization

    public LookupResponse() {
        super("lookupResponse");
        this.status = "success";
    }

    public static LookupResponse success(String host, int port) {
        LookupResponse response = new LookupResponse();
        response.setStatus("success");
        response.setHost(host);
        response.setPort(port);
        return response;
    }

    public static LookupResponse error(String errorMessage) {
        LookupResponse response = new LookupResponse();
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

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @JsonIgnore  // Don't serialize this method's return value
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