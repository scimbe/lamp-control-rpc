# Self-Made RPC Lamp Control System

This project demonstrates a self-made Remote Procedure Call (RPC) implementation for controlling a simulated lamp. The system consists of three main components:

1. **Registry Server**: Service discovery system that allows clients to find servers
2. **Lamp Server**: Exposes lamp functionality through RPC
3. **Lamp Client**: User interface to control the lamp remotely

## Architecture

The system is built using a middleware approach with several layers:

- **Transport Layer**: Handles network communication between components
- **Protocol Layer**: Defines message formats for RPC communication
- **Registry Layer**: Manages service registration and lookup
- **RPC Layer**: Provides high-level abstractions for remote procedure calls

## Running the Application

### Step 1: Start the Registry Server

```bash
java -jar target/registry-server-jar-with-dependencies.jar
```

This will start the registry server on the default port (7777).

### Step 2: Start the Lamp Server

```bash
java -jar target/lamp-server-jar-with-dependencies.jar
```

The lamp server will start, register its services with the registry, and listen for client connections.

### Step 3: Start the Lamp Client

```bash
java -jar target/lamp-client-jar-with-dependencies.jar
```

This will launch the client application with a simple console interface to control the lamp.

## Features

- **Dynamic Service Discovery**: Clients can discover services at runtime
- **Extensible System**: Easy to add new services to the RPC framework
- **Robust Communication**: Error handling and tracing for reliable messaging
- **Scalable Architecture**: Components can run on different machines

## Customization

If you need to run components on different hosts or ports, you can modify the source code:

- **Registry Server**: Edit `RegistryLauncher.java` to change default port
- **Lamp Server**: Edit `ServerMain.java` to change host/port settings
- **Lamp Client**: Edit `ClientMain.java` to point to a specific registry server

## Implementation Details

The implementation includes:

- JSON-based message serialization
- Socket-based network communication
- Thread-pooled request handling
- Asynchronous messaging
- Service registration and lookup