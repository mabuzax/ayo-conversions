# Ayo Conversions Service

## Description
This project is a microservice designed to handle unit conversions. It allows users to define their conversion factors for different unit pairs and then use these factors to perform conversions.

The service supports operations like adding new conversion configurations, retrieving existing conversion configurations, updating existing conversion configurations, deleting conversion configurations, and performing conversions based on the defined configurations.
The application uses a memory DB that gets loaded with 3 initial conversion configs on startup, as there is no need to persist the data for a longer period.  

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

After running these steps, the service can be accessed at `http://localhost:8090`. ```You may use any port for this.```

## Database
Application uses a small memory DB as there is no requirement to persist the data longer than testing the application.

## System Logs
While for a production system it would be required to keep proper logs that are available at a later stage, for this exercise, the application is written to only demonstrate when logging would take place.

## API Testing

The `ayoTesting.postman_collection.json` file inside the `api_test_collection` directory contains a set of Postman tests for the service's endpoints.

## Security

Security is not currently implemented for the endpoints, as it was out of scope for this project. However, it is recommended that critical endpoints should be protected either with an API Key or require some form of user authentication for access.  Publicly available GET endpoints should implement some sort of rate limiting to prevent abuse.

## Business Front-end

A VueJS front-end for business self-service is provided in the ```ayo-conversions-frontend ``` repo. Businesses can add/modify and view existing conversion configurations using this front-end.

## Monitoring & Support

The application logs all system errors to make it easy for the support team to investigate incidents.  In a real application these would be kept in log files that can be retained for a specified period of time. Integration with other tools such as ```Senty, or/and Newrelic``` could also be used in a real world scenario to assist the support team.

The `/actuator/health`, `/actuator/info`, `/actuator/loggers` endpoints can be used by production support to monitor and support the system. Under normal circumstances, these endpoints would require authentication, but this requirement has been waived for this exercise.
