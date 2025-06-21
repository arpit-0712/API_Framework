package com.restassured.framework.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Wrapper class for REST API responses with validation and utility methods
 */
public class ApiResponse {
    private static final Logger logger = LoggerFactory.getLogger(ApiResponse.class);
    private final Response response;
    private final ObjectMapper objectMapper;

    public ApiResponse(Response response) {
        this.response = response;
        this.objectMapper = new ObjectMapper();
    }

    public int getStatusCode() {
        return response.getStatusCode();
    }

    public String getBody() {
        return response.getBody().asString();
    }

    public Headers getHeaders() {
        return response.getHeaders();
    }

    public String getHeader(String headerName) {
        return response.getHeader(headerName);
    }

    public <T> T getBodyAs(Class<T> clazz) {
        try {
            return objectMapper.readValue(getBody(), clazz);
        } catch (JsonProcessingException e) {
            logger.error("Failed to deserialize response body to {}", clazz.getSimpleName(), e);
            throw new ApiException("Failed to deserialize response", e);
        }
    }

    public JsonNode getBodyAsJsonNode() {
        try {
            return objectMapper.readTree(getBody());
        } catch (JsonProcessingException e) {
            logger.error("Failed to parse response body as JSON", e);
            throw new ApiException("Failed to parse JSON response", e);
        }
    }

    public <T> T getValueByJsonPath(String jsonPath, Class<T> type) {
        try {
            return JsonPath.read(getBody(), jsonPath);
        } catch (Exception e) {
            logger.error("Failed to extract value using JSON path: {}", jsonPath, e);
            throw new ApiException("JSON path extraction failed", e);
        }
    }

    public String getStringValue(String jsonPath) {
        return getValueByJsonPath(jsonPath, String.class);
    }

    public Integer getIntValue(String jsonPath) {
        return getValueByJsonPath(jsonPath, Integer.class);
    }

    public Boolean getBooleanValue(String jsonPath) {
        return getValueByJsonPath(jsonPath, Boolean.class);
    }

    public List<Object> getListValue(String jsonPath) {
        return getValueByJsonPath(jsonPath, List.class);
    }

    public Map<String, Object> getMapValue(String jsonPath) {
        return getValueByJsonPath(jsonPath, Map.class);
    }

    public boolean hasKey(String jsonPath) {
        try {
            JsonPath.read(getBody(), jsonPath);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public ApiResponse assertStatusCode(int expectedStatusCode) {
        if (getStatusCode() != expectedStatusCode) {
            String errorMsg = String.format("Expected status code %d but got %d", expectedStatusCode, getStatusCode());
            logger.error(errorMsg);
            throw new AssertionError(errorMsg);
        }
        return this;
    }

    public ApiResponse assertStatusCodeInRange(int min, int max) {
        if (getStatusCode() < min || getStatusCode() > max) {
            String errorMsg = String.format("Expected status code between %d and %d but got %d", min, max, getStatusCode());
            logger.error(errorMsg);
            throw new AssertionError(errorMsg);
        }
        return this;
    }

    public ApiResponse assertBodyContains(String expectedText) {
        if (!getBody().contains(expectedText)) {
            String errorMsg = String.format("Response body does not contain expected text: %s", expectedText);
            logger.error(errorMsg);
            throw new AssertionError(errorMsg);
        }
        return this;
    }

    public ApiResponse assertJsonPathValue(String jsonPath, Object expectedValue) {
        Object actualValue = getValueByJsonPath(jsonPath, Object.class);
        if (!expectedValue.equals(actualValue)) {
            String errorMsg = String.format("Expected value at path '%s' to be '%s' but got '%s'", 
                jsonPath, expectedValue, actualValue);
            logger.error(errorMsg);
            throw new AssertionError(errorMsg);
        }
        return this;
    }

    public ApiResponse assertJsonPathExists(String jsonPath) {
        if (!hasKey(jsonPath)) {
            String errorMsg = String.format("JSON path '%s' does not exist in response", jsonPath);
            logger.error(errorMsg);
            throw new AssertionError(errorMsg);
        }
        return this;
    }

    public ApiResponse assertHeaderValue(String headerName, String expectedValue) {
        String actualValue = getHeader(headerName);
        if (!expectedValue.equals(actualValue)) {
            String errorMsg = String.format("Expected header '%s' to be '%s' but got '%s'", 
                headerName, expectedValue, actualValue);
            logger.error(errorMsg);
            throw new AssertionError(errorMsg);
        }
        return this;
    }

    public boolean isSuccess() {
        return getStatusCode() >= 200 && getStatusCode() < 300;
    }

    public boolean isClientError() {
        return getStatusCode() >= 400 && getStatusCode() < 500;
    }

    public boolean isServerError() {
        return getStatusCode() >= 500;
    }

    public Response getRawResponse() {
        return response;
    }

    @Override
    public String toString() {
        return String.format("ApiResponse{statusCode=%d, body='%s'}", getStatusCode(), getBody());
    }
} 