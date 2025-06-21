package com.restassured.framework.tests;

import com.restassured.framework.base.BaseTest;
import com.restassured.framework.core.ApiResponse;
import com.restassured.framework.utils.DataUtils;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Test class demonstrating API mocking capabilities
 */
public class MockApiTests extends BaseTest {

    @Test(description = "Test GET endpoint with mock")
    public void testMockGetEndpoint() {
        logTestInfo("testMockGetEndpoint", "Test GET endpoint with mock response");
        
        // Setup mock response
        String mockResponse = "{\"id\": 1, \"name\": \"Mock User\", \"email\": \"mock@example.com\"}";
        mockServer.mockGet("/mock/users/1", 200, mockResponse);
        
        // Make API call to mocked endpoint
        ApiResponse response = apiClient.get("/mock/users/1");
        
        // Validate response
        validateSuccessResponse(response);
        response.assertStatusCode(200);
        response.assertJsonPathValue("id", 1);
        response.assertJsonPathValue("name", "Mock User");
        response.assertJsonPathValue("email", "mock@example.com");
        
        logger.info("Successfully tested mocked GET endpoint");
    }

    @Test(description = "Test POST endpoint with mock")
    public void testMockPostEndpoint() {
        logTestInfo("testMockPostEndpoint", "Test POST endpoint with mock response");
        
        // Setup mock response
        String mockResponse = "{\"id\": 999, \"name\": \"Created User\", \"status\": \"created\"}";
        mockServer.mockPost("/mock/users", 201, mockResponse);
        
        // Prepare test data
        Map<String, Object> userData = DataUtils.createTestData(
            "name", "Test User",
            "email", "test@example.com"
        );
        
        // Make API call to mocked endpoint
        ApiResponse response = apiClient.post("/mock/users", userData);
        
        // Validate response
        response.assertStatusCode(201);
        response.assertJsonPathValue("id", 999);
        response.assertJsonPathValue("name", "Created User");
        response.assertJsonPathValue("status", "created");
        
        logger.info("Successfully tested mocked POST endpoint");
    }

    @Test(description = "Test PUT endpoint with mock")
    public void testMockPutEndpoint() {
        logTestInfo("testMockPutEndpoint", "Test PUT endpoint with mock response");
        
        // Setup mock response
        String mockResponse = "{\"id\": 1, \"name\": \"Updated User\", \"status\": \"updated\"}";
        mockServer.mockPut("/mock/users/1", 200, mockResponse);
        
        // Prepare update data
        Map<String, Object> updateData = DataUtils.createTestData(
            "name", "Updated User"
        );
        
        // Make API call to mocked endpoint
        ApiResponse response = apiClient.put("/mock/users/1", updateData);
        
        // Validate response
        validateSuccessResponse(response);
        response.assertStatusCode(200);
        response.assertJsonPathValue("id", 1);
        response.assertJsonPathValue("name", "Updated User");
        response.assertJsonPathValue("status", "updated");
        
        logger.info("Successfully tested mocked PUT endpoint");
    }

    @Test(description = "Test DELETE endpoint with mock")
    public void testMockDeleteEndpoint() {
        logTestInfo("testMockDeleteEndpoint", "Test DELETE endpoint with mock response");
        
        // Setup mock response
        String mockResponse = "{\"status\": \"deleted\", \"message\": \"User deleted successfully\"}";
        mockServer.mockDelete("/mock/users/1", 200, mockResponse);
        
        // Make API call to mocked endpoint
        ApiResponse response = apiClient.delete("/mock/users/1");
        
        // Validate response
        response.assertStatusCode(200);
        response.assertJsonPathValue("status", "deleted");
        response.assertJsonPathValue("message", "User deleted successfully");
        
        logger.info("Successfully tested mocked DELETE endpoint");
    }

    @Test(description = "Test mock with custom headers")
    public void testMockWithCustomHeaders() {
        logTestInfo("testMockWithCustomHeaders", "Test mock with custom response headers");
        
        // Setup mock response with custom headers
        String mockResponse = "{\"data\": \"header test\"}";
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Custom-Header", "custom-value");
        headers.put("Content-Type", "application/json");
        
        mockServer.mockGet("/mock/headers", 200, mockResponse, headers);
        
        // Make API call to mocked endpoint
        ApiResponse response = apiClient.get("/mock/headers");
        
        // Validate response
        validateSuccessResponse(response);
        response.assertStatusCode(200);
        response.assertHeaderValue("X-Custom-Header", "custom-value");
        response.assertJsonPathValue("data", "header test");
        
        logger.info("Successfully tested mock with custom headers");
    }

    @Test(description = "Test mock with delay")
    public void testMockWithDelay() {
        logTestInfo("testMockWithDelay", "Test mock with response delay");
        
        // Setup mock response with delay
        String mockResponse = "{\"message\": \"delayed response\"}";
        mockServer.mockWithDelay("GET", "/mock/delay", 200, mockResponse, 2000); // 2 second delay
        
        long startTime = System.currentTimeMillis();
        
        // Make API call to mocked endpoint
        ApiResponse response = apiClient.get("/mock/delay");
        
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;
        
        // Validate response
        validateSuccessResponse(response);
        response.assertStatusCode(200);
        response.assertJsonPathValue("message", "delayed response");
        
        // Verify delay was applied (allowing some tolerance)
        assert responseTime >= 1800 : "Expected delay of at least 1800ms, but got " + responseTime + "ms";
        
        logger.info("Successfully tested mock with delay. Response time: {}ms", responseTime);
    }

    @Test(description = "Test mock with error response")
    public void testMockErrorResponse() {
        logTestInfo("testMockErrorResponse", "Test mock with error response");
        
        // Setup mock error response
        String errorResponse = "{\"error\": \"User not found\", \"code\": 404}";
        mockServer.mockGet("/mock/error", 404, errorResponse);
        
        // Make API call to mocked endpoint
        ApiResponse response = apiClient.get("/mock/error");
        
        // Validate error response
        validateErrorResponse(response, 404);
        response.assertJsonPathValue("error", "User not found");
        response.assertJsonPathValue("code", 404);
        
        logger.info("Successfully tested mock error response");
    }

    @Test(description = "Test mock with JSON body matching")
    public void testMockWithBodyMatching() {
        logTestInfo("testMockWithBodyMatching", "Test mock with JSON body matching");
        
        // Setup mock with body matching
        String requestBodyPattern = "$.name";
        String mockResponse = "{\"status\": \"matched\", \"received\": \"name field present\"}";
        mockServer.mockWithBodyMatching("POST", "/mock/match", requestBodyPattern, 200, mockResponse);
        
        // Prepare test data
        Map<String, Object> testData = DataUtils.createTestData("name", "Test User");
        
        // Make API call to mocked endpoint
        ApiResponse response = apiClient.post("/mock/match", testData);
        
        // Validate response
        validateSuccessResponse(response);
        response.assertStatusCode(200);
        response.assertJsonPathValue("status", "matched");
        
        logger.info("Successfully tested mock with body matching");
    }
} 