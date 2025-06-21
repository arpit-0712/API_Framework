package com.restassured.framework.core;

/**
 * Custom exception for API-related errors
 */
public class ApiException extends RuntimeException {
    
    public ApiException(String message) {
        super(message);
    }
    
    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ApiException(Throwable cause) {
        super(cause);
    }
} 