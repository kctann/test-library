package com.jamestann.test.library.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health indicator for client application server enhanced by Test Library.
 * <p>
 * Provides comprehensive health check information for the client server application
 * including JVM metrics, performance indicators, and system resource status.
 * 
 * @author James Tann
 * @since 1.0.0
 */
/*
[003][M1][Health Check]
客戶端應用程式伺服器的健康檢查指標，由 Test Library 增強
提供客戶端伺服器的 JVM 指標、效能指標和系統資源狀態
*/
public class LibraryHealthIndicator implements HealthIndicator {

    private final LibraryActuatorProperties properties;
    private final MemoryMXBean memoryMXBean;
    private final RuntimeMXBean runtimeMXBean;

    /**
     * Creates a new LibraryHealthIndicator.
     * 
     * @param properties the Library actuator properties
     */
    public LibraryHealthIndicator(LibraryActuatorProperties properties) {
        this.properties = properties;
        this.memoryMXBean = ManagementFactory.getMemoryMXBean();
        this.runtimeMXBean = ManagementFactory.getRuntimeMXBean();
    }

    /**
     * Performs health check and returns health status.
     * <p>
     * Checks various aspects of the client server application including
     * JVM health, memory usage, uptime, and system performance indicators.
     * 
     * @return Health status with detailed server information
     */
    /*
    [003][M1][Health Check]
    input: void
    output: Health
    執行健康檢查並返回健康狀態，檢查客戶端伺服器的 JVM、記憶體、運行時間等
    */
    @Override
    public Health health() {
        try {
            Health.Builder healthBuilder = Health.up();
            
            // Add server basic information
            healthBuilder.withDetail("server", createServerInfo());
            
            // Add detailed metrics if enabled
            if (properties.getHealth().isShowDetails()) {
                healthBuilder.withDetail("jvm", createJvmHealth());
                healthBuilder.withDetail("memory", createMemoryHealth());
                healthBuilder.withDetail("performance", createPerformanceHealth());
                healthBuilder.withDetail("monitoring", createMonitoringStatus());
            }
            
            return healthBuilder.build();
            
        } catch (Exception ex) {
            return Health.down()
                    .withDetail("error", ex.getMessage())
                    .withDetail("server", createServerInfo())
                    .build();
        }
    }

    /**
     * Creates basic server information.
     * 
     * @return Server information map
     */
    /*
    [003][M1][Health Check]
    input: void
    output: Map<String, Object>
    創建客戶端伺服器基本資訊
    */
    private Map<String, Object> createServerInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", getApplicationName());
        info.put("uptime", Duration.ofMillis(runtimeMXBean.getUptime()).toString());
        info.put("startTime", LocalDateTime.now().minusSeconds(runtimeMXBean.getUptime() / 1000).toString());
        info.put("pid", runtimeMXBean.getName().split("@")[0]);
        return info;
    }

    /**
     * Creates JVM health information.
     * 
     * @return JVM health map
     */
    /*
    [003][M1][Health Check]
    input: void
    output: Map<String, Object>
    創建 JVM 健康資訊
    */
    private Map<String, Object> createJvmHealth() {
        Map<String, Object> jvm = new HashMap<>();
        
        jvm.put("vendor", System.getProperty("java.vm.vendor"));
        jvm.put("name", System.getProperty("java.vm.name"));
        jvm.put("version", System.getProperty("java.version"));
        jvm.put("runtime", System.getProperty("java.runtime.version"));
        
        // JVM arguments
        jvm.put("inputArguments", runtimeMXBean.getInputArguments());
        
        return jvm;
    }

    /**
     * Creates memory health information.
     * 
     * @return Memory health map
     */
    /*
    [003][M1][Health Check]
    input: void
    output: Map<String, Object>
    創建記憶體健康資訊
    */
    private Map<String, Object> createMemoryHealth() {
        Map<String, Object> memory = new HashMap<>();
        
        // Heap memory
        long heapUsed = memoryMXBean.getHeapMemoryUsage().getUsed();
        long heapMax = memoryMXBean.getHeapMemoryUsage().getMax();
        double heapUsedPercent = heapMax > 0 ? (double) heapUsed / heapMax * 100 : 0;
        
        memory.put("heap", Map.of(
            "used", heapUsed,
            "max", heapMax,
            "usedPercent", Math.round(heapUsedPercent * 100.0) / 100.0,
            "status", heapUsedPercent > 90 ? "WARNING" : "OK"
        ));
        
        // Non-heap memory
        long nonHeapUsed = memoryMXBean.getNonHeapMemoryUsage().getUsed();
        long nonHeapMax = memoryMXBean.getNonHeapMemoryUsage().getMax();
        
        memory.put("nonHeap", Map.of(
            "used", nonHeapUsed,
            "max", nonHeapMax > 0 ? nonHeapMax : -1
        ));
        
        return memory;
    }

    /**
     * Creates performance health information.
     * 
     * @return Performance health map
     */
    /*
    [003][M1][Health Check]
    input: void
    output: Map<String, Object>
    創建效能健康資訊
    */
    private Map<String, Object> createPerformanceHealth() {
        Map<String, Object> performance = new HashMap<>();
        
        // CPU information
        int processors = Runtime.getRuntime().availableProcessors();
        performance.put("processors", processors);
        
        // Load average (Unix systems)
        try {
            double loadAverage = ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
            if (loadAverage >= 0) {
                performance.put("loadAverage", loadAverage);
                performance.put("loadStatus", loadAverage > processors ? "HIGH" : "OK");
            }
        } catch (Exception e) {
            // Load average not available on all systems
        }
        
        // GC information
        performance.put("gcCount", ManagementFactory.getGarbageCollectorMXBeans().stream()
                .mapToLong(gc -> gc.getCollectionCount())
                .sum());
        
        performance.put("gcTime", ManagementFactory.getGarbageCollectorMXBeans().stream()
                .mapToLong(gc -> gc.getCollectionTime())
                .sum());
        
        return performance;
    }

    /**
     * Creates monitoring status information.
     * 
     * @return Monitoring status map
     */
    /*
    [003][M1][Health Check]
    input: void
    output: Map<String, Object>
    創建監控狀態資訊
    */
    private Map<String, Object> createMonitoringStatus() {
        Map<String, Object> monitoring = new HashMap<>();
        
        monitoring.put("libraryEnabled", properties.isEnabled());
        monitoring.put("healthEnabled", properties.getHealth().isEnabled());
        monitoring.put("infoEnabled", properties.getInfo().isEnabled());
        monitoring.put("metricsEnabled", properties.getMetrics().isEnabled());
        
        // SLI collection status
        monitoring.put("sliCollection", Map.of(
            "enabled", properties.getMetrics().isEnabled(),
            "percentiles", properties.getMetrics().isIncludePercentiles(),
            "packageMonitoring", !properties.getMonitoring().getIncludePackages().isEmpty() || 
                               !properties.getMonitoring().getExcludePackages().isEmpty()
        ));
        
        return monitoring;
    }

    /**
     * Gets application name from system properties or environment.
     * 
     * @return Application name or default
     */
    /*
    [003][M1][Health Check]
    input: void
    output: String
    從系統屬性或環境變數獲取應用程式名稱
    */
    private String getApplicationName() {
        String appName = System.getProperty("spring.application.name");
        if (appName != null && !appName.isEmpty()) {
            return appName;
        }
        
        appName = System.getenv("SPRING_APPLICATION_NAME");
        if (appName != null && !appName.isEmpty()) {
            return appName;
        }
        
        return "client-application";
    }
}