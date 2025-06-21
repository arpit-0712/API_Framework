# Production Deployment Guide

This guide provides step-by-step instructions for deploying the REST API Testing Framework in production environments.

## 🚀 Pre-Deployment Checklist

### Security Requirements
- [ ] API keys configured as environment variables
- [ ] No hardcoded credentials in codebase
- [ ] HTTPS endpoints configured
- [ ] Proper logging levels set (WARN for production)
- [ ] Mock server disabled
- [ ] Security scan completed
- [ ] Access controls implemented

### Infrastructure Requirements
- [ ] Java 11+ installed
- [ ] Maven 3.6+ installed
- [ ] Sufficient disk space (minimum 2GB)
- [ ] Network access to target APIs
- [ ] CI/CD pipeline configured
- [ ] Monitoring tools configured

### Configuration Requirements
- [ ] Production environment config created
- [ ] Timeout settings optimized
- [ ] Retry mechanism configured
- [ ] Parallel execution settings tuned
- [ ] Report storage configured

## 🔧 Environment Setup

### 1. Production Environment Configuration

Ensure `config-prod.properties` is properly configured:

```properties
# Production Environment Configuration
base.url=https://api.production.com
api.key=${API_KEY}
logging.enabled=true
logging.level=WARN
mocking.enabled=false
timeout=60000
connection.timeout=15000
read.timeout=45000
retry.count=2
retry.delay=2000
parallel.execution=true
thread.count=4
```

### 2. Environment Variables

Set production environment variables:

```bash
# Required
export API_KEY="your-production-api-key"
export ENV="prod"

# Optional
export TEST_EXECUTOR="Production Test Suite"
export REPORT_PATH="/var/reports/api-tests"
export LOG_PATH="/var/logs/api-tests"
```

### 3. Directory Structure

Create necessary directories:

```bash
mkdir -p /var/reports/api-tests
mkdir -p /var/logs/api-tests
mkdir -p /opt/api-testing-framework
```

## 🚀 Deployment Methods

### Method 1: Direct Deployment

```bash
# 1. Clone repository
git clone <repository-url> /opt/api-testing-framework
cd /opt/api-testing-framework

# 2. Set environment variables
export API_KEY="your-production-api-key"

# 3. Run setup script
./setup-env.sh

# 4. Run tests
mvn clean test -Denv=prod -Dtest.executor="Production Test Suite"
```

### Method 2: Docker Deployment

Create `Dockerfile`:

```dockerfile
FROM openjdk:11-jdk

# Install Maven
RUN apt-get update && apt-get install -y maven

# Set working directory
WORKDIR /app

# Copy project files
COPY . .

# Set environment variables
ENV API_KEY=""
ENV ENV="prod"

# Run tests
CMD ["mvn", "clean", "test", "-Denv=prod"]
```

Build and run:

```bash
# Build image
docker build -t api-testing-framework .

# Run with environment variable
docker run -e API_KEY="your-api-key" api-testing-framework
```

### Method 3: CI/CD Pipeline

Use the provided GitHub Actions workflow or create custom pipeline:

```yaml
# Example: Jenkins Pipeline
pipeline {
    agent any
    
    environment {
        API_KEY = credentials('PROD_API_KEY')
        ENV = 'prod'
    }
    
    stages {
        stage('Setup') {
            steps {
                checkout scm
                sh './setup-env.sh'
            }
        }
        
        stage('Test') {
            steps {
                sh 'mvn clean test -Denv=prod -Dtest.executor="Jenkins Pipeline"'
            }
        }
        
        stage('Archive Reports') {
            steps {
                archiveArtifacts artifacts: 'target/ExtentReports.html', fingerprint: true
                archiveArtifacts artifacts: 'target/surefire-reports/**/*', fingerprint: true
            }
        }
    }
}
```

## 📊 Monitoring and Reporting

### 1. Report Management

Configure report storage and access:

```bash
# Create report directory with proper permissions
sudo mkdir -p /var/reports/api-tests
sudo chown -R jenkins:jenkins /var/reports/api-tests
sudo chmod 755 /var/reports/api-tests

# Configure web server for report access
sudo ln -s /var/reports/api-tests /var/www/html/api-reports
```

### 2. Log Management

Configure log rotation:

```bash
# Create logrotate configuration
sudo tee /etc/logrotate.d/api-tests << EOF
/var/logs/api-tests/*.log {
    daily
    missingok
    rotate 30
    compress
    delaycompress
    notifempty
    create 644 jenkins jenkins
}
EOF
```

### 3. Monitoring Setup

Configure monitoring for test execution:

