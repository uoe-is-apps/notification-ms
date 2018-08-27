# Notification Microservice

This project is the back-end component for the University of Edinburgh's Notification Hub.  It is
developed with Spring Boot 1.4.

## Running this Project

You can run the `notification-ms` project locally (for development) or in a server environment
(TEST, PROD, etc.)

### Running `notification-ms` Locally

Use the following command to run `notification-ms` locally with the Spring Boot Maven Plugin:

```
$ mvn spring-boot:run
```

[Spring Boot Maven Plugin documentation][]

## Configuration

### Database Connection Settings

The `notification-ms` component requires a database connection.  Enter standard JDBC/JPA connection
settings in `application.properties`.


[Spring Boot Maven Plugin documentation]: https://docs.spring.io/spring-boot/docs/current/reference/html/using-boot-running-your-application.html#using-boot-running-with-the-maven-plugin
