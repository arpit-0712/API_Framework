# REST API Testing Framework

A comprehensive, production-ready REST API testing framework built with **RestAssured**, **TestNG**, **ExtentReports**, and **WireMock**. Designed for enterprise-level API testing with support for multiple environments, secure credential management, and comprehensive reporting.

## 🚀 Features

- **🔧 Reusable HTTP Client**: Support for all HTTP methods (GET, POST, PUT, DELETE, PATCH, OPTIONS)
- **🌍 Multi-Environment Support**: QA, Staging, and Production configurations
- **🔐 Secure Credential Management**: Environment variable-based API keys
- **📊 ExtentReports Integration**: Beautiful HTML reports with system info and test details
- **🎭 API Mocking**: WireMock integration for isolated testing
- **📝 Comprehensive Logging**: Logback integration with configurable levels
- **⚡ Parallel Execution**: Configurable parallel test execution
- **🔄 Retry Mechanism**: Automatic retry for failed requests
- **🔍 JSON Schema Validation**: Built-in schema validation support
- **📈 CI/CD Ready**: GitHub Actions workflow included

## 🏗️ Architecture

```
src/
├── main/java/
│   └── com/apiframework/
│       ├── client/
│       │   └── RestApiClient.java          # Reusable HTTP client
│       ├── config/
│       │   └── ConfigManager.java          # Configuration management
│       ├── utils/
│       │   ├── JsonUtils.java              # JSON utilities
│       │   └── TestDataUtils.java          # Test data utilities
│       └── constants/
│           └── ApiConstants.java           # API constants
└── test/
    ├── java/
    │   └── com/apiframework/
    │       ├── base/
    │       │   └── BaseTest.java           # Base test class
    │       ├── tests/
    │       │   └── JSONPlaceholderApiTests.java  # Sample tests
    │       └── mocks/
    │           └── MockServer.java         # WireMock server
    └── resources/
        ├── config/
        │   ├── config.properties           # Default config
        │   ├── config-qa.properties        # QA environment
        │   ├── config-staging.properties   # Staging environment
        │   └── config-prod.properties      # Production environment
        ├── testdata/                       # Test data files
        └── schemas/                        # JSON schemas
```

## 🚀 Quick Start

### Prerequisites
- Java 11 or higher
- Maven 3.6+
- API key (set as environment variable)

### 1. Clone and Setup
```bash
git clone <repository-url>
cd RestAssured_sample
```

### 2. Set API Key
```bash
# Linux/Mac
export API_KEY="your-api-key"

# Windows
set API_KEY=your-api-key
```

### 3. Run Tests
```bash
# Run with default configuration
./run-tests.sh

# Run for specific environment
mvn clean test -Denv=qa
mvn clean test -Denv=staging
mvn clean test -Denv=prod

# Run with custom executor name
./run-tests.sh "My Test Suite"
```

## 🔧 Configuration

### Environment Configuration
The framework supports multiple environments with specific configurations:

| Environment | Config File | Purpose |
|-------------|-------------|---------|
| Default | `config.properties` | Development/Default settings |
| QA | `config-qa.properties` | Quality Assurance testing |
| Staging | `config-staging.properties` | Pre-production testing |
| Production | `config-prod.properties` | Production testing |

### Key Configuration Properties
```properties
# Base URL for API endpoints
base.url=https://api.example.com

# API Key (set via environment variable)
api.key=${API_KEY}

# Timeout settings (milliseconds)
timeout=30000
connection.timeout=10000
read.timeout=30000

# Logging settings
logging.enabled=true
logging.level=INFO

# Mock server settings
mocking.enabled=false
mock.server.port=8080

# ExtentReports settings
extent.report.path=target/ExtentReports.html

# Parallel execution
parallel.execution=false
thread.count=1
```

## �� Writing Tests

### Basic Test Structure
```java
public class MyApiTests extends BaseTest {
    
    @Test
    public void testGetUser() {
        // Use the reusable client
        Response response = restApiClient
            .get("/users/1")
            .addHeader("Authorization", "Bearer " + apiKey)
            .execute();
        
        // Validate response
        response.then()
            .statusCode(200)
            .body("id", equalTo(1))
            .body("name", notNullValue());
    }
}
```

### Using Different HTTP Methods
```java
// GET request
Response getResponse = restApiClient
    .get("/users")
    .addQueryParam("page", "1")
    .execute();

// POST request
Response postResponse = restApiClient
    .post("/users")
    .setBody(userData)
    .setContentType("application/json")
    .execute();

// PUT request
Response putResponse = restApiClient
    .put("/users/1")
    .setBody(updatedUserData)
    .execute();

// DELETE request
Response deleteResponse = restApiClient
    .delete("/users/1")
    .execute();
```

## 🔐 Security Best Practices

### 1. API Key Management
- **Never** commit API keys to version control
- **Always** use environment variables
- **Rotate** keys regularly
- **Monitor** key usage

### 2. Environment Separation
- Use different API keys for each environment
- Implement proper access controls
- Use HTTPS for all endpoints

### 3. Logging Security
- Production: `logging.level=WARN`
- Staging: `logging.level=INFO`
- QA: `logging.level=DEBUG`

See [SECURITY.md](SECURITY.md) for detailed security guidelines.

## 🚀 Production Deployment

### Pre-Deployment Checklist
- [ ] API keys set as environment variables
- [ ] No hardcoded credentials in code
- [ ] HTTPS endpoints configured
- [ ] Proper logging levels set
- [ ] Timeout settings configured
- [ ] Mock server disabled
- [ ] Parallel execution configured
- [ ] Retry mechanism enabled

### CI/CD Integration
The framework includes a GitHub Actions workflow (`/.github/workflows/api-tests.yml`) that:
- Runs tests on multiple environments
- Uses secure environment variables
- Generates and uploads reports
- Performs security scans

### Environment Variables for CI/CD
```yaml
# GitHub Secrets
QA_API_KEY: "qa-api-key"
STAGING_API_KEY: "staging-api-key"
PROD_API_KEY: "prod-api-key"
```

## 📊 Reporting

### ExtentReports
- **Location**: `target/ExtentReports.html`
- **Features**: 
  - System information
  - Test execution details
  - Step-by-step logging
  - Screenshots (if applicable)
  - Environment details

### Report Customization
```java
// Add custom system info
extent.setSystemInfo("Environment", environment);
extent.setSystemInfo("API Version", "v1.0");
extent.setSystemInfo("Executor", executorName);
```

## 🎭 API Mocking

### Enable Mocking
```properties
# In config file
mocking.enabled=true
mock.server.port=8080
```

### Mock Server Setup
```java
@BeforeClass
public void setupMockServer() {
    if (ConfigManager.getInstance().isMockingEnabled()) {
        MockServer.start();
        MockServer.stubGet("/users/1", 200, userResponse);
    }
}
```

## 🔧 Troubleshooting

### Common Issues

1. **API Key Not Found**
   ```bash
   export API_KEY="your-api-key"
   ```

2. **Port Already in Use**
   ```properties
   # Change mock server port
   mock.server.port=8081
   ```

3. **Timeout Issues**
   ```properties
   # Increase timeout
   timeout=60000
   connection.timeout=15000
   ```

### Debug Mode
```bash
# Enable debug logging
mvn test -Dlogging.level=DEBUG
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Check the troubleshooting section
- Review the security guidelines

---

**⚠️ Important**: Always follow security best practices and never commit sensitive information to version control!