```bash
# Create monitoring script
cat > /opt/api-testing-framework/monitor-tests.sh << 'EOF'
#!/bin/bash

# Check if tests are running
if pgrep -f "mvn.*test.*-Denv=prod" > /dev/null; then
    echo "Tests are running"
    exit 0
else
    echo "No tests running"
    exit 1
fi
EOF

chmod +x /opt/api-testing-framework/monitor-tests.sh

# Add to crontab for regular monitoring
echo "*/5 * * * * /opt/api-testing-framework/monitor-tests.sh" | crontab -
```

## 🔒 Security Hardening

### 1. Access Control

```bash
# Create dedicated user for test execution
sudo useradd -r -s /bin/false api-test-user

# Set proper file permissions
sudo chown -R api-test-user:api-test-user /opt/api-testing-framework
sudo chmod 700 /opt/api-testing-framework
```

### 2. Network Security

```bash
# Configure firewall rules
sudo ufw allow from 10.0.0.0/8 to any port 8080  # Internal network only
sudo ufw deny 8080  # Deny external access to mock server
```

### 3. API Key Security

```bash
# Use key management service
# Example: AWS Secrets Manager
aws secretsmanager get-secret-value --secret-id prod-api-key --query SecretString --output text

# Or use environment-specific key files
echo "your-api-key" | sudo tee /etc/api-testing/prod-api-key
sudo chmod 600 /etc/api-testing/prod-api-key
```

## 📈 Performance Optimization

### 1. JVM Tuning

```bash
# Optimize JVM settings for production
export MAVEN_OPTS="-Xmx2g -Xms1g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
```

### 2. Parallel Execution

```properties
# Optimize parallel execution
parallel.execution=true
thread.count=8  # Adjust based on CPU cores
```

### 3. Timeout Optimization

```properties
# Production-optimized timeouts
timeout=60000
connection.timeout=15000
read.timeout=45000
```

## 🚨 Troubleshooting

### Common Issues

1. **API Key Not Found**
   ```bash
   # Check environment variable
   echo $API_KEY
   
   # Check if set in shell profile
   grep API_KEY ~/.bashrc
   ```

2. **Permission Denied**
   ```bash
   # Fix file permissions
   sudo chown -R $USER:$USER /opt/api-testing-framework
   chmod +x /opt/api-testing-framework/*.sh
   ```

3. **Out of Memory**
   ```bash
   # Increase heap size
   export MAVEN_OPTS="-Xmx4g -Xms2g"
   ```

4. **Network Timeout**
   ```bash
   # Check network connectivity
   curl -I https://api.production.com
   
   # Check firewall rules
   sudo ufw status
   ```

### Log Analysis

```bash
# View recent logs
tail -f /var/logs/api-tests/rest-api-framework.log

# Search for errors
grep -i error /var/logs/api-tests/*.log

# Check test execution time
grep "Test completed" /var/logs/api-tests/*.log | tail -10
```

## 🔄 Maintenance

### Regular Tasks

1. **Weekly**
   - Review test reports
   - Check log files for errors
   - Update API keys if needed
   - Clean old reports and logs

2. **Monthly**
   - Update dependencies
   - Review security settings
   - Performance analysis
   - Backup configuration

3. **Quarterly**
   - Security audit
   - Performance optimization
   - Framework updates
   - Documentation review

### Backup Strategy

```bash
# Create backup script
cat > /opt/api-testing-framework/backup.sh << 'EOF'
#!/bin/bash

BACKUP_DIR="/var/backups/api-testing-framework"
DATE=$(date +%Y%m%d_%H%M%S)

mkdir -p $BACKUP_DIR

# Backup configuration
tar -czf $BACKUP_DIR/config_$DATE.tar.gz src/test/resources/config/

# Backup reports
tar -czf $BACKUP_DIR/reports_$DATE.tar.gz /var/reports/api-tests/

# Backup logs
tar -czf $BACKUP_DIR/logs_$DATE.tar.gz /var/logs/api-tests/

# Clean old backups (keep last 30 days)
find $BACKUP_DIR -name "*.tar.gz" -mtime +30 -delete
EOF

chmod +x /opt/api-testing-framework/backup.sh

# Add to crontab
echo "0 2 * * 0 /opt/api-testing-framework/backup.sh" | crontab -
```

## 📞 Support

For production deployment support:

1. **Documentation**: Check README.md and SECURITY.md
2. **Logs**: Review `/var/logs/api-tests/`
3. **Reports**: Check `/var/reports/api-tests/`
4. **Issues**: Create issue in repository
5. **Emergency**: Contact system administrator

---

**⚠️ Important**: Always test deployment in staging environment before production!

## 🚀 Step-by-Step Deployment Instructions

### Phase 1: Pre-Deployment Setup

#### 1.1 System Requirements Verification
```bash
# Check Java version
java -version

# Check Maven version
mvn -version

# Check available disk space
df -h

# Check available memory
free -h
```

