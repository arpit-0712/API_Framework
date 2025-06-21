package com.restassured.framework.constants;

/**
 * API Constants for the testing framework
 */
public class ApiConstants {
    
    // HTTP Methods
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
    public static final String PATCH = "PATCH";
    
    // HTTP Status Codes
    public static final int OK = 200;
    public static final int CREATED = 201;
    public static final int NO_CONTENT = 204;
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int METHOD_NOT_ALLOWED = 405;
    public static final int CONFLICT = 409;
    public static final int UNPROCESSABLE_ENTITY = 422;
    public static final int INTERNAL_SERVER_ERROR = 500;
    public static final int BAD_GATEWAY = 502;
    public static final int SERVICE_UNAVAILABLE = 503;
    
    // Content Types
    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_XML = "application/xml";
    public static final String TEXT_PLAIN = "text/plain";
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    
    // Common Headers
    public static final String AUTHORIZATION = "Authorization";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String ACCEPT = "Accept";
    public static final String USER_AGENT = "User-Agent";
    public static final String CACHE_CONTROL = "Cache-Control";
    public static final String X_REQUESTED_WITH = "X-Requested-With";
    
    // Authorization Types
    public static final String BEARER = "Bearer";
    public static final String BASIC = "Basic";
    public static final String API_KEY = "ApiKey";
    
    // Common Endpoints (for JSONPlaceholder API)
    public static final String USERS_ENDPOINT = "/users";
    public static final String POSTS_ENDPOINT = "/posts";
    public static final String COMMENTS_ENDPOINT = "/comments";
    public static final String ALBUMS_ENDPOINT = "/albums";
    public static final String PHOTOS_ENDPOINT = "/photos";
    public static final String TODOS_ENDPOINT = "/todos";
    
    // Query Parameters
    public static final String LIMIT_PARAM = "_limit";
    public static final String START_PARAM = "_start";
    public static final String PAGE_PARAM = "_page";
    public static final String SORT_PARAM = "_sort";
    public static final String ORDER_PARAM = "_order";
    
    // Default Values
    public static final int DEFAULT_TIMEOUT = 30000;
    public static final int DEFAULT_CONNECTION_TIMEOUT = 10000;
    public static final int DEFAULT_READ_TIMEOUT = 30000;
    public static final int DEFAULT_MOCK_PORT = 8080;
    
    // Log Messages
    public static final String REQUEST_STARTED = "API Request Started";
    public static final String REQUEST_COMPLETED = "API Request Completed";
    public static final String REQUEST_FAILED = "API Request Failed";
    public static final String VALIDATION_PASSED = "Validation Passed";
    public static final String VALIDATION_FAILED = "Validation Failed";
    
    // Error Messages
    public static final String INVALID_RESPONSE_STATUS = "Invalid response status code";
    public static final String MISSING_REQUIRED_FIELD = "Missing required field";
    public static final String INVALID_FIELD_TYPE = "Invalid field type";
    public static final String SCHEMA_VALIDATION_FAILED = "Schema validation failed";
    public static final String TIMEOUT_ERROR = "Request timeout";
    public static final String CONNECTION_ERROR = "Connection error";
    
    // Test Data Keys
    public static final String VALID_USER = "validUser";
    public static final String NEW_USER = "newUser";
    public static final String INVALID_USER = "invalidUser";
    public static final String TEST_USER = "testUser";
    
    // Schema Paths
    public static final String USER_SCHEMA = "schemas/user-schema.json";
    public static final String POST_SCHEMA = "schemas/post-schema.json";
    public static final String COMMENT_SCHEMA = "schemas/comment-schema.json";
    
    // Mock Response Templates
    public static final String MOCK_USER_RESPONSE = "{\"id\": 1, \"name\": \"Mock User\", \"email\": \"mock@example.com\"}";
    public static final String MOCK_ERROR_RESPONSE = "{\"error\": \"Resource not found\", \"code\": 404}";
    public static final String MOCK_SUCCESS_RESPONSE = "{\"status\": \"success\", \"message\": \"Operation completed\"}";
    
    // Test Groups
    public static final String SMOKE = "smoke";
    public static final String REGRESSION = "regression";
    public static final String API = "api";
    public static final String MOCK = "mock";
    public static final String WIP = "wip";
    
    // Environment Names
    public static final String QA = "qa";
    public static final String STAGING = "staging";
    public static final String PRODUCTION = "production";
    public static final String DEV = "dev";
    
    // File Paths
    public static final String LOGS_DIRECTORY = "logs";
    public static final String TEST_DATA_PATH = "src/test/resources/testdata";
    public static final String SCHEMA_PATH = "src/test/resources/schemas";
    public static final String CONFIG_PATH = "src/test/resources/config";
    
    // Private constructor to prevent instantiation
    private ApiConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
} 