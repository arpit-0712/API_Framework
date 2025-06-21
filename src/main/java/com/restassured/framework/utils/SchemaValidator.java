package com.restassured.framework.utils;

import com.restassured.framework.core.ApiResponse;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for JSON schema validation
 */
public class SchemaValidator {
    private static final Logger logger = LoggerFactory.getLogger(SchemaValidator.class);

    /**
     * Validate response against JSON schema file
     */
    public static void validateResponseAgainstSchema(ApiResponse response, String schemaPath) {
        try {
            String schemaContent = DataUtils.readResourceAsString(schemaPath);
            validateResponseAgainstSchemaString(response, schemaContent);
            logger.info("Schema validation passed for schema: {}", schemaPath);
        } catch (Exception e) {
            logger.error("Schema validation failed for schema: {}", schemaPath, e);
            throw new RuntimeException("Schema validation failed", e);
        }
    }

    /**
     * Validate response against JSON schema string
     */
    public static void validateResponseAgainstSchemaString(ApiResponse response, String schemaString) {
        try {
            JsonSchemaValidator.matchesJsonSchema(schemaString)
                    .matches(response.getRawResponse());
            logger.info("Schema validation passed");
        } catch (Exception e) {
            logger.error("Schema validation failed", e);
            throw new RuntimeException("Schema validation failed", e);
        }
    }

    /**
     * Validate response against JSON schema with custom error message
     */
    public static void validateResponseAgainstSchema(ApiResponse response, String schemaPath, String errorMessage) {
        try {
            String schemaContent = DataUtils.readResourceAsString(schemaPath);
            JsonSchemaValidator.matchesJsonSchema(schemaContent)
                    .matches(response.getRawResponse());
            logger.info("Schema validation passed for schema: {}", schemaPath);
        } catch (Exception e) {
            logger.error("Schema validation failed for schema: {} - {}", schemaPath, errorMessage, e);
            throw new RuntimeException(errorMessage + ": " + e.getMessage(), e);
        }
    }

    /**
     * Validate that response matches a specific schema type
     */
    public static void validateResponseType(ApiResponse response, String expectedType) {
        try {
            switch (expectedType.toLowerCase()) {
                case "object":
                    validateObjectResponse(response);
                    break;
                case "array":
                    validateArrayResponse(response);
                    break;
                case "string":
                    validateStringResponse(response);
                    break;
                case "number":
                    validateNumberResponse(response);
                    break;
                case "boolean":
                    validateBooleanResponse(response);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported schema type: " + expectedType);
            }
            logger.info("Response type validation passed for type: {}", expectedType);
        } catch (Exception e) {
            logger.error("Response type validation failed for type: {}", expectedType, e);
            throw new RuntimeException("Response type validation failed", e);
        }
    }

    private static void validateObjectResponse(ApiResponse response) {
        String body = response.getBody().trim();
        if (!body.startsWith("{") || !body.endsWith("}")) {
            throw new RuntimeException("Response is not a valid JSON object");
        }
    }

    private static void validateArrayResponse(ApiResponse response) {
        String body = response.getBody().trim();
        if (!body.startsWith("[") || !body.endsWith("]")) {
            throw new RuntimeException("Response is not a valid JSON array");
        }
    }

    private static void validateStringResponse(ApiResponse response) {
        String body = response.getBody().trim();
        if (!body.startsWith("\"") || !body.endsWith("\"")) {
            throw new RuntimeException("Response is not a valid JSON string");
        }
    }

    private static void validateNumberResponse(ApiResponse response) {
        String body = response.getBody().trim();
        try {
            Double.parseDouble(body);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Response is not a valid JSON number");
        }
    }

    private static void validateBooleanResponse(ApiResponse response) {
        String body = response.getBody().trim();
        if (!body.equals("true") && !body.equals("false")) {
            throw new RuntimeException("Response is not a valid JSON boolean");
        }
    }

    /**
     * Validate that response contains required fields
     */
    public static void validateRequiredFields(ApiResponse response, String... requiredFields) {
        for (String field : requiredFields) {
            if (!response.hasKey(field)) {
                String errorMsg = "Required field '" + field + "' is missing in response";
                logger.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }
        }
        logger.info("Required fields validation passed");
    }

    /**
     * Validate that response field has expected data type
     */
    public static void validateFieldType(ApiResponse response, String fieldPath, String expectedType) {
        try {
            Object value = response.getValueByJsonPath(fieldPath, Object.class);
            validateValueType(value, expectedType);
            logger.info("Field type validation passed for field: {} (type: {})", fieldPath, expectedType);
        } catch (Exception e) {
            logger.error("Field type validation failed for field: {} (expected type: {})", fieldPath, expectedType, e);
            throw new RuntimeException("Field type validation failed", e);
        }
    }

    private static void validateValueType(Object value, String expectedType) {
        switch (expectedType.toLowerCase()) {
            case "string":
                if (!(value instanceof String)) {
                    throw new RuntimeException("Expected string but got: " + value.getClass().getSimpleName());
                }
                break;
            case "number":
                if (!(value instanceof Number)) {
                    throw new RuntimeException("Expected number but got: " + value.getClass().getSimpleName());
                }
                break;
            case "boolean":
                if (!(value instanceof Boolean)) {
                    throw new RuntimeException("Expected boolean but got: " + value.getClass().getSimpleName());
                }
                break;
            case "array":
                if (!(value instanceof java.util.List)) {
                    throw new RuntimeException("Expected array but got: " + value.getClass().getSimpleName());
                }
                break;
            case "object":
                if (!(value instanceof java.util.Map)) {
                    throw new RuntimeException("Expected object but got: " + value.getClass().getSimpleName());
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported type: " + expectedType);
        }
    }
} 