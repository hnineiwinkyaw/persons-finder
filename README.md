## About the Project

This project implements the backend for a mobile application called **Persons Finder**, designed to help users locate people nearby based on their geographical location. The system exposes a REST API that allows clients to create and update persons’ profiles and their current locations, as well as search for people within a specified radius of a given location. 

## What Has Been Implemented

The project includes the following REST API endpoints, designed to meet the specified requirements:

#### POST /api/v1/persons  
Creates a new person record in the system. Accepts person details in the request body and returns the created person with a unique identifier.

#### PUT /api/v1/persons/{id}/location  
Updates the location data (latitude and longitude) for the specified person ID.

#### GET /api/v1/persons  
Retrieves one or more persons by their IDs. Supports multiple `id` query parameters to fetch a list of persons in a single request.

#### GET /api/v1/persons/nearby  
Finds persons within a specified radius (in kilometers) around a given latitude and longitude. Returns a list of persons sorted by their distance from the query location.

#### GET /api/v1/persons/seed  
An endpoint provided to seed the database with a configurable amount of sample person and location data, primarily intended for testing and benchmarking purposes.

All APIs can be explored in the Swagger UI after running the project: [Swagger UI](http://localhost:8080/swagger-ui/index.html#/)

## Development Highlights

- The application uses an H2 in-memory database.
- Comprehensive test cases have been implemented.
- The project has a high test coverage with 94% of instructions, 80% of branches, and 86% of lines covered, measured using [JaCoCo](https://www.jacoco.org/jacoco/) with reports available in the `toBeUpdated` directory.  
- Code quality and consistency are maintained using Spotless Kotlin formatting.  
- API contracts are defined upfront via interfaces for clarity and early alignment, allowing controllers to implement them without blocking collaborators or integrations.  
- Database performance is optimized by adding indexes on location-related columns to speed up queries.  
- A large-scale seeding test was performed, successfully query nearby persons from 1 million records in under 300 ms, demonstrating the system's scalability.  
- Exception handling is centralized with a global handler, ensuring that all errors are captured and responded to uniformly.  
- API responses are standardized and unified, with each transaction assigned a unique transaction ID to facilitate debugging and integration with upstream services.  
