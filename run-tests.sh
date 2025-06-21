#!/bin/bash

# REST API Testing Framework - Test Runner Script

echo "🚀 REST API Testing Framework"
echo "=============================="

# Default values
ENV="qa"
PARALLEL="false"
THREAD_COUNT="1"
TEST_CLASS=""
EXECUTOR_NAME=""

# Function to display usage
show_usage() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -e, --env ENV           Environment (default: qa)"
    echo "  -x, --executor NAME     Test executor name (default: system user)"
    echo "  -p, --parallel          Enable parallel execution"
    echo "  -t, --threads COUNT     Number of threads for parallel execution (default: 1)"
    echo "  -c, --class CLASS       Run specific test class"
    echo "  -h, --help              Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0                                    # Run all tests in QA environment"
    echo "  $0 -e prod                            # Run tests in production environment"
    echo "  $0 -x 'John Doe'                      # Run tests with executor name"
    echo "  $0 -p -t 3                            # Run tests in parallel with 3 threads"
    echo "  $0 -c UserApiTests                    # Run specific test class"
    echo "  $0 -e qa -x 'QA Team' -c UserApiTests # Run with environment and executor"
}

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -e|--env)
            ENV="$2"
            shift 2
            ;;
        -x|--executor)
            EXECUTOR_NAME="$2"
            shift 2
            ;;
        -p|--parallel)
            PARALLEL="true"
            shift
            ;;
        -t|--threads)
            THREAD_COUNT="$2"
            shift 2
            ;;
        -c|--class)
            TEST_CLASS="$2"
            shift 2
            ;;
        -h|--help)
            show_usage
            exit 0
            ;;
        *)
            echo "Unknown option: $1"
            show_usage
            exit 1
            ;;
    esac
done

# Display configuration
echo "Configuration:"
echo "  Environment: $ENV"
if [ ! -z "$EXECUTOR_NAME" ]; then
    echo "  Executor: $EXECUTOR_NAME"
fi
echo "  Parallel Execution: $PARALLEL"
echo "  Thread Count: $THREAD_COUNT"
if [ ! -z "$TEST_CLASS" ]; then
    echo "  Test Class: $TEST_CLASS"
fi
echo ""

# Build Maven command
MAVEN_CMD="mvn clean test"

# Add environment parameter
MAVEN_CMD="$MAVEN_CMD -Denv=$ENV"

# Add executor parameter if specified
if [ ! -z "$EXECUTOR_NAME" ]; then
    MAVEN_CMD="$MAVEN_CMD -Dtest.executor=\"$EXECUTOR_NAME\""
fi

# Add parallel execution parameters
if [ "$PARALLEL" = "true" ]; then
    MAVEN_CMD="$MAVEN_CMD -Dparallel.execution=true -Dthread.count=$THREAD_COUNT"
fi

# Add test class parameter
if [ ! -z "$TEST_CLASS" ]; then
    MAVEN_CMD="$MAVEN_CMD -Dtest=$TEST_CLASS"
fi

echo "Executing: $MAVEN_CMD"
echo ""

# Execute Maven command
eval $MAVEN_CMD

# Check if tests were successful
if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Tests completed successfully!"
else
    echo ""
    echo "❌ Tests failed!"
fi

# Open ExtentReports HTML report if it exists
REPORT_PATH="target/ExtentReports.html"
if [ -f "$REPORT_PATH" ]; then
    echo "📖 Opening ExtentReports: $REPORT_PATH"
    if which xdg-open > /dev/null; then
        xdg-open "$REPORT_PATH" &
    elif which gnome-open > /dev/null; then
        gnome-open "$REPORT_PATH" &
    else
        echo "Please open $REPORT_PATH in your browser."
    fi
else
    echo "ExtentReports HTML report not found at $REPORT_PATH."
fi 