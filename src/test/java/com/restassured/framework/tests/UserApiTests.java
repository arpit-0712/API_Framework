package com.restassured.framework.tests;

import com.restassured.framework.base.BaseTest;
import com.restassured.framework.core.ApiResponse;
import com.restassured.framework.utils.DataUtils;
import com.restassured.framework.utils.SchemaValidator;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Sample test class demonstrating the REST API testing framework
 */
public class UserApiTests extends BaseTest {

    @Test(description = "Get all users")
    public void testGetAllUsers() {
        logTestInfo("testGetAllUsers", "Get all users from the API");
        
        // Make API call
        ApiResponse response = apiClient.get("/users");
        
        // Validate response
        validateSuccessResponse(response);
        response.assertStatusCode(200);
        
        // Validate response structure
        SchemaValidator.validateResponseType(response, "array");
        
        // Get the array and validate first element has required fields
        List<Object> users = response.getListValue("$");
        assert users.size() > 0 : "Expected at least one user in the response";
        
        // Validate that the first user has required fields
        response.assertJsonPathExists("$[0].id");
        response.assertJsonPathExists("$[0].name");
        response.assertJsonPathExists("$[0].email");
        
        logger.info("Successfully retrieved {} users", users.size());
    }

    @Test(description = "Get user by ID")
    public void testGetUserById() {
        logTestInfo("testGetUserById", "Get user by ID from the API");
        
        int userId = 1;
        
        // Make API call
        ApiResponse response = apiClient.get("/users/" + userId);
        
        // Validate response
        validateSuccessResponse(response);
        response.assertStatusCode(200);
        
        // Validate specific user data
        response.assertJsonPathValue("id", userId);
        response.assertJsonPathValue("name", "Leanne Graham");
        
        // Validate schema
        SchemaValidator.validateResponseAgainstSchema(response, "schemas/user-schema.json");
        
        logger.info("Successfully retrieved user with ID: {}", userId);
    }

    @Test(description = "Create new user")
    public void testCreateUser() {
        logTestInfo("testCreateUser", "Create a new user via API");
        
        // Prepare test data
        Map<String, Object> userData = DataUtils.createTestData(
            "name", "Test User",
            "username", "testuser",
            "email", "test.user@example.com",
            "phone", "123-456-7890"
        );
        
        // Make API call
        ApiResponse response = apiClient.post("/users", userData);
        
        // Validate response
        validateSuccessResponse(response);
        response.assertStatusCode(201);
        
        // Validate created user data
        response.assertJsonPathValue("name", "Test User");
        response.assertJsonPathValue("username", "testuser");
        response.assertJsonPathValue("email", "test.user@example.com");
        
        // Validate that ID is generated
        response.assertJsonPathExists("id");
        
        logger.info("Successfully created user with ID: {}", response.getIntValue("id"));
    }

    @Test(description = "Update user")
    public void testUpdateUser() {
        logTestInfo("testUpdateUser", "Update user information via API");
        
        int userId = 1;
        Map<String, Object> updateData = DataUtils.createTestData(
            "name", "Updated User",
            "email", "updated.user@example.com"
        );
        
        // Make API call
        ApiResponse response = apiClient.put("/users/" + userId, updateData);
        
        // Validate response
        validateSuccessResponse(response);
        response.assertStatusCode(200);
        
        // Validate updated data
        response.assertJsonPathValue("id", userId);
        response.assertJsonPathValue("name", "Updated User");
        response.assertJsonPathValue("email", "updated.user@example.com");
        
        logger.info("Successfully updated user with ID: {}", userId);
    }

    @Test(description = "Delete user")
    public void testDeleteUser() {
        logTestInfo("testDeleteUser", "Delete user via API");
        
        int userId = 1;
        
        // Make API call
        ApiResponse response = apiClient.delete("/users/" + userId);
        
        // Validate response
        response.assertStatusCode(200);
        
        logger.info("Successfully deleted user with ID: {}", userId);
    }

    @Test(description = "Get user with invalid ID")
    public void testGetUserWithInvalidId() {
        logTestInfo("testGetUserWithInvalidId", "Test error handling for invalid user ID");
        
        int invalidUserId = 999999;
        
        // Make API call
        ApiResponse response = apiClient.get("/users/" + invalidUserId);
        
        // Validate error response
        validateErrorResponse(response, 404);
        
        logger.info("Correctly handled invalid user ID: {}", invalidUserId);
    }

    @Test(description = "Create user with invalid data")
    public void testCreateUserWithInvalidData() {
        logTestInfo("testCreateUserWithInvalidData", "Test validation for invalid user data");
        
        // Prepare invalid test data
        Map<String, Object> invalidData = DataUtils.createTestData(
            "name", "",
            "email", "invalid-email",
            "phone", "invalid-phone"
        );
        
        // Make API call
        ApiResponse response = apiClient.post("/users", invalidData);
        
        // Validate error response (assuming API returns 400 for invalid data)
        validateErrorResponse(response, 400);
        
        logger.info("Correctly handled invalid user data");
    }

    @Test(description = "Get users with query parameters")
    public void testGetUsersWithQueryParams() {
        logTestInfo("testGetUsersWithQueryParams", "Get users with query parameters");
        
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("_limit", "5");
        queryParams.put("_start", "0");
        
        // Make API call
        ApiResponse response = apiClient.get("/users", null, queryParams);
        
        // Validate response
        validateSuccessResponse(response);
        response.assertStatusCode(200);
        
        // Validate that we got limited results
        int userCount = response.getListValue("$").size();
        assert userCount <= 5 : "Expected maximum 5 users, but got " + userCount;
        
        logger.info("Successfully retrieved {} users with query parameters", userCount);
    }
} 