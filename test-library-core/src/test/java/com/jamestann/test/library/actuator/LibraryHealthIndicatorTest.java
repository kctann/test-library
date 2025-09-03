/*
[003][M1][Health Check]
Library Health Indicator測試類別
測試客戶端伺服器健康檢查功能
*/
package com.jamestann.test.library.actuator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for LibraryHealthIndicator.
 * <p>
 * Tests the health check functionality for client server monitoring
 * including JVM metrics, memory usage, and performance indicators.
 * 
 * @author James Tann
 * @since 1.0.0
 */
/*
[003][M1][Health Check]
LibraryHealthIndicator 的單元測試
測試客戶端伺服器監控的健康檢查功能
*/
class LibraryHealthIndicatorTest {

    private LibraryActuatorProperties properties;
    private LibraryHealthIndicator healthIndicator;

    /**
     * Sets up test fixtures before each test.
     */
    /*
    [003][M1][Health Check]
    input: void
    output: void
    在每個測試前設置測試裝置
    */
    @BeforeEach
    void setUp() {
        properties = new LibraryActuatorProperties();
        healthIndicator = new LibraryHealthIndicator(properties);
    }

    /**
     * Tests health check returns UP status when enabled.
     */
    /*
    [003][M1][Health Check]
    input: void
    output: void
    測試健康檢查在啟用時返回 UP 狀態
    */
    @Test
    void testHealthCheckUp() {
        properties.getHealth().setEnabled(true);
        
        Health health = healthIndicator.health();
        
        assertThat(health.getStatus()).isEqualTo(Status.UP);
        assertThat(health.getDetails()).isNotEmpty();
        assertThat(health.getDetails()).containsKeys(
            "library.version", 
            "jvm.version", 
            "jvm.memory.used", 
            "jvm.memory.max"
        );
    }

    /**
     * Tests health check includes JVM information.
     */
    /*
    [003][M1][Health Check]
    input: void
    output: void
    測試健康檢查包含 JVM 資訊
    */
    @Test
    void testHealthCheckIncludesJvmInfo() {
        properties.getHealth().setEnabled(true);
        
        Health health = healthIndicator.health();
        
        assertThat(health.getDetails()).containsKey("jvm.version");
        assertThat(health.getDetails()).containsKey("jvm.memory.used");
        assertThat(health.getDetails()).containsKey("jvm.memory.max");
        assertThat(health.getDetails()).containsKey("jvm.memory.free");
        
        // Verify values are reasonable
        Long memoryUsed = (Long) health.getDetails().get("jvm.memory.used");
        Long memoryMax = (Long) health.getDetails().get("jvm.memory.max");
        
        assertThat(memoryUsed).isGreaterThan(0L);
        assertThat(memoryMax).isGreaterThan(memoryUsed);
    }

    /**
     * Tests health check includes system performance metrics.
     */
    /*
    [003][M1][Health Check]
    input: void
    output: void
    測試健康檢查包含系統效能指標
    */
    @Test
    void testHealthCheckIncludesPerformanceMetrics() {
        properties.getHealth().setEnabled(true);
        
        Health health = healthIndicator.health();
        
        assertThat(health.getDetails()).containsKey("system.cpu.count");
        assertThat(health.getDetails()).containsKey("system.uptime");
        
        Integer cpuCount = (Integer) health.getDetails().get("system.cpu.count");
        Long uptime = (Long) health.getDetails().get("system.uptime");
        
        assertThat(cpuCount).isGreaterThan(0);
        assertThat(uptime).isGreaterThanOrEqualTo(0L);
    }

    /**
     * Tests health check includes library version information.
     */
    /*
    [003][M1][Health Check]
    input: void
    output: void
    測試健康檢查包含函式庫版本資訊
    */
    @Test
    void testHealthCheckIncludesLibraryVersion() {
        properties.getHealth().setEnabled(true);
        
        Health health = healthIndicator.health();
        
        assertThat(health.getDetails()).containsKey("library.version");
        assertThat(health.getDetails()).containsKey("library.monitoring.enabled");
        
        String version = (String) health.getDetails().get("library.version");
        Boolean monitoringEnabled = (Boolean) health.getDetails().get("library.monitoring.enabled");
        
        assertThat(version).isNotNull();
        assertThat(monitoringEnabled).isTrue();
    }

    /**
     * Tests health check when disabled.
     */
    /*
    [003][M1][Health Check]
    input: void
    output: void
    測試健康檢查在禁用時的行為
    */
    @Test
    void testHealthCheckWhenDisabled() {
        properties.getHealth().setEnabled(false);
        
        Health health = healthIndicator.health();
        
        assertThat(health.getStatus()).isEqualTo(Status.UP);
        assertThat(health.getDetails()).containsKey("status");
        assertThat(health.getDetails().get("status")).isEqualTo("disabled");
    }

    /**
     * Tests health check handles exceptions gracefully.
     */
    /*
    [003][M1][Health Check]
    input: void
    output: void
    測試健康檢查優雅處理異常
    */
    @Test
    void testHealthCheckHandlesExceptions() {
        properties.getHealth().setEnabled(true);
        
        // This should not throw exceptions even if system calls fail
        Health health = healthIndicator.health();
        
        assertThat(health.getStatus()).isIn(Status.UP, Status.DOWN);
        assertThat(health.getDetails()).isNotNull();
    }

    /**
     * Tests health check details are properly formatted.
     */
    /*
    [003][M1][Health Check]
    input: void
    output: void
    測試健康檢查詳細資訊格式正確
    */
    @Test
    void testHealthCheckDetailsFormatting() {
        properties.getHealth().setEnabled(true);
        
        Health health = healthIndicator.health();
        
        // Check that numeric values are properly formatted
        Object memoryUsed = health.getDetails().get("jvm.memory.used");
        Object memoryMax = health.getDetails().get("jvm.memory.max");
        
        assertThat(memoryUsed).isInstanceOf(Long.class);
        assertThat(memoryMax).isInstanceOf(Long.class);
        
        // Check string values are not empty
        String jvmVersion = (String) health.getDetails().get("jvm.version");
        String libraryVersion = (String) health.getDetails().get("library.version");
        
        assertThat(jvmVersion).isNotBlank();
        assertThat(libraryVersion).isNotBlank();
    }
}