#### 1.2 Environment Preparation
```bash
# Create deployment user
sudo useradd -m -s /bin/bash api-deploy
sudo usermod -aG sudo api-deploy

# Switch to deployment user
sudo su - api-deploy

# Create application directories
mkdir -p /home/api-deploy/api-testing-framework
mkdir -p /home/api-deploy/reports
mkdir -p /home/api-deploy/logs
```

#### 1.3 Security Setup
```bash
# Create secure API key storage
sudo mkdir -p /etc/api-testing
sudo chmod 700 /etc/api-testing

# Store API key securely
echo "your-production-api-key" | sudo tee /etc/api-testing/prod-api-key
sudo chmod 600 /etc/api-testing/prod-api-key
sudo chown api-deploy:api-deploy /etc/api-testing/prod-api-key
```

### Phase 2: Application Deployment

#### 2.1 Code Deployment
```bash
# Clone repository
cd /home/api-deploy
git clone <repository-url> api-testing-framework
cd api-testing-framework

# Set environment variables
export API_KEY=$(cat /etc/api-testing/prod-api-key)
export ENV="prod"
export TEST_EXECUTOR="Production Deployment"

# Run setup validation
./setup-env.sh
```

#### 2.2 Configuration Setup
```bash
# Verify production configuration
cat src/test/resources/config/config-prod.properties

# Test configuration loading
mvn test -Dtest=ConfigManagerTest -q
```

#### 2.3 Initial Test Run
```bash
# Run smoke tests
mvn clean test -Denv=prod -Dtest=JSONPlaceholderApiTests#testGetUser

# Verify reports generation
ls -la target/ExtentReports.html
```

### Phase 3: Production Configuration

#### 3.1 Service Configuration
```bash
# Create systemd service file
sudo tee /etc/systemd/system/api-testing.service << EOF
[Unit]
Description=REST API Testing Framework
After=network.target

[Service]
Type=simple
User=api-deploy
WorkingDirectory=/home/api-deploy/api-testing-framework
Environment=API_KEY=$(cat /etc/api-testing/prod-api-key)
Environment=ENV=prod
ExecStart=/usr/bin/mvn clean test -Denv=prod
Restart=on-failure
RestartSec=30

[Install]
WantedBy=multi-user.target
EOF

# Enable and start service
sudo systemctl daemon-reload
sudo systemctl enable api-testing
sudo systemctl start api-testing
```

#### 3.2 Monitoring Setup
```bash
# Create monitoring script
cat > /home/api-deploy/monitor.sh << 'EOF'
#!/bin/bash

# Check service status
if systemctl is-active --quiet api-testing; then
    echo "Service is running"
    exit 0
else
    echo "Service is not running"
    systemctl restart api-testing
    exit 1
fi
EOF

chmod +x /home/api-deploy/monitor.sh

# Add to crontab
(crontab -l 2>/dev/null; echo "*/5 * * * * /home/api-deploy/monitor.sh") | crontab -
```

### Phase 4: Post-Deployment Verification

#### 4.1 Health Checks
```bash
# Check service status
sudo systemctl status api-testing

# Check logs
sudo journalctl -u api-testing -f

# Verify test execution
ls -la /home/api-deploy/reports/
```

#### 4.2 Performance Validation
```bash
# Run performance test
mvn clean test -Denv=prod -Dtest=PerformanceTest

# Check resource usage
top -p $(pgrep -f "mvn.*test.*-Denv=prod")
```

#### 4.3 Security Validation
```bash
# Verify no hardcoded credentials
grep -r "api.key=" src/ --include="*.properties" | grep -v "\${API_KEY}"

# Check file permissions
ls -la /etc/api-testing/
ls -la /home/api-deploy/api-testing-framework/
```

## 🔧 Advanced Deployment Scenarios

### Multi-Instance Deployment
```bash
# Deploy to multiple servers
for server in server1 server2 server3; do
    ssh $server "cd /opt/api-testing-framework && git pull origin main"
    ssh $server "export API_KEY=$API_KEY && mvn clean test -Denv=prod"
done
```

### Blue-Green Deployment
```bash
# Blue environment (current)
export BLUE_ENV="prod-blue"
export GREEN_ENV="prod-green"

# Deploy to green environment
mvn clean test -Denv=$GREEN_ENV

# Switch traffic
# Update load balancer configuration
# Verify green environment

# Promote green to blue
export BLUE_ENV="prod-green"
```

### Canary Deployment
```bash
# Deploy to 10% of traffic
mvn clean test -Denv=prod-canary -Dtest.executor="Canary Test"

# Monitor canary performance
# Check error rates
# Verify functionality

# Gradually increase traffic
# Full deployment if successful
```

## 📊 Operational Monitoring

