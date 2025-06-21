package com.restassured.framework.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for data management and file operations
 */
public class DataUtils {
    private static final Logger logger = LoggerFactory.getLogger(DataUtils.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Read JSON file and return as JsonNode
     */
    public static JsonNode readJsonFile(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                logger.error("File not found: {}", filePath);
                throw new RuntimeException("File not found: " + filePath);
            }
            return objectMapper.readTree(file);
        } catch (IOException e) {
            logger.error("Failed to read JSON file: {}", filePath, e);
            throw new RuntimeException("Failed to read JSON file", e);
        }
    }

    /**
     * Read JSON file from resources and return as JsonNode
     */
    public static JsonNode readJsonFromResources(String resourcePath) {
        try (InputStream inputStream = DataUtils.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                logger.error("Resource not found: {}", resourcePath);
                throw new RuntimeException("Resource not found: " + resourcePath);
            }
            return objectMapper.readTree(inputStream);
        } catch (IOException e) {
            logger.error("Failed to read JSON from resources: {}", resourcePath, e);
            throw new RuntimeException("Failed to read JSON from resources", e);
        }
    }

    /**
     * Read file content as string
     */
    public static String readFileAsString(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            logger.error("Failed to read file as string: {}", filePath, e);
            throw new RuntimeException("Failed to read file as string", e);
        }
    }

    /**
     * Read file content from resources as string
     */
    public static String readResourceAsString(String resourcePath) {
        try (InputStream inputStream = DataUtils.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                logger.error("Resource not found: {}", resourcePath);
                throw new RuntimeException("Resource not found: " + resourcePath);
            }
            return new String(inputStream.readAllBytes());
        } catch (IOException e) {
            logger.error("Failed to read resource as string: {}", resourcePath, e);
            throw new RuntimeException("Failed to read resource as string", e);
        }
    }

    /**
     * Convert object to JSON string
     */
    public static String toJsonString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (IOException e) {
            logger.error("Failed to convert object to JSON string", e);
            throw new RuntimeException("Failed to convert object to JSON string", e);
        }
    }

    /**
     * Convert JSON string to object
     */
    public static <T> T fromJsonString(String jsonString, Class<T> clazz) {
        try {
            return objectMapper.readValue(jsonString, clazz);
        } catch (IOException e) {
            logger.error("Failed to convert JSON string to object", e);
            throw new RuntimeException("Failed to convert JSON string to object", e);
        }
    }

    /**
     * Create test data map
     */
    public static Map<String, Object> createTestData(String... keyValuePairs) {
        Map<String, Object> data = new HashMap<>();
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            if (i + 1 < keyValuePairs.length) {
                data.put(keyValuePairs[i], keyValuePairs[i + 1]);
            }
        }
        return data;
    }

    /**
     * Generate random string
     */
    public static String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    /**
     * Generate random email
     */
    public static String generateRandomEmail() {
        return generateRandomString(8) + "@example.com";
    }

    /**
     * Generate random number between min and max
     */
    public static int generateRandomNumber(int min, int max) {
        return min + (int) (Math.random() * (max - min + 1));
    }

    /**
     * Check if file exists
     */
    public static boolean fileExists(String filePath) {
        return new File(filePath).exists();
    }

    /**
     * Check if resource exists
     */
    public static boolean resourceExists(String resourcePath) {
        return DataUtils.class.getClassLoader().getResource(resourcePath) != null;
    }
} 