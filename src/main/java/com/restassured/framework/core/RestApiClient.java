package com.restassured.framework.core;

import com.restassured.framework.config.ConfigManager;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Reusable REST API Client with comprehensive logging and error handling
 */
public class RestApiClient {
    private static final Logger logger = LoggerFactory.getLogger(RestApiClient.class);
    private final ConfigManager configManager;
    private final Map<String, String> defaultHeaders = new HashMap<>();

    public RestApiClient() {
        this.configManager = ConfigManager.getInstance();
    }

    /**
     * GET Request to endpoint
     */
    public ApiResponse get(String endpoint) {
        return get(endpoint, null, null);
    }

    /**
     * GET Request to endpoint with headers
     */
    public ApiResponse get(String endpoint, Map<String, String> headers) {
        return get(endpoint, headers, null);
    }

    /**
     * GET Request to endpoint with query parameters
     */
    public ApiResponse get(String endpoint, Map<String, String> headers, Map<String, ?> queryParams) {
        return get(endpoint, headers, queryParams, null, null, null);
    }

    /**
     * GET Request to endpoint with path parameters
     */
    public ApiResponse get(String endpoint, Map<String, String> headers, Map<String, ?> queryParams, Map<String, ?> pathParams, ContentType contentType, String accept) {
        return request(Method.GET, endpoint, headers, queryParams, null, pathParams, contentType, accept);
    }

    /**
     * POST Request to endpoint
     */
    public ApiResponse post(String endpoint, Object body) {
        return post(endpoint, body, null);
    }

    /**
     * POST Request to endpoint with headers
     */
    public ApiResponse post(String endpoint, Object body, Map<String, String> headers) {
        return post(endpoint, body, headers, null, null, null, null);
    }

    /**
     * POST Request to endpoint with query parameters
     */
    public ApiResponse post(String endpoint, Object body, Map<String, String> headers, Map<String, ?> queryParams, Map<String, ?> pathParams, ContentType contentType, String accept) {
        return request(Method.POST, endpoint, headers, queryParams, body, pathParams, contentType, accept);
    }

    /**
     * PUT Request to endpoint
     */
    public ApiResponse put(String endpoint, Object body) {
        return put(endpoint, body, null);
    }

    /**
     * PUT Request to endpoint with headers
     */
    public ApiResponse put(String endpoint, Object body, Map<String, String> headers) {
        return put(endpoint, body, headers, null, null, null, null);
    }

    /**
     * PUT Request to endpoint with query parameters
     */
    public ApiResponse put(String endpoint, Object body, Map<String, String> headers, Map<String, ?> queryParams, Map<String, ?> pathParams, ContentType contentType, String accept) {
        return request(Method.PUT, endpoint, headers, queryParams, body, pathParams, contentType, accept);
    }

    /**
     * DELETE Request to endpoint
     */
    public ApiResponse delete(String endpoint) {
        return delete(endpoint, null);
    }

    /**
     * DELETE Request to endpoint with headers
     */
    public ApiResponse delete(String endpoint, Map<String, String> headers) {
        return delete(endpoint, headers, null, null, null, null);
    }

    /**
     * DELETE Request to endpoint with query parameters
     */
    public ApiResponse delete(String endpoint, Map<String, String> headers, Map<String, ?> queryParams, Map<String, ?> pathParams, ContentType contentType, String accept) {
        return request(Method.DELETE, endpoint, headers, queryParams, null, pathParams, contentType, accept);
    }

    /**
     * PATCH Request to endpoint
     */
    public ApiResponse patch(String endpoint, Object body) {
        return patch(endpoint, body, null);
    }

    /**
     * PATCH Request to endpoint with headers
     */
    public ApiResponse patch(String endpoint, Object body, Map<String, String> headers) {
        return patch(endpoint, body, headers, null, null, null, null);
    }

    /**
     * PATCH Request to endpoint with query parameters
     */
    public ApiResponse patch(String endpoint, Object body, Map<String, String> headers, Map<String, ?> queryParams, Map<String, ?> pathParams, ContentType contentType, String accept) {
        return request(Method.PATCH, endpoint, headers, queryParams, body, pathParams, contentType, accept);
    }

    /**
     * OPTIONS Request to endpoint
     */
    public ApiResponse options(String endpoint) {
        return options(endpoint, null);
    }

    /**
     * OPTIONS Request to endpoint with headers
     */
    public ApiResponse options(String endpoint, Map<String, String> headers) {
        return options(endpoint, headers, null, null, null, null);
    }

    /**
     * OPTIONS Request to endpoint with query parameters
     */
    public ApiResponse options(String endpoint, Map<String, String> headers, Map<String, ?> queryParams, Map<String, ?> pathParams, ContentType contentType, String accept) {
        return request(Method.OPTIONS, endpoint, headers, queryParams, null, pathParams, contentType, accept);
    }

    private void logResponse(ApiResponse response) {
        logger.info("Response Status: {}", response.getStatusCode());
        logger.debug("Response Headers: {}", response.getHeaders());
        logger.debug("Response Body: {}", response.getBody());
        
        if (response.getStatusCode() >= 400) {
            logger.error("API request failed with status: {}", response.getStatusCode());
        }
    }

    public void addDefaultHeader(String name, String value) {
        defaultHeaders.put(name, value);
        logger.debug("Default header added: {} = {}", name, value);
    }

    public void addDefaultHeaders(Map<String, String> headers) {
        if (headers != null) {
            defaultHeaders.putAll(headers);
            logger.debug("Default headers added: {}", headers);
        }
    }

    private void applyDefaultHeaders(RequestSpecification request) {
        if (!defaultHeaders.isEmpty()) {
            request.headers(defaultHeaders);
        }
    }

    private ApiResponse request(Method method, String endpoint, Map<String, String> headers, Map<String, ?> queryParams, Object body, Map<String, ?> pathParams, ContentType contentType, String accept) {
        logger.info("Making {} request to: {}", method, endpoint);
        RequestSpecification request = RestAssured.given()
                .baseUri(configManager.getBaseUrl());

        // Content-Type
        if (contentType != null) {
            request.contentType(contentType);
        } else {
            request.contentType(ContentType.JSON);
        }
        // Accept
        if (accept != null) {
            request.accept(accept);
        }
        // Default headers
        applyDefaultHeaders(request);
        // Custom headers
        if (headers != null) {
            request.headers(headers);
            logger.debug("Request headers: {}", headers);
        }
        // Query params
        if (queryParams != null) {
            request.queryParams(queryParams);
            logger.debug("Query parameters: {}", queryParams);
        }
        // Path params
        if (pathParams != null) {
            request.pathParams(pathParams);
            logger.debug("Path parameters: {}", pathParams);
        }
        // Body
        if (body != null) {
            request.body(body);
            logger.debug("Request body: {}", body);
        }
        try {
            Response response = request.request(method, endpoint);
            ApiResponse apiResponse = new ApiResponse(response);
            logResponse(apiResponse);
            return apiResponse;
        } catch (Exception e) {
            logger.error("{} request failed for endpoint: {}", method, endpoint, e);
            throw new ApiException(method + " request failed", e);
        }
    }
} 