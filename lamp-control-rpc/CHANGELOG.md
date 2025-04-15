# Changelog

All notable changes to the Lamp Control RPC project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Light bulb GUI representation for the Lamp Server showing the lamp's on/off state
- Visual feedback via graphical bulb with dynamic state changes (yellow glow when on, gray when off)
- Real-time synchronization between the lamp state and visual representation

## [1.0.0] - 2025-04-15

### Added
- Initial project setup with Maven structure
- RPC infrastructure with client, server, and registry components
- JSON-based communication protocol
- Client application with command-line interface
- Server application with lamp functionality
- Registry service for function registration and discovery
- Tracing system with unique IDs for request tracking
- Logging with SLF4J and Logback
- Error handling and recovery mechanisms

### Fixed
- JSON deserialization issues with the `RpcResponse` class
- Proper handling of the "success" status field in response objects
- Fixed LookupResponse and RegisterResponse deserialization problems

## Project Setup - 2025-04-15

### Added
- Maven project structure with standard directories
- Core packages for application, middleware, and transport layers
- Socket-based transport implementation
- RPC client and server implementation
- Registry system for service discovery
- Basic remote procedure call capability
- Lamp interface and implementation
- Unit tests for core functionality
- Multi-module build configuration with assembly plugin
- Executable JARs for client, server, and registry components

### Technical Details
- Java 17 support
- SLF4J for logging abstraction
- Logback for logging implementation
- Jackson for JSON serialization/deserialization
- JUnit for testing
- Maven for build automation and dependency management
