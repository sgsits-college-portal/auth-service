
# Auth Service

Authentication microservice for the SGSITS College Portal.

## Prerequisites

- Java 21
- Gradle
- MySQL

## Setup

1. Copy the template configuration:

```bash
cp src/main/resources/application.properties.template src/main/resources/application.properties
```

Windows:

```cmd
copy src\main\resources\application.properties.template src\main\resources\application.properties
```

2. Update `application.properties` with your database credentials and JWT secret.

3. Run the application:

```bash
./gradlew bootRun
```

## API

- POST `/auth/login`
- POST `/auth/register`