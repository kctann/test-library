/*
[003][M1][基礎Actuator整合]
測試配置類別
為整合測試提供必要的 Bean 配置
*/
package com.jamestann.test.library.actuator;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;

/**
 * Test configuration for LibraryActuator integration tests.
 * <p>
 * Provides necessary beans for testing actuator functionality
 * including MeterRegistry and Environment mocks.
 * 
 * @author James Tann
 * @since 1.0.0
 */
/*
[003][M1][基礎Actuator整合]
LibraryActuator 整合測試的測試配置
提供測試 actuator 功能所需的 Bean
*/
@TestConfiguration
public class ActuatorTestConfiguration {

    /**
     * Provides a simple MeterRegistry for testing.
     * 
     * @return SimpleMeterRegistry instance
     */
    /*
    [003][M1][基礎Actuator整合]
    input: void
    output: SimpleMeterRegistry
    為測試提供簡單的 MeterRegistry
    */
    @Bean
    @Primary
    public MeterRegistry meterRegistry() {
        return new SimpleMeterRegistry();
    }

    /**
     * Provides a mock Environment for testing.
     * 
     * @return MockEnvironment instance with test properties
     */
    /*
    [003][M1][基礎Actuator整合]
    input: void
    output: MockEnvironment
    為測試提供模擬的 Environment
    */
    @Bean
    @Primary
    public Environment environment() {
        MockEnvironment mockEnvironment = new MockEnvironment();
        mockEnvironment.setProperty("spring.application.name", "test-application");
        mockEnvironment.setProperty("server.port", "8080");
        mockEnvironment.setActiveProfiles("test");
        return mockEnvironment;
    }
}