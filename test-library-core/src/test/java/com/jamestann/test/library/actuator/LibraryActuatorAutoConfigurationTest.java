/*
[003][M1][基礎Actuator整合]
Library Actuator Auto Configuration測試類別
測試 Library Actuator 自動配置功能和條件化載入
*/
package com.jamestann.test.library.actuator;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.core.env.Environment;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for LibraryActuatorAutoConfiguration.
 * <p>
 * Tests the conditional loading and bean creation of Library Actuator components
 * including Health, Info, and Metrics configurations.
 * 
 * @author James Tann
 * @since 1.0.0
 */
/*
[003][M1][基礎Actuator整合]
LibraryActuatorAutoConfiguration 的單元測試
測試 Library Actuator 元件的條件載入和 Bean 創建
*/
class LibraryActuatorAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(LibraryActuatorAutoConfiguration.class))
        .withBean(MeterRegistry.class, SimpleMeterRegistry::new);

    /**
     * Tests that actuator components are created when enabled.
     */
    /*
    [003][M1][基礎Actuator整合]
    input: void
    output: void
    測試當啟用時創建 actuator 元件
    */
    @Test
    void testActuatorAutoConfigurationEnabled() {
        contextRunner
            .withPropertyValues("test-library.actuator.enabled=true")
            .run(context -> {
                assertThat(context).hasSingleBean(LibraryActuatorProperties.class);
                assertThat(context).hasSingleBean(LibrarySLICollector.class);
            });
    }

    /**
     * Tests that actuator components are not created when disabled.
     */
    /*
    [003][M1][基礎Actuator整合]
    input: void
    output: void
    測試當禁用時不創建 actuator 元件
    */
    @Test
    void testActuatorAutoConfigurationDisabled() {
        contextRunner
            .withPropertyValues("test-library.actuator.enabled=false")
            .run(context -> {
                assertThat(context).doesNotHaveBean(LibrarySLICollector.class);
            });
    }

    /**
     * Tests health indicator configuration when enabled.
     */
    /*
    [003][M1][基礎Actuator整合]
    input: void
    output: void
    測試健康指標配置當啟用時
    */
    @Test
    void testHealthIndicatorEnabled() {
        contextRunner
            .withPropertyValues(
                "test-library.actuator.enabled=true",
                "test-library.actuator.health.enabled=true"
            )
            .run(context -> {
                assertThat(context).hasSingleBean(LibraryHealthIndicator.class);
                
                LibraryHealthIndicator healthIndicator = 
                    context.getBean(LibraryHealthIndicator.class);
                assertThat(healthIndicator).isNotNull();
            });
    }

    /**
     * Tests health indicator is not created when disabled.
     */
    /*
    [003][M1][基礎Actuator整合]
    input: void
    output: void
    測試健康指標在禁用時不創建
    */
    @Test
    void testHealthIndicatorDisabled() {
        contextRunner
            .withPropertyValues(
                "test-library.actuator.enabled=true",
                "test-library.actuator.health.enabled=false"
            )
            .run(context -> {
                assertThat(context).doesNotHaveBean(LibraryHealthIndicator.class);
            });
    }

    /**
     * Tests info contributor configuration when enabled.
     */
    /*
    [003][M1][基礎Actuator整合]
    input: void
    output: void
    測試資訊貢獻者配置當啟用時
    */
    @Test
    void testInfoContributorEnabled() {
        contextRunner
            .withPropertyValues(
                "test-library.actuator.enabled=true",
                "test-library.actuator.info.enabled=true"
            )
            .run(context -> {
                assertThat(context).hasSingleBean(LibraryInfoContributor.class);
                
                LibraryInfoContributor infoContributor = 
                    context.getBean(LibraryInfoContributor.class);
                assertThat(infoContributor).isNotNull();
            });
    }

    /**
     * Tests metrics configuration when enabled.
     */
    /*
    [003][M1][基礎Actuator整合]
    input: void
    output: void
    測試指標配置當啟用時
    */
    @Test
    void testMetricsConfigurationEnabled() {
        contextRunner
            .withPropertyValues(
                "test-library.actuator.enabled=true",
                "test-library.actuator.metrics.enabled=true",
                "test-library.actuator.monitoring.http-interceptor.enabled=true"
            )
            .run(context -> {
                assertThat(context).hasSingleBean(LibrarySLICollector.class);
                assertThat(context).hasSingleBean(LibraryMonitoringInterceptor.class);
                
                LibrarySLICollector sliCollector = context.getBean(LibrarySLICollector.class);
                assertThat(sliCollector).isNotNull();
            });
    }

    /**
     * Tests AOP aspect configuration when enabled.
     */
    /*
    [003][M1][基礎Actuator整合]
    input: void
    output: void
    測試 AOP 切面配置當啟用時
    */
    @Test
    void testAopAspectEnabled() {
        contextRunner
            .withPropertyValues(
                "test-library.actuator.enabled=true",
                "test-library.actuator.metrics.enabled=true",
                "test-library.actuator.aop.enabled=true"
            )
            .run(context -> {
                assertThat(context).hasSingleBean(LibraryMetricsAspect.class);
                
                LibraryMetricsAspect aspect = context.getBean(LibraryMetricsAspect.class);
                assertThat(aspect).isNotNull();
            });
    }

    /**
     * Tests AOP aspect is not created when disabled.
     */
    /*
    [003][M1][基礎Actuator整合]
    input: void
    output: void
    測試 AOP 切面在禁用時不創建
    */
    @Test
    void testAopAspectDisabled() {
        contextRunner
            .withPropertyValues(
                "test-library.actuator.enabled=true",
                "test-library.actuator.metrics.enabled=true",
                "test-library.actuator.aop.enabled=false"
            )
            .run(context -> {
                assertThat(context).doesNotHaveBean(LibraryMetricsAspect.class);
            });
    }

    /**
     * Tests default configuration values.
     */
    /*
    [003][M1][基礎Actuator整合]
    input: void
    output: void
    測試預設配置值
    */
    @Test
    void testDefaultConfiguration() {
        contextRunner
            .run(context -> {
                assertThat(context).hasSingleBean(LibraryActuatorProperties.class);
                
                LibraryActuatorProperties properties = 
                    context.getBean(LibraryActuatorProperties.class);
                
                assertThat(properties.isEnabled()).isTrue();
                assertThat(properties.getHealth().isEnabled()).isTrue();
                assertThat(properties.getInfo().isEnabled()).isTrue();
                assertThat(properties.getMetrics().isEnabled()).isTrue();
            });
    }
}