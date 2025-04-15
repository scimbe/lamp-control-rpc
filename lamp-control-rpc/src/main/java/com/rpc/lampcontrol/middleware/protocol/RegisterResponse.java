package com.rpc.lampcontrol.middleware.protocol;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Represents a service registration response from the registry.
 */
public class RegisterResponse extends Message {
    private String status;
    private String message;
    private Boolean success;  // Added to handle deserialization

    public RegisterResponse() {
        super("registerResponse");
        this.status = "success";
    }

    public static RegisterResponse success(String message) {
        RegisterResponse response = new RegisterResponse();
        response.setStatus("success");
        response.setMessage(message);
        return response;
    }

    public static RegisterResponse error(String message) {
        RegisterResponse response = new RegisterResponse();
        response.setStatus("error");
        response.setMessage(message);
        return response;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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