# HexaDEVenture

![Spring Boot](https://img.shields.io/badge/Spring_Boot-79BD25.svg?style=flat&logo=Spring&logoColor=white)
![Unity](https://img.shields.io/badge/Unity-FFFFFF.svg?style=flat&logo=Unity&logoColor=black)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-336791.svg?style=flat&logo=PostgreSQL&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-001D2A.svg?style=flat&logo=MongoDB&logoColor=05BD4E)
![Docker](https://img.shields.io/badge/Docker-2496ED.svg?style=flat&logo=Docker&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-2088FF.svg?style=flat&logo=GitHub-Actions&logoColor=white)

---

## Table of Contents

- [Overview](#overview)  
- [Features](#features)  
- [Architecture](#architecture)  
- [Getting Started](#getting-started)  
  - [Prerequisites](#prerequisites)  
  - [Installation](#installation)  
  - [Configuration](#configuration)  
  - [Running the Application](#running-the-application)  
  - [Testing](#testing)  
- [License](#license)  
- [Acknowledgments](#acknowledgments)  

---

## Overview

**HexaDEVenture** is a project that showcases **Hexagonal Architecture (Ports & Adapters)** in a realistic and moderately complex environment. It consists of two services developed in **Java Spring Boot**: a **turn-based strategy game** and a **statistics microservice** communicating via HTTP. 

The system uses **multiple output ports/adapters**, including multi-database persistence (PostgreSQL and MongoDB), the JNoise library, an A* pathfinding adapter, and a HTTP communication adapter. It also features a Unity-based frontend fully integrated with the game service, an easy deployment using Docker, and includes extensive automated tests with approximately **90% code coverage**.

## Features

- Complex Hexagonal Architecture (Ports & Adapters).  
- Two independent services: Game (Main Service) and Statistics microservice.  
- Strict layer separation: Adapter, Application, Domain, Bootstrap.  
- Multi-database support: PostgreSQL and MongoDB in each service.
- Multiple output adapters.
- Basic Security with Spring Security.     
- Automated testing with high coverage.  
- Containerized deployment with Docker.  
- Swagger documentation for both services.
- Fully integrated Unity frontend.

## Architecture

The project is structured following the Hexagonal Architecture (Ports & Adapters) pattern, which is divided into four layers: **Adapter**, **Application**, **Domain**, and **Bootstrap**. The repository contains two main services:

### 1. Main Service (Game)

- **Adapter Layer**: Contains REST controllers and other output services implementations.
- **Application Layer**: Manages the business logic and use cases.
- **Domain Layer**: Contains the domain models.
- **Bootstrap Layer**: Responsible for initializing the application.

### 2. Statistics Service (Microservice)

- **Adapter Layer**: Includes REST controllers and HTTP client adapters for communication with the Main Service.
- **Application Layer**: Manages the business logic and use cases.
- **Domain Layer**: Contains the domain models.
- **Bootstrap Layer**: Responsible for initializing the application.

Both services are designed to be independently deployable, ensuring a clear separation of concerns and maintainability. However, since the statistics service depends on the main game service to function, it automatically launches the main service as a separate Docker Container to enable communication and preserve functionality.

## Getting Started

### Prerequisites

- Java 22+
- Maven 3.9+  
- Docker & Docker Compose (Docker Desktop)
- Unity (Optional, for frontend)
- IntelliJ IDEA (Recommended IDE)

### Setup

1. Clone the repository:  
    ```sh
    git clone https://github.com/Zarpyk/HexaDEVenture.git
    cd HexaDEVenture
    ```

2. Build all modules with Maven:  
    ```sh
    mvn clean package -DskipTests
    ```

### Configuration

- Database connection settings can be change on the `docker-compose.yaml`.
  - For PostgreSQL: On the main service use `SPRING_PROFILES_ACTIVE=jpa` environment variable and use PostgreSQL container.
  - For MongoDB: On the main service use `SPRING_PROFILES_ACTIVE=mongo` environment variable and use MongoDB container.

### Running the Application
Run on the root folder
```sh
docker-compose up
```

### Testing

Execute all automated tests:

```sh
mvn test
```

## License

This project is licensed under the **Commons Clause** on top of the Apache License 2.0.  
See [LICENSE](LICENSE) for details.

## Acknowledgments

- Based on references from [Sven Woltmann’s project](https://github.com/SvenWoltmann/hexagonal-architecture-java).  

---
