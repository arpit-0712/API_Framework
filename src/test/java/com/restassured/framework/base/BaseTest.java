package com.restassured.framework.base;

import com.restassured.framework.config.ConfigManager;
import com.restassured.framework.core.RestApiClient;
import com.restassured.framework.mocks.ApiMockServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.*;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import java.util.Properties;

/**
 * Base test class providing common functionality for all API tests
 */
public abstract class BaseTest {
    protected static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
    protected RestApiClient apiClient;
    protected ApiMockServer mockServer;
    protected ConfigManager configManager;
    protected static ExtentReports extent;
    protected static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    private static String executorName = "Default User";

    /**
     * Set the executor name for the test report
     */
    public static void setExecutorName(String name) {
        executorName = name;
    }

    /**
     * Get executor name from system properties, environment variables, or default
     */
    private static String getExecutorName() {
        // Check system property first
        String sysProp = System.getProperty("test.executor");
        if (sysProp != null && !sysProp.trim().isEmpty()) {
            return sysProp;
        }
        
        // Check environment variable
        String envVar = System.getenv("TEST_EXECUTOR");
        if (envVar != null && !envVar.trim().isEmpty()) {
            return envVar;
        }
        
        // Return default or previously set name
        return executorName;
    }

    /**
     * Get system information as a formatted string
     */
    private String getSystemInfo() {
        Properties props = System.getProperties();
        return String.format("OS: %s %s, Java: %s, User: %s", 
            props.getProperty("os.name"),
            props.getProperty("os.version"),
            props.getProperty("java.version"),
            props.getProperty("user.name"));
    }

    @BeforeClass(alwaysRun = true)
    public void setUpClass() {
        logger.info("Setting up test class: {}", this.getClass().getSimpleName());
        configManager = ConfigManager.getInstance();
        
        // Initialize API client
        apiClient = new RestApiClient();
        
        // Add default headers if configured
        String apiKey = configManager.getApiKey();
        if (apiKey != null && !apiKey.isEmpty()) {
            apiClient.addDefaultHeader("Authorization", "Bearer " + apiKey);
        }
        
        // Setup mock server if mocking is enabled
        if (configManager.isMockingEnabled()) {
            setupMockServer();
        }
    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass() {
        logger.info("Tearing down test class: {}", this.getClass().getSimpleName());
        
        if (mockServer != null && mockServer.isRunning()) {
            mockServer.stop();
        }
    }

    @BeforeSuite(alwaysRun = true)
    public void setUpSuite() {
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter("target/ExtentReports.html");
        sparkReporter.config().setTheme(Theme.STANDARD);
        sparkReporter.config().setDocumentTitle("API Test Report");
        sparkReporter.config().setReportName("REST API Testing");
        extent = new ExtentReports();
        
        // Add system information
        extent.setSystemInfo("OS", System.getProperty("os.name") + " " + System.getProperty("os.version"));
        extent.setSystemInfo("Platform", System.getProperty("os.arch"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        extent.setSystemInfo("User", System.getProperty("user.name"));
        extent.setSystemInfo("Executor", getExecutorName());
        extent.setSystemInfo("Framework", "REST Assured + TestNG");
        
        extent.attachReporter(sparkReporter);
        
        logStep("ExtentReports initialized with system information: " + getSystemInfo());
    }

    @AfterSuite(alwaysRun = true)
    public void tearDownSuite() {
        if (extent != null) {
            extent.flush();
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void setUpMethod(java.lang.reflect.Method method) {
        logger.debug("Setting up test method");
        ExtentTest extentTest = extent.createTest(method.getName());
        
        test.set(extentTest);
        // Reset mock server if running
        if (mockServer != null && mockServer.isRunning()) {
            mockServer.reset();
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDownMethod(ITestResult result) {
        logger.debug("Tearing down test method");
        ExtentTest extentTest = test.get();
        if (result.getStatus() == ITestResult.SUCCESS) {
            extentTest.log(Status.PASS, "Test passed");
        } else if (result.getStatus() == ITestResult.FAILURE) {
            extentTest.log(Status.FAIL, result.getThrowable());
        } else if (result.getStatus() == ITestResult.SKIP) {
            extentTest.log(Status.SKIP, "Test skipped");
        }
        test.remove();
    }

    private void setupMockServer() {
        try {
            mockServer = new ApiMockServer();
            mockServer.start();
            
            // Override base URL to use mock server
            String mockBaseUrl = mockServer.getBaseUrl();
            configManager.setBaseUrl(mockBaseUrl);
            
            logger.info("Mock server setup completed. Base URL: {}", mockBaseUrl);
        } catch (Exception e) {
            logger.error("Failed to setup mock server", e);
            throw new RuntimeException("Mock server setup failed", e);
        }
    }

    /**
     * Helper method to log test information
     */
    protected void logTestInfo(String testName, String description) {
        logStep("Executing test: " + testName + " - " + description);
    }

    /**
     * Helper method to validate response status
     */
    protected void validateSuccessResponse(com.restassured.framework.core.ApiResponse response) {
        response.assertStatusCodeInRange(200, 299);
        logStep("Validated success response with status code: " + response.getStatusCode());
    }

    /**
     * Helper method to validate error response
     */
    protected void validateErrorResponse(com.restassured.framework.core.ApiResponse response, int expectedStatusCode) {
        response.assertStatusCode(expectedStatusCode);
        logStep("Validated error response with expected status code: " + expectedStatusCode);
    }

    protected void logStep(String message) {
        ExtentTest extentTest = test.get();
        if (extentTest != null) {
            extentTest.log(Status.INFO, message);
        }
        logger.info(message);
    }
} 