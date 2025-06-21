package com.restassured.framework.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration Manager for handling environment-specific configurations
 */
public class ConfigManager {
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    private static ConfigManager instance;
    private Properties properties;
    private String environment;
    private String baseUrlOverride = null;

    private ConfigManager() {
        loadConfiguration();
    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    private void loadConfiguration() {
        properties = new Properties();
        environment = System.getProperty("env", "qa");
        
        try {
            // Load default properties
            loadPropertiesFromFile("config.properties");
            
            // Load environment-specific properties
            loadPropertiesFromFile("config-" + environment + ".properties");
            
            // Load system properties (override file properties)
            properties.putAll(System.getProperties());
            
            logger.info("Configuration loaded for environment: {}", environment);
        } catch (IOException e) {
            logger.error("Failed to load configuration files", e);
            throw new RuntimeException("Configuration loading failed", e);
        }
    }

    private void loadPropertiesFromFile(String fileName) throws IOException {
        String configPath = "src/test/resources/config/" + fileName;
        try (InputStream input = new FileInputStream(configPath)) {
            properties.load(input);
            logger.debug("Loaded properties from: {}", configPath);
        } catch (IOException e) {
            logger.warn("Could not load properties from: {}", configPath);
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key);
        try {
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            logger.warn("Invalid integer value for property: {}, using default: {}", key, defaultValue);
            return defaultValue;
        }
    }

    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }

    public String getBaseUrl() {
        if (baseUrlOverride != null) {
            return baseUrlOverride;
        }
        return getProperty("base.url");
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrlOverride = baseUrl;
    }

    public String getApiKey() {
        return getProperty("api.key");
    }

    public int getTimeout() {
        return getIntProperty("timeout", 30000);
    }

    public String getEnvironment() {
        return environment;
    }

    public boolean isLoggingEnabled() {
        return getBooleanProperty("logging.enabled", true);
    }

    public boolean isMockingEnabled() {
        return getBooleanProperty("mocking.enabled", false);
    }
} 