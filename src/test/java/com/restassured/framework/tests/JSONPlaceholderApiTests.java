package com.restassured.framework.tests;

import com.restassured.framework.base.BaseTest;
import com.restassured.framework.core.ApiResponse;
import io.restassured.http.ContentType;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Sample test class demonstrating the reusable framework with JSONPlaceholder API
 * https://jsonplaceholder.typicode.com/
 */
public class JSONPlaceholderApiTests extends BaseTest {

    @Test(description = "Get all posts")
    public void testGetAllPosts() {
        logStep("Starting test: Get all posts from JSONPlaceholder API");
        ApiResponse response = apiClient.get("/posts");
        logStep("Made GET request to /posts endpoint");
        validateSuccessResponse(response);
        response.assertStatusCode(200);
        logStep("Verified response status code is 200");
        assert response.getListValue("$").size() > 0 : "Expected at least one post";
        logStep("Verified response contains at least one post");
        logStep("Test completed successfully: Retrieved " + response.getListValue("$").size() + " posts");
    }

    @Test(description = "Get post by ID using path param")
    public void testGetPostById() {
        logStep("Starting test: Get post by ID using path parameter");
        Map<String, Object> pathParams = new HashMap<>();
        pathParams.put("id", 1);
        logStep("Set path parameter: id = 1");
        ApiResponse response = apiClient.get("/posts/{id}", null, null, pathParams, null, null);
        logStep("Made GET request to /posts/{id} endpoint with path parameter");
        validateSuccessResponse(response);
        response.assertStatusCode(200);
        logStep("Verified response status code is 200");
        response.assertJsonPathValue("id", 1);
        logStep("Verified response contains post with ID = 1");
        logStep("Test completed successfully: Retrieved post with ID 1");
    }

    @Test(description = "Get posts with query params")
    public void testGetPostsWithQueryParams() {
        logStep("Starting test: Get posts with query parameters");
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("userId", "1");
        logStep("Set query parameter: userId = 1");
        ApiResponse response = apiClient.get("/posts", null, queryParams);
        logStep("Made GET request to /posts endpoint with query parameter");
        validateSuccessResponse(response);
        response.assertStatusCode(200);
        logStep("Verified response status code is 200");
        assert response.getListValue("$").size() > 0 : "Expected at least one post for userId=1";
        logStep("Verified response contains posts for userId=1");
        logStep("Test completed successfully: Retrieved " + response.getListValue("$").size() + " posts for userId=1");
    }

    @Test(description = "Create a new post (POST)")
    public void testCreatePost() {
        logStep("Starting test: Create a new post using POST method");
        Map<String, Object> postData = new HashMap<>();
        postData.put("title", "foo");
        postData.put("body", "bar");
        postData.put("userId", 1);
        logStep("Prepared post data: title='foo', body='bar', userId=1");
        ApiResponse response = apiClient.post("/posts", postData, null, null, null, ContentType.JSON, null);
        logStep("Made POST request to /posts endpoint with JSON data");
        validateSuccessResponse(response);
        response.assertStatusCode(201);
        logStep("Verified response status code is 201 (Created)");
        response.assertJsonPathValue("title", "foo");
        logStep("Verified response contains correct title: 'foo'");
        logStep("Test completed successfully: Created new post with ID " + response.getIntValue("id"));
    }

    @Test(description = "Update a post (PUT)")
    public void testUpdatePost() {
        logStep("Starting test: Update a post using PUT method");
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("id", 1);
        updateData.put("title", "updated");
        updateData.put("body", "updated body");
        updateData.put("userId", 1);
        logStep("Prepared update data: title='updated', body='updated body', userId=1");
        Map<String, Object> pathParams = new HashMap<>();
        pathParams.put("id", 1);
        logStep("Set path parameter: id = 1");
        ApiResponse response = apiClient.put("/posts/{id}", updateData, null, null, pathParams, ContentType.JSON, null);
        logStep("Made PUT request to /posts/{id} endpoint with updated data");
        validateSuccessResponse(response);
        response.assertStatusCode(200);
        logStep("Verified response status code is 200");
        response.assertJsonPathValue("title", "updated");
        logStep("Verified response contains updated title: 'updated'");
        logStep("Test completed successfully: Updated post with ID 1");
    }

    @Test(description = "Patch a post (PATCH)")
    public void testPatchPost() {
        logStep("Starting test: Patch a post using PATCH method");
        Map<String, Object> patchData = new HashMap<>();
        patchData.put("title", "patched title");
        logStep("Prepared patch data: title='patched title'");
        Map<String, Object> pathParams = new HashMap<>();
        pathParams.put("id", 1);
        logStep("Set path parameter: id = 1");
        ApiResponse response = apiClient.patch("/posts/{id}", patchData, null, null, pathParams, ContentType.JSON, null);
        logStep("Made PATCH request to /posts/{id} endpoint with patch data");
        validateSuccessResponse(response);
        response.assertStatusCode(200);
        logStep("Verified response status code is 200");
        response.assertJsonPathValue("title", "patched title");
        logStep("Verified response contains patched title: 'patched title'");
        logStep("Test completed successfully: Patched post with ID 1");
    }

    @Test(description = "Delete a post (DELETE)")
    public void testDeletePost() {
        logStep("Starting test: Delete a post using DELETE method");
        Map<String, Object> pathParams = new HashMap<>();
        pathParams.put("id", 1);
        logStep("Set path parameter: id = 1");
        ApiResponse response = apiClient.delete("/posts/{id}", null, null, pathParams, null, null);
        logStep("Made DELETE request to /posts/{id} endpoint");
        // JSONPlaceholder returns 200 for DELETE
        response.assertStatusCode(200);
        logStep("Verified response status code is 200");
        logStep("Test completed successfully: Deleted post with ID 1");
    }

    @Test(description = "Custom headers and Accept type")
    public void testCustomHeadersAndAccept() {
        logStep("Starting test: Test custom headers and Accept type");
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Test-Header", "test-value");
        logStep("Set custom header: X-Test-Header = test-value");
        ApiResponse response = apiClient.get("/posts", headers, null, null, null, "application/json");
        logStep("Made GET request to /posts endpoint with custom headers and Accept type");
        validateSuccessResponse(response);
        response.assertStatusCode(200);
        logStep("Verified response status code is 200");
        logStep("Test completed successfully: Verified custom headers and Accept type work correctly");
    }
} 