# Ayo Conversions Service

## Description
This project is a microservice designed to handle unit conversions. It allows users to define their conversion factors for different unit pairs and then use these factors to perform conversions.

The service supports operations like adding new conversion configurations, retrieving existing conversion configurations, updating existing conversion configurations, deleting conversion configurations, and performing conversions based on the defined configurations.

This repo is strictly for the backend of the application.  A VueJS front-end can be found in the  ```ayo-conversions-frontend ``` repo       

## Docker Build & Run Instructions

To build and run the project in a Docker container, use the following steps:

1. Build the Docker image from the Dockerfile:

    ```
    docker build -t ayo-conversions .
    ```

2. Run the Docker container on port 8090:

    ```
    docker run -p 8090:8090 ayo-conversions
    ```

After running these steps, the service can be accessed at `http://localhost:8090`.

## API Testing

The `ayoTesting.postman_collection.json` file inside the `api_test_collection` directory contains a set of Postman tests for the service's endpoints.

## Security

Security is not currently implemented for the endpoint, as it was out of scope for this project. However, it is recommended that sensitive endpoints should be protected. Also, GET endpoints should implement some sort of rate limiting to prevent abuse.

## Business Front-end

A VueJS front-end for business self-service is provided in the ```ayo-conversions-frontend ``` repo. Businesses can add/modify and view existing conversion configurations using this front-end.

## Monitoring & Support

The `/actuator/health`, `/actuator/info`, `/actuator/loggers` endpoints can be used by production support to monitor and support the system. Under normal circumstances, these endpoints would require authentication, but this requirement has been waived for this exercise.
