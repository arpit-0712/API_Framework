package com.restassured.framework.mocks;

import com.restassured.framework.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Simplified API Mock Server for API mocking capabilities
 * Note: This is a placeholder implementation. For full WireMock functionality,
 * ensure WireMock dependencies are properly resolved in pom.xml
 */
public class ApiMockServer {
    private static final Logger logger = LoggerFactory.getLogger(ApiMockServer.class);
    private final ConfigManager configManager;
    private final int port;
    private final String baseUrl;
    private boolean isRunning = false;

    public ApiMockServer() {
        this.configManager = ConfigManager.getInstance();
        this.port = configManager.getIntProperty("mock.server.port", 8080);
        this.baseUrl = "http://localhost:" + port;
    }

    public ApiMockServer(int port) {
        this.configManager = ConfigManager.getInstance();
        this.port = port;
        this.baseUrl = "http://localhost:" + port;
    }

    /**
     * Start Mock Server
     */
    public void start() {
        if (isRunning) {
            logger.warn("Mock server is already running on port {}", port);
            return;
        }

        // Placeholder for WireMock server start
        logger.info("Mock server started on port: {}", port);
        logger.info("Mock server base URL: {}", baseUrl);
        logger.warn("WireMock functionality is not available. This is a placeholder implementation.");
        isRunning = true;
    }

    /**
     * Stop Mock Server
     */
    public void stop() {
        if (isRunning) {
            logger.info("Mock server stopped");
            isRunning = false;
        }
    }

    /**
     * Reset Mock Server
     */
    public void reset() {
        if (isRunning) {
            logger.info("Mock server reset");
        }
    }

    /**
     * Mock GET endpoint: {endpoint}
     */
    public void mockGet(String endpoint, int statusCode, String responseBody) {
        mockGet(endpoint, statusCode, responseBody, new HashMap<>());
    }

    /**
     * Mock GET endpoint: {endpoint} with headers
     */
    public void mockGet(String endpoint, int statusCode, String responseBody, Map<String, String> headers) {
        logger.info("Mocked GET {} -> Status: {}, Body: {}", endpoint, statusCode, responseBody);
        logger.warn("WireMock functionality is not available. This is a placeholder implementation.");
    }

    /**
     * Mock POST endpoint: {endpoint}
     */
    public void mockPost(String endpoint, int statusCode, String responseBody) {
        mockPost(endpoint, statusCode, responseBody, new HashMap<>());
    }

    /**
     * Mock POST endpoint: {endpoint} with headers
     */
    public void mockPost(String endpoint, int statusCode, String responseBody, Map<String, String> headers) {
        logger.info("Mocked POST {} -> Status: {}, Body: {}", endpoint, statusCode, responseBody);
        logger.warn("WireMock functionality is not available. This is a placeholder implementation.");
    }

    /**
     * Mock PUT endpoint: {endpoint}
     */
    public void mockPut(String endpoint, int statusCode, String responseBody) {
        mockPut(endpoint, statusCode, responseBody, new HashMap<>());
    }

    /**
     * Mock PUT endpoint: {endpoint} with headers
     */
    public void mockPut(String endpoint, int statusCode, String responseBody, Map<String, String> headers) {
        logger.info("Mocked PUT {} -> Status: {}, Body: {}", endpoint, statusCode, responseBody);
        logger.warn("WireMock functionality is not available. This is a placeholder implementation.");
    }

    /**
     * Mock DELETE endpoint: {endpoint}
     */
    public void mockDelete(String endpoint, int statusCode, String responseBody) {
        mockDelete(endpoint, statusCode, responseBody, new HashMap<>());
    }

    /**
     * Mock DELETE endpoint: {endpoint} with headers
     */
    public void mockDelete(String endpoint, int statusCode, String responseBody, Map<String, String> headers) {
        logger.info("Mocked DELETE {} -> Status: {}, Body: {}", endpoint, statusCode, responseBody);
        logger.warn("WireMock functionality is not available. This is a placeholder implementation.");
    }

    /**
     * Mock endpoint with delay: {endpoint}
     */
    public void mockWithDelay(String method, String endpoint, int statusCode, String responseBody, int delayMs) {
        logger.info("Mocked {} {} with {}ms delay -> Status: {}", method, endpoint, delayMs, statusCode);
        logger.warn("WireMock functionality is not available. This is a placeholder implementation.");
    }

    /**
     * Mock endpoint with JSON body matching
     */
    public void mockWithBodyMatching(String method, String endpoint, String requestBodyPattern, int statusCode, String responseBody) {
        logger.info("Mocked {} {} with body matching -> Status: {}", method, endpoint, statusCode);
        logger.warn("WireMock functionality is not available. This is a placeholder implementation.");
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public int getPort() {
        return port;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public Object getWireMockServer() {
        logger.warn("WireMock server is not available in this implementation");
        return null;
    }
} 