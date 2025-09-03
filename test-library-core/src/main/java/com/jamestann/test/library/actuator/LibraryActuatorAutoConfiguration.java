package com.jamestann.test.library.actuator;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.actuate.autoconfigure.info.ConditionalOnEnabledInfoContributor;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Auto-configuration for Library Actuator features.
 * <p>
 * This configuration is conditionally loaded based on the presence of Spring Boot Actuator
 * and enables Library-specific monitoring and management capabilities.
 * 
 * @author James Tann
 * @since 1.0.0
 */
/*
[003][M1][基礎Actuator整合]
Library Actuator 功能的自動配置類，根據 Spring Boot Actuator 的存在情況條件化載入
Library 特定的監控和管理功能
*/
@AutoConfiguration
@ConditionalOnClass({
    HealthIndicator.class,
    InfoContributor.class,
    MeterRegistry.class
})
@ConditionalOnProperty(
    name = "test-library.actuator.enabled",
    havingValue = "true",
    matchIfMissing = true
)
@EnableConfigurationProperties(LibraryActuatorProperties.class)
public class LibraryActuatorAutoConfiguration {

    /**
     * Configuration for Health Check features.
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(HealthIndicator.class)
    static class HealthConfiguration {

        /**
         * Creates LibraryHealthIndicator bean.
         * <p>
         * This bean is conditionally created when health indicator is enabled
         * and provides Library-specific health check information.
         * 
         * @param properties the Library actuator properties
         * @return configured LibraryHealthIndicator instance
         */
        /*
        [003][M1][Health Check]
        input: LibraryActuatorProperties
        output: LibraryHealthIndicator bean
        條件化創建 Library 健康檢查指標
        */
        @Bean
        @ConditionalOnMissingBean(name = "libraryHealthIndicator")
        @ConditionalOnEnabledHealthIndicator("library")
        @ConditionalOnProperty(
            name = "test-library.actuator.health.enabled",
            havingValue = "true",
            matchIfMissing = true
        )
        public LibraryHealthIndicator libraryHealthIndicator(LibraryActuatorProperties properties) {
            return new LibraryHealthIndicator(properties);
        }
    }

    /**
     * Configuration for Info Contributor features.
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(InfoContributor.class)
    static class InfoConfiguration {

        /**
         * Creates LibraryInfoContributor bean.
         * <p>
         * This bean is conditionally created when info contributor is enabled
         * and provides Library-specific information to the actuator info endpoint.
         * 
         * @param properties the Library actuator properties
         * @return configured LibraryInfoContributor instance
         */
        /*
        [003][M1][Info Contributor]
        input: LibraryActuatorProperties, Environment
        output: LibraryInfoContributor bean
        條件化創建 Library 資訊貢獻者
        */
        @Bean
        @ConditionalOnMissingBean(name = "libraryInfoContributor")
        @ConditionalOnEnabledInfoContributor("library")
        @ConditionalOnProperty(
            name = "test-library.actuator.info.enabled",
            havingValue = "true",
            matchIfMissing = true
        )
        public LibraryInfoContributor libraryInfoContributor(LibraryActuatorProperties properties, 
                                                           org.springframework.core.env.Environment environment) {
            return new LibraryInfoContributor(properties, environment);
        }
    }

    /**
     * Configuration for SLI Metrics collection features.
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(MeterRegistry.class)
    @ConditionalOnProperty(
        name = "test-library.actuator.metrics.enabled",
        havingValue = "true",
        matchIfMissing = true
    )
    static class MetricsConfiguration {

        /**
         * Creates MonitoringConfiguration bean.
         * <p>
         * This bean handles the logic for determining which methods should be monitored
         * based on package configuration, annotations, and Spring stereotypes.
         * 
         * @param properties the Library actuator properties
         * @return configured MonitoringConfiguration instance
         */
        /*
        [003][M1][監控配置系統]
        input: LibraryActuatorProperties
        output: MonitoringConfiguration bean
        創建監控配置處理器，處理基於包和註解的監控邏輯
        */
        @Bean
        @ConditionalOnMissingBean
        public MonitoringConfiguration monitoringConfiguration(LibraryActuatorProperties properties) {
            return new MonitoringConfiguration(properties.getMonitoring());
        }

        /**
         * Creates LibrarySLICollector bean.
         * <p>
         * This bean is responsible for collecting Golden Signals metrics
         * (Latency, Traffic, Errors, Saturation) from the application.
         * 
         * @param meterRegistry the Micrometer MeterRegistry
         * @param properties the Library actuator properties
         * @return configured LibrarySLICollector instance
         */
        /*
        [003][M1][SLI數據收集]
        input: MeterRegistry, LibraryActuatorProperties
        output: LibrarySLICollector bean
        創建 SLI 數據收集器，負責收集 Golden Signals 指標
        */
        @Bean
        @ConditionalOnMissingBean
        public LibrarySLICollector librarySLICollector(
                MeterRegistry meterRegistry,
                LibraryActuatorProperties properties) {
            return new LibrarySLICollector(meterRegistry, properties);
        }

        /**
         * Configuration for optional Web request monitoring.
         * <p>
         * This provides basic HTTP-level statistics as a complement to the main
         * annotation-based monitoring. It's disabled by default in favor of
         * more precise AOP-based monitoring.
         */
        @Configuration(proxyBeanMethods = false)
        @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
        @ConditionalOnProperty(
            name = "test-library.actuator.monitoring.http-interceptor.enabled",
            havingValue = "true",
            matchIfMissing = false
        )
        static class WebMonitoringConfiguration implements WebMvcConfigurer {

            private final LibrarySLICollector sliCollector;
            private final LibraryActuatorProperties properties;

            public WebMonitoringConfiguration(
                    LibrarySLICollector sliCollector,
                    LibraryActuatorProperties properties) {
                this.sliCollector = sliCollector;
                this.properties = properties;
            }

            /**
             * Creates LibraryMonitoringInterceptor bean.
             * <p>
             * This interceptor provides basic HTTP request statistics.
             * For detailed monitoring, use annotation-based monitoring instead.
             * 
             * @return configured LibraryMonitoringInterceptor instance
             */
            /*
            [003][M1][AOP攔截器]
            input: LibrarySLICollector, LibraryActuatorProperties
            output: LibraryMonitoringInterceptor bean
            創建可選的 Web 請求監控攔截器，提供基礎 HTTP 統計
            */
            @Bean
            @ConditionalOnMissingBean
            public LibraryMonitoringInterceptor libraryMonitoringInterceptor() {
                return new LibraryMonitoringInterceptor(sliCollector, properties);
            }

            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(libraryMonitoringInterceptor())
                        .addPathPatterns("/**");
            }
        }

        /**
         * Configuration for AOP-based method monitoring.
         */
        @Configuration(proxyBeanMethods = false)
        @ConditionalOnClass(name = "org.aspectj.lang.annotation.Aspect")
        static class AopMonitoringConfiguration {

            /**
             * Creates LibraryMetricsAspect bean.
             * <p>
             * This aspect provides fine-grained method-level monitoring capabilities
             * using AspectJ annotations and pointcuts with package-based configuration.
             * 
             * @param sliCollector the SLI collector instance
             * @param monitoringConfig the monitoring configuration for package-based control
             * @return configured LibraryMetricsAspect instance
             */
            /*
            [003][M1][Metrics AOP切面]
            input: LibrarySLICollector, MonitoringConfiguration
            output: LibraryMetricsAspect bean
            創建 AOP 切面，提供基於包配置的方法層級監控功能
            */
            @Bean
            @ConditionalOnMissingBean
            @ConditionalOnProperty(
                name = "test-library.actuator.aop.enabled",
                havingValue = "true",
                matchIfMissing = false
            )
            public LibraryMetricsAspect libraryMetricsAspect(LibrarySLICollector sliCollector,
                                                           MonitoringConfiguration monitoringConfig) {
                return new LibraryMetricsAspect(sliCollector, monitoringConfig);
            }
        }
    }
}