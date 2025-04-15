package com.rpc.lampcontrol.middleware.protocol;

import java.util.UUID;

/**
 * Base class for all RPC messages in the system.
 */
public abstract class Message {
    protected String version = "1.0";
    protected String type;
    protected String traceId;

    public Message() {
        this.traceId = UUID.randomUUID().toString();
    }

    public Message(String type) {
        this();
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}