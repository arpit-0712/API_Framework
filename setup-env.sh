#!/bin/bash

# REST API Testing Framework - Environment Setup Script
# This script helps set up the environment for running API tests

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Function to validate Java version
check_java() {
    print_status "Checking Java installation..."
    
    if ! command_exists java; then
        print_error "Java is not installed. Please install Java 11 or higher."
        exit 1
    fi
    
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    
    if [ "$JAVA_VERSION" -lt 11 ]; then
        print_error "Java version $JAVA_VERSION is too old. Please install Java 11 or higher."
        exit 1
    fi
    
    print_success "Java $JAVA_VERSION is installed and compatible."
}

# Function to check Maven
check_maven() {
    print_status "Checking Maven installation..."
    
    if ! command_exists mvn; then
        print_error "Maven is not installed. Please install Maven 3.6 or higher."
        exit 1
    fi
    
    MVN_VERSION=$(mvn -version | head -n 1 | cut -d' ' -f3)
    print_success "Maven $MVN_VERSION is installed."
}

# Function to setup API key
setup_api_key() {
    print_status "Setting up API key..."
    
    if [ -z "$API_KEY" ]; then
        print_warning "API_KEY environment variable is not set."
        echo
        echo "Please set your API key using one of these methods:"
        echo
        echo "1. Export as environment variable:"
        echo "   export API_KEY=\"your-api-key\""
        echo
        echo "2. Add to your shell profile (~/.bashrc, ~/.zshrc, etc.):"
        echo "   echo 'export API_KEY=\"your-api-key\"' >> ~/.bashrc"
        echo "   source ~/.bashrc"
        echo
        echo "3. Set for current session only:"
        read -p "Enter your API key: " -s api_key_input
        echo
        export API_KEY="$api_key_input"
        print_success "API key set for current session."
    else
        print_success "API key is already set."
    fi
}

# Function to validate configuration files
validate_config() {
    print_status "Validating configuration files..."
    
    CONFIG_FILES=(
        "src/test/resources/config/config.properties"
        "src/test/resources/config/config-qa.properties"
        "src/test/resources/config/config-staging.properties"
        "src/test/resources/config/config-prod.properties"
    )
    
    for config_file in "${CONFIG_FILES[@]}"; do
        if [ -f "$config_file" ]; then
            print_success "✓ $config_file exists"
        else
            print_error "✗ $config_file missing"
            exit 1
        fi
    done
}

# Function to check for hardcoded credentials
security_check() {
    print_status "Performing security check..."
    
    # Check for hardcoded API keys
    if grep -r "api.key=" src/test/resources/config/ --include="*.properties" | grep -v "\${API_KEY}"; then
        print_error "Found hardcoded API keys in configuration files!"
        print_error "Please use \${API_KEY} instead of hardcoded values."
        exit 1
    fi
    
    print_success "No hardcoded credentials found."
}

# Function to install dependencies
install_dependencies() {
    print_status "Installing Maven dependencies..."
    
    if mvn clean compile -q; then
        print_success "Dependencies installed successfully."
    else
        print_error "Failed to install dependencies."
        exit 1
    fi
}

# Function to run quick validation test
run_validation_test() {
    print_status "Running validation test..."
    
    if [ -z "$API_KEY" ]; then
        print_warning "Skipping validation test - API key not set."
        return
    fi
    
    if mvn test -Dtest=JSONPlaceholderApiTests#testGetUser -q; then
        print_success "Validation test passed."
    else
        print_warning "Validation test failed. This might be expected if the API is not available."
    fi
}

# Function to display next steps
show_next_steps() {
    echo
    echo "=========================================="
    echo "🎉 Environment setup completed!"
    echo "=========================================="
    echo
    echo "Next steps:"
    echo
    echo "1. Run all tests:"
    echo "   ./run-tests.sh"
    echo
    echo "2. Run tests for specific environment:"
    echo "   mvn clean test -Denv=qa"
    echo "   mvn clean test -Denv=staging"
    echo "   mvn clean test -Denv=prod"
    echo
    echo "3. Run with custom executor name:"
    echo "   ./run-tests.sh \"My Test Suite\""
    echo
    echo "4. View test reports:"
    echo "   open target/ExtentReports.html"
    echo
    echo "5. Check logs:"
    echo "   tail -f logs/rest-api-framework.log"
    echo
    echo "For more information, see README.md"
    echo
}

# Main execution
main() {
    echo "🚀 REST API Testing Framework - Environment Setup"
    echo "================================================"
    echo
    
    check_java
    check_maven
    setup_api_key
    validate_config
    security_check
    install_dependencies
    run_validation_test
    show_next_steps
}

# Run main function
main "$@" 