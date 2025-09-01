/*
[001][專案結構建立]
Spring Boot Auto Configuration 類別
自動配置Library的核心功能
*/
package com.jamestann.test.library.config;

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

    public TestLibraryAutoConfiguration() {
        log.info("Test Library Auto Configuration initialized");
    }

    @Bean
    public TestLibraryManager testLibraryManager(TestLibraryProperties properties) {
        log.info("Creating TestLibraryManager with properties: {}", properties);
        return new TestLibraryManager(properties);
    }
}