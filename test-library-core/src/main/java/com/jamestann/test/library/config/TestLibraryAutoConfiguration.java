/*
[001][專案結構建立]
Spring Boot Auto Configuration 類別
自動配置Library的核心功能
*/
/*
Update History:
[002][依賴調整] - 新增版本相容性檢查功能，整合SpringBootVersionDetector
*/
package com.jamestann.test.library.config;

import com.jamestann.test.library.util.SpringBootVersionDetector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@AutoConfiguration
@ComponentScan(basePackages = "com.jamestann.test.library")
@EnableConfigurationProperties(TestLibraryProperties.class)
@ConditionalOnProperty(
    prefix = "test.library",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true
)
public class TestLibraryAutoConfiguration {

    private final SpringBootVersionDetector versionDetector;

    public TestLibraryAutoConfiguration(SpringBootVersionDetector versionDetector) {
        this.versionDetector = versionDetector;
        log.info("Test Library Auto Configuration initialized");
        performVersionCompatibilityCheck();
    }

    @Bean
    public TestLibraryManager testLibraryManager(TestLibraryProperties properties) {
        log.info("Creating TestLibraryManager with properties: {}", properties);
        return new TestLibraryManager(properties, versionDetector);
    }

    /**
     * 執行Spring Boot版本相容性檢查
     */
    private void performVersionCompatibilityCheck() {
        SpringBootVersionDetector.VersionCompatibilityReport report = versionDetector.getCompatibilityReport();
        
        if (report.isCompatible()) {
            log.info("Spring Boot version compatibility check passed: {}", report.getMessage());
        } else {
            log.warn("Spring Boot version compatibility warning: {}", report.getMessage());
            log.warn("Supported versions: {}", String.join(", ", report.getSupportedVersions()));
            log.warn("Current setup may not work as expected. Consider upgrading to a supported Spring Boot version.");
        }
        
        log.debug("Version compatibility report: {}", report);
    }
}