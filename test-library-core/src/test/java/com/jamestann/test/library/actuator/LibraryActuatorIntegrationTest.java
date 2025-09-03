/*
[003][M1][基礎Actuator整合]
Library Actuator Integration測試類別
測試 Spring Context 中的 Actuator 整合功能
*/
package com.jamestann.test.library.actuator;

import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for LibraryActuator components with Spring Context.
 * <p>
 * Tests the full Spring Boot auto-configuration and bean creation
 * in an actual Spring application context.
 * 
 * @author James Tann
 * @since 1.0.0
 */
/*
[003][M1][基礎Actuator整合]
LibraryActuator 元件與 Spring Context 的整合測試
測試實際 Spring 應用程式上下文中的完整自動配置和 Bean 創建
*/
@SpringBootTest(classes = {
    LibraryActuatorAutoConfiguration.class,
    ActuatorTestConfiguration.class
})
@TestPropertySource(properties = {
    "test-library.actuator.enabled=true",
    "test-library.actuator.health.enabled=true", 
    "test-library.actuator.info.enabled=true",
    "test-library.actuator.metrics.enabled=true"
})
class LibraryActuatorIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Tests that all required beans are created in Spring context.
     */
    /*
    [003][M1][基礎Actuator整合]
    input: void
    output: void
    測試所有必需的 Bean 在 Spring 上下文中創建
    */
    @Test
    void testAllRequiredBeansCreated() {
        // Properties bean
        assertThat(applicationContext.getBeansOfType(LibraryActuatorProperties.class))
            .hasSize(1);
            
        // SLI Collector
        assertThat(applicationContext.getBeansOfType(LibrarySLICollector.class))
            .hasSize(1);
            
        // Health Indicator  
        assertThat(applicationContext.getBeansOfType(LibraryHealthIndicator.class))
            .hasSize(1);
            
        // Info Contributor
        assertThat(applicationContext.getBeansOfType(LibraryInfoContributor.class))
            .hasSize(1);
            
        // Monitoring Interceptor
        assertThat(applicationContext.getBeansOfType(LibraryMonitoringInterceptor.class))
            .hasSize(1);
    }

    /**
     * Tests that beans are properly configured and wired.
     */
    /*
    [003][M1][基礎Actuator整合]
    input: void
    output: void
    測試 Bean 正確配置和連接
    */
    @Test
    void testBeansProperlyConfigured() {
        LibraryActuatorProperties properties = 
            applicationContext.getBean(LibraryActuatorProperties.class);
        assertThat(properties.isEnabled()).isTrue();
        assertThat(properties.getHealth().isEnabled()).isTrue();
        assertThat(properties.getInfo().isEnabled()).isTrue();
        assertThat(properties.getMetrics().isEnabled()).isTrue();
        
        LibrarySLICollector sliCollector = 
            applicationContext.getBean(LibrarySLICollector.class);
        assertThat(sliCollector).isNotNull();
        assertThat(sliCollector.getActiveRequestCount()).isEqualTo(0);
        
        LibraryHealthIndicator healthIndicator = 
            applicationContext.getBean(LibraryHealthIndicator.class);
        assertThat(healthIndicator).isNotNull();
        assertThat(healthIndicator.health().getStatus().getCode()).isEqualTo("UP");
    }

    /**
     * Tests that health indicator is registered as HealthIndicator.
     */
    /*
    [003][M1][基礎Actuator整合]
    input: void
    output: void
    測試健康指標註冊為 HealthIndicator
    */
    @Test
    void testHealthIndicatorRegistration() {
        // Should be available as HealthIndicator interface
        HealthIndicator healthIndicator = applicationContext.getBean("libraryHealthIndicator", HealthIndicator.class);
        assertThat(healthIndicator).isNotNull();
        assertThat(healthIndicator).isInstanceOf(LibraryHealthIndicator.class);
    }

    /**
     * Tests that info contributor is registered as InfoContributor.
     */
    /*
    [003][M1][基礎Actuator整合]
    input: void
    output: void
    測試資訊貢獻者註冊為 InfoContributor
    */
    @Test
    void testInfoContributorRegistration() {
        // Should be available as InfoContributor interface
        InfoContributor infoContributor = applicationContext.getBean("libraryInfoContributor", InfoContributor.class);
        assertThat(infoContributor).isNotNull();
        assertThat(infoContributor).isInstanceOf(LibraryInfoContributor.class);
    }

    /**
     * Tests conditional bean creation based on properties.
     */
    /*
    [003][M1][基礎Actuator整合]
    input: void
    output: void
    測試基於屬性的條件 Bean 創建
    */
    @Test
    void testConditionalBeanCreation() {
        // All components should be present with current configuration
        assertThat(applicationContext.containsBean("libraryHealthIndicator")).isTrue();
        assertThat(applicationContext.containsBean("libraryInfoContributor")).isTrue();
        assertThat(applicationContext.containsBean("librarySLICollector")).isTrue();
        assertThat(applicationContext.containsBean("libraryMonitoringInterceptor")).isTrue();
    }
}

/**
 * Disabled configuration test to verify conditional loading.
 */
/*
[003][M1][基礎Actuator整合]
禁用配置測試以驗證條件載入
*/
@SpringBootTest(classes = {
    LibraryActuatorAutoConfiguration.class,
    ActuatorTestConfiguration.class
})
@TestPropertySource(properties = {
    "test-library.actuator.enabled=false"
})
class LibraryActuatorDisabledIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Tests that beans are not created when actuator is disabled.
     */
    /*
    [003][M1][基礎Actuator整合]
    input: void
    output: void
    測試 actuator 禁用時不創建 Bean
    */
    @Test
    void testBeansNotCreatedWhenDisabled() {
        // SLI Collector should not be created
        assertThat(applicationContext.getBeansOfType(LibrarySLICollector.class))
            .isEmpty();
            
        // Monitoring Interceptor should not be created
        assertThat(applicationContext.getBeansOfType(LibraryMonitoringInterceptor.class))
            .isEmpty();
    }
}

/**
 * Selective feature configuration test.
 */
/*
[003][M1][基礎Actuator整合]  
選擇性功能配置測試
*/
@SpringBootTest(classes = {
    LibraryActuatorAutoConfiguration.class,
    ActuatorTestConfiguration.class
})
@TestPropertySource(properties = {
    "test-library.actuator.enabled=true",
    "test-library.actuator.health.enabled=false",
    "test-library.actuator.info.enabled=true",
    "test-library.actuator.metrics.enabled=false"
})
class LibraryActuatorSelectiveIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Tests selective feature enabling/disabling.
     */
    /*
    [003][M1][基礎Actuator整合]
    input: void
    output: void
    測試選擇性功能啟用/禁用
    */
    @Test
    void testSelectiveFeatureConfiguration() {
        // Health should be disabled
        assertThat(applicationContext.getBeansOfType(LibraryHealthIndicator.class))
            .isEmpty();
            
        // Info should be enabled
        assertThat(applicationContext.getBeansOfType(LibraryInfoContributor.class))
            .hasSize(1);
            
        // Metrics should be disabled
        assertThat(applicationContext.getBeansOfType(LibrarySLICollector.class))
            .isEmpty();
    }
}