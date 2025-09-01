/*
[002][依賴調整]LibraryInternalIntegrationTest
功能說明: Library內部組件整合測試，驗證AutoConfiguration和組件間依賴注入是否正確
Input: Spring Boot Test環境
Output: 組件整合驗證結果
*/
package com.jamestann.test.library.integration;

import com.jamestann.test.library.config.TestLibraryAutoConfiguration;
import com.jamestann.test.library.config.TestLibraryManager;
import com.jamestann.test.library.config.TestLibraryProperties;
import com.jamestann.test.library.util.SpringBootVersionDetector;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Library內部組件整合測試
 * 測試AutoConfiguration是否正確載入所有組件，以及組件間的依賴注入是否正確
 */
@DisplayName("Library Internal Integration Test")
class LibraryInternalIntegrationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(TestLibraryAutoConfiguration.class));

    @Test
    @DisplayName("Should correctly load all library components through AutoConfiguration")
    void shouldLoadAllLibraryComponents() {
        contextRunner.run(context -> {
            // 驗證所有核心組件都被正確載入
            assertThat(context).hasSingleBean(TestLibraryAutoConfiguration.class);
            assertThat(context).hasSingleBean(TestLibraryManager.class);
            assertThat(context).hasSingleBean(TestLibraryProperties.class);
            assertThat(context).hasSingleBean(SpringBootVersionDetector.class);
        });
    }

    @Test
    @DisplayName("Should bind properties correctly")
    void shouldBindPropertiesCorrectly() {
        contextRunner
                .withPropertyValues(
                        "test.library.enabled=true",
                        "test.library.library-name=integration-test-lib",
                        "test.library.performance-monitoring-enabled=false",
                        "test.library.logging-standardization-enabled=true",
                        "test.library.actuator.custom-endpoints-enabled=false"
                )
                .run(context -> {
                    TestLibraryProperties properties = context.getBean(TestLibraryProperties.class);
                    
                    assertThat(properties.isEnabled()).isTrue();
                    assertThat(properties.getLibraryName()).isEqualTo("integration-test-lib");
                    assertThat(properties.isPerformanceMonitoringEnabled()).isFalse();
                    assertThat(properties.isLoggingStandardizationEnabled()).isTrue();
                    assertThat(properties.getActuator().isCustomEndpointsEnabled()).isFalse();
                });
    }

    @Test
    @DisplayName("Should initialize TestLibraryManager successfully")
    void shouldInitializeTestLibraryManagerSuccessfully() {
        contextRunner.run(context -> {
            TestLibraryManager manager = context.getBean(TestLibraryManager.class);
            
            // 驗證管理器初始化成功
            assertThat(manager).isNotNull();
            assertThat(manager.isEnabled()).isTrue();
            assertThat(manager.getProperties()).isNotNull();
            
            // 驗證版本檢測功能正常
            assertThat(manager.getCurrentSpringBootVersion()).isNotNull();
            assertThat(manager.isCurrentVersionCompatible()).isTrue();
            assertThat(manager.getVersionCompatibilityReport()).isNotNull();
        });
    }

    @Test
    @DisplayName("Should work correctly with version detection mechanism")
    void shouldWorkWithVersionDetectionMechanism() {
        contextRunner.run(context -> {
            SpringBootVersionDetector detector = context.getBean(SpringBootVersionDetector.class);
            
            // 驗證版本檢測器功能
            assertThat(detector.detectSpringBootVersion()).isNotNull();
            assertThat(detector.getSupportedVersions()).isNotEmpty();
            assertThat(detector.getSupportedVersions()).containsAnyOf("2.7", "3.0", "3.1", "3.2");
            
            // 驗證相容性報告
            SpringBootVersionDetector.VersionCompatibilityReport report = detector.getCompatibilityReport();
            assertThat(report).isNotNull();
            assertThat(report.getCurrentVersion()).isNotNull();
            assertThat(report.getMessage()).isNotNull();
            assertThat(report.getSupportedVersions()).isNotEmpty();
        });
    }

    @Test
    @DisplayName("Should handle dependency injection correctly between components")
    void shouldHandleDependencyInjectionCorrectly() {
        contextRunner.run(context -> {
            TestLibraryManager manager = context.getBean(TestLibraryManager.class);
            SpringBootVersionDetector detector = context.getBean(SpringBootVersionDetector.class);
            
            // 驗證Manager中的version detector是同一個Bean
            SpringBootVersionDetector.VersionCompatibilityReport managerReport = manager.getVersionCompatibilityReport();
            SpringBootVersionDetector.VersionCompatibilityReport detectorReport = detector.getCompatibilityReport();
            
            assertThat(managerReport.getCurrentVersion()).isEqualTo(detectorReport.getCurrentVersion());
            assertThat(managerReport.isCompatible()).isEqualTo(detectorReport.isCompatible());
            assertThat(managerReport.getMessage()).isEqualTo(detectorReport.getMessage());
        });
    }

    @Test
    @DisplayName("Should respect conditional configuration when library is disabled")
    void shouldRespectConditionalConfigurationWhenDisabled() {
        contextRunner
                .withPropertyValues("test.library.enabled=false")
                .run(context -> {
                    // 當library disabled時，AutoConfiguration不應該被載入
                    assertThat(context).doesNotHaveBean(TestLibraryAutoConfiguration.class);
                    assertThat(context).doesNotHaveBean(TestLibraryManager.class);
                    
                    // SpringBootVersionDetector也不會被載入，因為ComponentScan在AutoConfiguration上
                    assertThat(context).doesNotHaveBean(SpringBootVersionDetector.class);
                });
    }

    @Test
    @DisplayName("Should work with default properties when no configuration provided")
    void shouldWorkWithDefaultProperties() {
        contextRunner.run(context -> {
            TestLibraryProperties properties = context.getBean(TestLibraryProperties.class);
            
            // 驗證默認配置值
            assertThat(properties.isEnabled()).isTrue(); // matchIfMissing = true
            assertThat(properties.getLibraryName()).isEqualTo("test-library");
            assertThat(properties.isPerformanceMonitoringEnabled()).isTrue();
            assertThat(properties.isLoggingStandardizationEnabled()).isTrue();
            assertThat(properties.getActuator().isCustomEndpointsEnabled()).isTrue();
            assertThat(properties.getActuator().getEndpointPathPrefix()).isEqualTo("test-library");
        });
    }

    @Test
    @DisplayName("Should properly integrate all components with Spring context lifecycle")
    void shouldIntegrateWithSpringContextLifecycle() {
        contextRunner.run(context -> {
            // 驗證所有beans都正確註冊到Spring context中
            ApplicationContext springContext = context.getSourceApplicationContext();
            
            assertThat(springContext.getBeanDefinitionNames())
                    .contains("testLibraryAutoConfiguration", "testLibraryManager");
            
            // 驗證Bean的生命周期方法被正確調用
            TestLibraryManager manager = context.getBean(TestLibraryManager.class);
            assertThat(manager.isEnabled()).isTrue(); // afterPropertiesSet已被調用
        });
    }
}