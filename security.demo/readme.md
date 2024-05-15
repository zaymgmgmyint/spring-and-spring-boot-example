# Spring Interceptors Demo

This demo project illustrates the usage of Spring's interceptor, including filters, HandlerInterceptor, and OncePerRequestFilter.

## Overview

In web applications, interceptors are used to perform pre-processing and post-processing tasks on HTTP requests and responses. 

This demo includes three types of interceptors provided by Spring:

1. **Servlet Filter**: A standard Java Servlet API used to intercept requests before they reach the servlet and responses before they are sent back to the client.

2. **HandlerInterceptor**: A Spring-specific interceptor that operates within the Spring MVC framework, allowing for more fine-grained control over request processing.

3. **OncePerRequestFilter**: A Spring-specific filter that ensures it is executed only once per request, addressing scenarios where multiple invocations of a filter may occur.

## Components

### 1. Servlet Filter

The `RequestResponseLoggingFilter` class demonstrates the usage of a servlet filter. It intercepts requests and responses, performing custom logic.

### 2. HandlerInterceptor

The `LoggingInterceptor` class showcases the implementation of a HandlerInterceptor. It provides preHandle, postHandle, and afterCompletion methods to intercept requests before they are handled by a controller, after the controller method is executed, and after the response is sent back to the client, respectively.

### 3. OncePerRequestFilter

The `AuthenticationFilter` class extends the OncePerRequestFilter provided by Spring. It ensures that the filter logic is executed only once per request, regardless of how many times the request is forwarded or dispatched.

## Usage

To run this demo project:

1. Clone the repository to your local machine.
2. Open the project in your preferred IDE.
3. Build and run the project using Maven.
4. Access the endpoints defined in the controllers (`HomeController`, for example) to observe the interceptor behavior.

## Dependencies

This project requires the following dependencies:

- Spring Boot
- Spring Web
- Other dependencies may be included based on specific requirement