### Real-time Monitoring
```bash
# Monitor test execution
watch -n 5 'ps aux | grep mvn'

# Monitor resource usage
htop

# Monitor network connections
netstat -tulpn | grep java
```

### Alert Configuration
```bash
# Create alert script
cat > /home/api-deploy/alert.sh << 'EOF'
#!/bin/bash

# Check for test failures
if grep -q "FAILED" /home/api-deploy/logs/test-execution.log; then
    echo "Test failures detected!" | mail -s "API Test Alert" admin@company.com
fi

# Check for high resource usage
CPU_USAGE=$(top -bn1 | grep "Cpu(s)" | awk '{print $2}' | cut -d'%' -f1)
if (( $(echo "$CPU_USAGE > 80" | bc -l) )); then
    echo "High CPU usage: ${CPU_USAGE}%" | mail -s "Resource Alert" admin@company.com
fi
EOF

chmod +x /home/api-deploy/alert.sh
```

## 🔄 Rollback Procedures

### Emergency Rollback
```bash
# Stop current deployment
sudo systemctl stop api-testing

# Revert to previous version
cd /home/api-deploy/api-testing-framework
git checkout HEAD~1

# Restart with previous version
sudo systemctl start api-testing

# Verify rollback
mvn clean test -Denv=prod -Dtest=SmokeTest
```

### Database Rollback (if applicable)
```bash
# Restore test data
mysql -u username -p database_name < backup/test_data_backup.sql

# Verify data integrity
mvn clean test -Denv=prod -Dtest=DataIntegrityTest
```

## 📈 Performance Tuning

### JVM Optimization
```bash
# Production JVM settings
export MAVEN_OPTS="-Xmx4g -Xms2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UseStringDeduplication"
```

### Network Optimization
```bash
# Increase file descriptor limits
echo "* soft nofile 65536" | sudo tee -a /etc/security/limits.conf
echo "* hard nofile 65536" | sudo tee -a /etc/security/limits.conf

# Optimize network settings
echo "net.core.somaxconn = 65536" | sudo tee -a /etc/sysctl.conf
sudo sysctl -p
```

### Disk I/O Optimization
```bash
# Use SSD for reports and logs
sudo mkdir -p /mnt/ssd/reports
sudo mkdir -p /mnt/ssd/logs

# Mount SSD
sudo mount /dev/sdb1 /mnt/ssd

# Update configuration to use SSD
sed -i 's|/var/reports|/mnt/ssd/reports|g' config-prod.properties
sed -i 's|/var/logs|/mnt/ssd/logs|g' config-prod.properties
```

## 🛡️ Security Hardening

### Network Security
```bash
# Configure firewall
sudo ufw default deny incoming
sudo ufw default allow outgoing
sudo ufw allow ssh
sudo ufw allow from 10.0.0.0/8 to any port 8080
sudo ufw enable
```

### File System Security
```bash
# Set proper permissions
sudo chown -R api-deploy:api-deploy /home/api-deploy/api-testing-framework
sudo chmod 700 /home/api-deploy/api-testing-framework
sudo chmod 600 /home/api-deploy/api-testing-framework/src/test/resources/config/*.properties
```

### Process Security
```bash
# Run as non-root user
sudo chown -R api-deploy:api-deploy /opt/api-testing-framework

# Set up process isolation
sudo systemctl set-property api-testing.service NoNewPrivileges=true
sudo systemctl set-property api-testing.service ProtectSystem=strict
```

## 📋 Deployment Checklist

### Pre-Deployment
- [ ] System requirements verified
- [ ] Security audit completed
- [ ] Backup created
- [ ] Rollback plan prepared
- [ ] Monitoring configured
- [ ] Team notified

### During Deployment
- [ ] Code deployed
- [ ] Configuration updated
- [ ] Environment variables set
- [ ] Service started
- [ ] Health checks passed
- [ ] Smoke tests executed

### Post-Deployment
- [ ] Full test suite executed
- [ ] Performance validated
- [ ] Security verified
- [ ] Monitoring alerts configured
- [ ] Documentation updated
- [ ] Team notified of completion

## 🚨 Emergency Procedures

### Service Outage
```bash
# Immediate response
sudo systemctl restart api-testing

# Check logs
sudo journalctl -u api-testing --since "5 minutes ago"

# Verify connectivity
curl -I https://api.production.com
```

### Data Loss
```bash
# Restore from backup
tar -xzf /var/backups/api-testing-framework/config_$(date +%Y%m%d).tar.gz

# Verify restoration
mvn clean test -Denv=prod -Dtest=DataValidationTest
```

### Security Breach
```bash
# Immediate actions
sudo systemctl stop api-testing
sudo ufw deny all

# Rotate API keys
# Update environment variables
# Restart service with new keys
sudo systemctl start api-testing
```

---

**🎯 Your REST API Testing Framework is now fully deployed and operational in production!** 