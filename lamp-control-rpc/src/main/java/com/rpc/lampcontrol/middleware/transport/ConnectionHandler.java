package com.rpc.lampcontrol.middleware.transport;

import com.rpc.lampcontrol.middleware.common.JsonSerializer;
import com.rpc.lampcontrol.middleware.protocol.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Handles socket-based communication for sending and receiving messages.
 */
public class ConnectionHandler implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionHandler.class);
    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;

    /**
     * Creates a new ConnectionHandler with the given socket.
     *
     * @param socket The socket to handle
     * @throws IOException if there's an error accessing the socket streams
     */
    public ConnectionHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        logger.info("Connection established with {}:{}", socket.getInetAddress().getHostAddress(), socket.getPort());
    }

    /**
     * Reads a raw message from the socket as a JSON string.
     *
     * @return The raw JSON message
     * @throws IOException if there's an error reading
     * @throws ConnectionException if the connection is closed
     */
    public String readRawMessage() throws IOException, ConnectionException {
        String json = in.readLine();
        if (json == null) {
            throw new ConnectionException("Received null message, connection may be closed");
        }
        logger.debug("Received raw message: {}", json);
        return json;
    }

    /**
     * Sends a message over the connection.
     *
     * @param message The message to send
     * @throws ConnectionException if sending fails
     */
    public void sendMessage(Message message) throws ConnectionException {
        try {
            String json = JsonSerializer.serialize(message);
            logger.debug("Sending message: {}", json);
            out.println(json);
            if (out.checkError()) {
                throw new ConnectionException("Error while sending message");
            }
        } catch (JsonSerializer.SerializationException e) {
            throw new ConnectionException("Failed to serialize message for sending", e);
        }
    }

    /**
     * Receives a message of the specified type.
     *
     * @param messageClass The expected message class
     * @param <T> Type parameter for the Message class
     * @return The received message
     * @throws ConnectionException if receiving fails or message is null
     */
    public <T extends Message> T receiveMessage(Class<T> messageClass) throws ConnectionException {
        try {
            String json = in.readLine();
            if (json == null) {
                throw new ConnectionException("Received null message, connection may be closed");
            }
            
            logger.debug("Received message: {}", json);
            return JsonSerializer.deserialize(json, messageClass);
        } catch (IOException e) {
            throw new ConnectionException("Error reading from socket", e);
        } catch (JsonSerializer.SerializationException e) {
            throw new ConnectionException("Failed to deserialize received message", e);
        }
    }

    /**
     * Closes the connection.
     */
    @Override
    public void close() {
        try {
            out.close();
            in.close();
            socket.close();
            logger.info("Connection closed with {}:{}", socket.getInetAddress().getHostAddress(), socket.getPort());
        } catch (IOException e) {
            logger.error("Error closing connection: {}", e.getMessage());
        }
    }

    /**
     * Gets the remote host address.
     *
     * @return the remote host address
     */
    public String getRemoteHost() {
        return socket.getInetAddress().getHostAddress();
    }

    /**
     * Gets the remote port.
     *
     * @return the remote port
     */
    public int getRemotePort() {
        return socket.getPort();
    }

    /**
     * Exception thrown when connection operations fail.
     */
    public static class ConnectionException extends Exception {
        public ConnectionException(String message) {
            super(message);
        }

        public ConnectionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}