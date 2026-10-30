# Calculator DevOps Service

A Java 17 calculator service built with Maven and tested using JUnit 5.

## Build

Make sure Java 17 and Maven are installed.

```bash
mvn clean package
```

This creates the executable JAR:

```text
target/calculator-devops-1.0.0.jar
```

## Test

Run all automated tests:

```bash
mvn clean test
```

The project includes unit tests for the calculator operations and HTTP integration tests for the REST API.

## Run

Start the calculator service using:

```bash
java -jar target/calculator-devops-1.0.0.jar
```

The service runs on port `8081`.

## API

The API accepts `GET` requests with query parameters `a` and `b`.

### Addition

```bash
curl "http://localhost:8081/api/v1/add?a=10&b=20"
```

Response:

```json
{"operation":"add","a":10.0,"b":20.0,"result":30.0}
```

### Subtraction

```bash
curl "http://localhost:8081/api/v1/subtract?a=20&b=10"
```

### Multiplication

```bash
curl "http://localhost:8081/api/v1/multiply?a=6&b=7"
```

### Division

```bash
curl "http://localhost:8081/api/v1/divide?a=20&b=5"
```

### Health Check

```bash
curl "http://localhost:8081/actuator/health"
```

Expected response:

```json
{"status":"UP"}
```

## Docker Compose

The service can also be run using Docker Compose:

```bash
docker compose up -d
```

The API is then accessible through Nginx on port `8082`:

```bash
curl "http://localhost:8082/api/v1/add?a=10&b=20"
```

Stop the services with:

```bash
docker compose down
```