
# Auth Service

Authentication microservice for the SGSITS College Portal.

## Prerequisites

- Java 24
- Maven (via Maven Wrapper)
- MySQL

## Setup

1. Copy the template configuration:

```bash
cp src/main/resources/application.properties src/main/resources/application.properties
```

Windows:

```cmd
copy src\main\resources\application.properties.template src\main\resources\application.properties
```

2. Update `application.properties` with your database credentials and JWT secret.

3. Run the application:

```bash
./mvnw spring-boot:run
```

Windows:

```cmd
.\mvnw.cmd spring-boot:run
```

## API

- POST `/auth/login`
- POST `/auth/register`