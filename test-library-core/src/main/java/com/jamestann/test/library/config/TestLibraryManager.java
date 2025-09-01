/*
[001][專案結構建立]
Test Library管理類別
負責管理Library的核心功能和生命週期
*/
package com.jamestann.test.library.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

@Slf4j
public class TestLibraryManager implements InitializingBean {

    private final TestLibraryProperties properties;

    public TestLibraryManager(TestLibraryProperties properties) {
        this.properties = properties;
    }

    @Override
    public void afterPropertiesSet() {
        log.info("Initializing Test Library with configuration:");
        log.info("  - Enabled: {}", properties.isEnabled());
        log.info("  - Library Name: {}", properties.getLibraryName());
        log.info("  - Performance Monitoring: {}", properties.isPerformanceMonitoringEnabled());
        log.info("  - Logging Standardization: {}", properties.isLoggingStandardizationEnabled());
        log.info("  - Custom Actuator Endpoints: {}", properties.getActuator().isCustomEndpointsEnabled());

        if (properties.isEnabled()) {
            initializeLibrary();
        }
    }

    private void initializeLibrary() {
        log.info("Test Library [{}] successfully initialized", properties.getLibraryName());
    }

    public TestLibraryProperties getProperties() {
        return properties;
    }

    public boolean isEnabled() {
        return properties.isEnabled();
    }
}