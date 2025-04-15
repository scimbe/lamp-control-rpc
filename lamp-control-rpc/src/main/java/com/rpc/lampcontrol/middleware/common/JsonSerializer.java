package com.rpc.lampcontrol.middleware.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.rpc.lampcontrol.middleware.protocol.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles serialization and deserialization of messages to/from JSON format.
 */
public class JsonSerializer {
    private static final Logger logger = LoggerFactory.getLogger(JsonSerializer.class);
    private static final ObjectMapper objectMapper = createObjectMapper();
    private static final Map<String, Class<? extends Message>> MESSAGE_TYPES = createMessageTypes();

    private static ObjectMapper createObjectMapper() {
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(Message.class)
                .build();
        
        return new ObjectMapper();
    }

    private static Map<String, Class<? extends Message>> createMessageTypes() {
        Map<String, Class<? extends Message>> types = new HashMap<>();
        types.put("register", RegisterRequest.class);
        types.put("registerResponse", RegisterResponse.class);
        types.put("lookup", LookupRequest.class);
        types.put("lookupResponse", LookupResponse.class);
        types.put("request", RpcRequest.class);
        types.put("response", RpcResponse.class);
        return types;
    }

    /**
     * Serializes a Message object to JSON string.
     *
     * @param message The message to serialize
     * @return JSON string representation
     * @throws SerializationException if serialization fails
     */
    public static String serialize(Message message) throws SerializationException {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize message: {}", e.getMessage());
            throw new SerializationException("Failed to serialize message", e);
        }
    }

    /**
     * Deserializes a JSON string to a Message object.
     * 
     * @param json JSON string to deserialize
     * @return Deserialized Message object
     * @throws SerializationException if deserialization fails
     */
    public static Message deserialize(String json) throws SerializationException {
        try {
            // First, parse to get type
            Map<String, Object> map = objectMapper.readValue(json, Map.class);
            String type = (String) map.get("type");
            
            if (type == null) {
                throw new SerializationException("Message missing 'type' field", null);
            }
            
            Class<? extends Message> messageClass = MESSAGE_TYPES.get(type);
            if (messageClass == null) {
                throw new SerializationException("Unknown message type: " + type, null);
            }
            
            return objectMapper.readValue(json, messageClass);
        } catch (IOException e) {
            logger.error("Failed to deserialize message: {}", e.getMessage());
            throw new SerializationException("Failed to deserialize message", e);
        }
    }

    /**
     * Deserializes a JSON string to a Message object of the specified type.
     *
     * @param json JSON string to deserialize
     * @param messageClass The target Message class
     * @param <T> Type parameter for the Message class
     * @return Deserialized Message object
     * @throws SerializationException if deserialization fails
     */
    public static <T extends Message> T deserialize(String json, Class<T> messageClass) throws SerializationException {
        try {
            return objectMapper.readValue(json, messageClass);
        } catch (IOException e) {
            logger.error("Failed to deserialize message: {}", e.getMessage());
            throw new SerializationException("Failed to deserialize message", e);
        }
    }

    /**
     * Exception thrown when serialization or deserialization fails.
     */
    public static class SerializationException extends Exception {
        public SerializationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}