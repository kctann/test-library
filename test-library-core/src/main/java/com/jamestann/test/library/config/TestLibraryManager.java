/*
[001][專案結構建立]
Test Library管理類別
負責管理Library的核心功能和生命週期
*/
/*
Update History:
[002][依賴調整] - 整合SpringBootVersionDetector，新增版本資訊管理功能
*/
package com.jamestann.test.library.config;

import com.jamestann.test.library.util.SpringBootVersionDetector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

@Slf4j
public class TestLibraryManager implements InitializingBean {

    private final TestLibraryProperties properties;
    private final SpringBootVersionDetector versionDetector;

    public TestLibraryManager(TestLibraryProperties properties, SpringBootVersionDetector versionDetector) {
        this.properties = properties;
        this.versionDetector = versionDetector;
    }

    @Override
    public void afterPropertiesSet() {
        log.info("Initializing Test Library with configuration:");
        log.info("  - Enabled: {}", properties.isEnabled());
        log.info("  - Library Name: {}", properties.getLibraryName());
        log.info("  - Performance Monitoring: {}", properties.isPerformanceMonitoringEnabled());
        log.info("  - Logging Standardization: {}", properties.isLoggingStandardizationEnabled());
        log.info("  - Custom Actuator Endpoints: {}", properties.getActuator().isCustomEndpointsEnabled());
        
        // 添加版本資訊到初始化日誌
        logVersionInformation();

        if (properties.isEnabled()) {
            initializeLibrary();
        }
    }

    private void initializeLibrary() {
        log.info("Test Library [{}] successfully initialized", properties.getLibraryName());
    }

    /**
     * 記錄版本相關資訊
     */
    private void logVersionInformation() {
        SpringBootVersionDetector.VersionCompatibilityReport report = versionDetector.getCompatibilityReport();
        log.info("  - Spring Boot Version: {}", report.getCurrentVersion());
        log.info("  - Version Compatibility: {}", report.isCompatible() ? "Compatible" : "Not Compatible");
    }

    public TestLibraryProperties getProperties() {
        return properties;
    }

    public boolean isEnabled() {
        return properties.isEnabled();
    }

    /**
     * 取得版本相容性資訊
     * 
     * @return 版本相容性報告
     */
    public SpringBootVersionDetector.VersionCompatibilityReport getVersionCompatibilityReport() {
        return versionDetector.getCompatibilityReport();
    }

    /**
     * 檢查當前Spring Boot版本是否相容
     * 
     * @return true如果相容，false否則
     */
    public boolean isCurrentVersionCompatible() {
        return versionDetector.isCurrentVersionCompatible();
    }

    /**
     * 取得當前Spring Boot版本
     * 
     * @return Spring Boot版本字串
     */
    public String getCurrentSpringBootVersion() {
        return versionDetector.detectSpringBootVersion();
    }
}