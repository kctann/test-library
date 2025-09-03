/*
[003][M1][Demo Controller]
健康檢查測試Controller
測試Health Check功能和健康狀態監控
*/
package com.jamestann.test.library.demo.controller;

import com.jamestann.test.library.actuator.annotation.IncludeMonitoring;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 健康檢查測試Controller - 測試各種健康檢查功能
 * <p>
 * 提供多種健康檢查測試端點來驗證：
 * - 應用程式健康狀態監控
 * - 依賴服務健康檢查
 * - 自定義健康指標
 * - 健康狀態變更測試
 * - 健康檢查性能監控
 * 
 * @author James Tann
 * @since 1.0.0
 */
/*
[003][M1][Demo Controller]
健康檢查測試控制器，提供各種健康狀態測試端點
驗證Health Check功能和健康監控
*/
@Slf4j
@RestController
@RequestMapping("/api/health")
public class HealthTestController {

    private final TestHealthIndicator testHealthIndicator;
    private final DatabaseHealthSimulator databaseHealth;
    private final ExternalServiceHealthSimulator externalServiceHealth;

    public HealthTestController(TestHealthIndicator testHealthIndicator,
                              DatabaseHealthSimulator databaseHealth,
                              ExternalServiceHealthSimulator externalServiceHealth) {
        this.testHealthIndicator = testHealthIndicator;
        this.databaseHealth = databaseHealth;
        this.externalServiceHealth = externalServiceHealth;
    }

    /**
     * 獲取應用程式整體健康狀態
     */
    /*
    [003][M1][Demo Controller]
    input: void
    output: Map<String, Object>
    獲取應用程式整體健康狀態
    */
    @GetMapping("/status")
    @IncludeMonitoring(
        name = "health.status.check",
        description = "Overall application health status check",
        tags = {"operation=read", "type=health", "scope=application"}
    )
    public Map<String, Object> getHealthStatus() {
        log.info("Health status check requested");
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("application", "test-library-demo");
        
        // 檢查各個組件的健康狀態
        Map<String, Object> components = new HashMap<>();
        
        // Test Health Indicator
        Health testHealth = testHealthIndicator.health();
        components.put("testComponent", Map.of(
            "status", testHealth.getStatus().getCode(),
            "details", testHealth.getDetails()
        ));
        
        // Database Health
        Health dbHealth = databaseHealth.health();
        components.put("database", Map.of(
            "status", dbHealth.getStatus().getCode(),
            "details", dbHealth.getDetails()
        ));
        
        // External Service Health
        Health externalHealth = externalServiceHealth.health();
        components.put("externalService", Map.of(
            "status", externalHealth.getStatus().getCode(),
            "details", externalHealth.getDetails()
        ));
        
        response.put("components", components);
        
        // 計算整體狀態
        boolean allHealthy = components.values().stream()
            .allMatch(component -> {
                @SuppressWarnings("unchecked")
                Map<String, Object> comp = (Map<String, Object>) component;
                return "UP".equals(comp.get("status"));
            });
            
        response.put("overallStatus", allHealthy ? "UP" : "DOWN");
        
        return response;
    }

    /**
     * 測試健康檢查性能
     */
    /*
    [003][M1][Demo Controller]
    input: int iterations
    output: Map<String, Object>
    測試健康檢查的性能和響應時間
    */
    @GetMapping("/performance")
    @IncludeMonitoring(
        name = "health.performance.test",
        description = "Health check performance test",
        tags = {"operation=test", "type=health", "category=performance"}
    )
    public Map<String, Object> healthPerformanceTest(@RequestParam(defaultValue = "10") int iterations) {
        log.info("Health performance test - iterations: {}", iterations);
        
        long startTime = System.nanoTime();
        
        long totalHealthCheckTime = 0;
        int successCount = 0;
        
        for (int i = 0; i < iterations; i++) {
            long checkStart = System.nanoTime();
            
            try {
                // 執行健康檢查
                Health health = testHealthIndicator.health();
                if (health.getStatus().getCode().equals("UP")) {
                    successCount++;
                }
            } catch (Exception e) {
                log.warn("Health check iteration {} failed: {}", i, e.getMessage());
            }
            
            long checkEnd = System.nanoTime();
            totalHealthCheckTime += (checkEnd - checkStart);
        }
        
        long endTime = System.nanoTime();
        
        double totalTimeMs = (endTime - startTime) / 1_000_000.0;
        double avgHealthCheckTimeMs = (totalHealthCheckTime / iterations) / 1_000_000.0;
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "completed");
        response.put("iterations", iterations);
        response.put("successCount", successCount);
        response.put("totalTimeMs", totalTimeMs);
        response.put("averageHealthCheckTimeMs", avgHealthCheckTimeMs);
        response.put("timestamp", LocalDateTime.now());
        
        return response;
    }

    /**
     * 模擬健康狀態變更
     */
    /*
    [003][M1][Demo Controller]
    input: String component, boolean healthy
    output: Map<String, Object>
    模擬指定組件的健康狀態變更
    */
    @PostMapping("/simulate/{component}")
    @IncludeMonitoring(
        name = "health.simulate.change",
        description = "Simulate health status change for component",
        tags = {"operation=write", "type=health", "category=simulation"}
    )
    public Map<String, Object> simulateHealthChange(
            @PathVariable String component,
            @RequestParam boolean healthy) {
        
        log.info("Simulating health change - component: {}, healthy: {}", component, healthy);
        
        switch (component.toLowerCase()) {
            case "test":
                testHealthIndicator.setHealthy(healthy);
                break;
            case "database":
                databaseHealth.setHealthy(healthy);
                break;
            case "external":
                externalServiceHealth.setHealthy(healthy);
                break;
            default:
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Unknown component: " + component);
                errorResponse.put("availableComponents", "test, database, external");
                return errorResponse;
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "updated");
        response.put("component", component);
        response.put("newHealthStatus", healthy ? "UP" : "DOWN");
        response.put("timestamp", LocalDateTime.now());
        
        return response;
    }

    /**
     * 獲取健康檢查統計資訊
     */
    /*
    [003][M1][Demo Controller]
    input: void
    output: Map<String, Object>
    獲取健康檢查的統計資訊
    */
    @GetMapping("/statistics")
    @IncludeMonitoring(
        name = "health.statistics.get",
        description = "Get health check statistics",
        tags = {"operation=read", "type=health", "category=statistics"}
    )
    public Map<String, Object> getHealthStatistics() {
        log.info("Health statistics requested");
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        
        // Test Health Indicator 統計
        Map<String, Object> testStats = new HashMap<>();
        testStats.put("checkCount", testHealthIndicator.getCheckCount());
        testStats.put("currentStatus", testHealthIndicator.health().getStatus().getCode());
        response.put("testComponent", testStats);
        
        // Database Health 統計
        Map<String, Object> dbStats = new HashMap<>();
        dbStats.put("checkCount", databaseHealth.getCheckCount());
        dbStats.put("currentStatus", databaseHealth.health().getStatus().getCode());
        dbStats.put("connectionPool", databaseHealth.getConnectionPoolInfo());
        response.put("database", dbStats);
        
        // External Service Health 統計
        Map<String, Object> externalStats = new HashMap<>();
        externalStats.put("checkCount", externalServiceHealth.getCheckCount());
        externalStats.put("currentStatus", externalServiceHealth.health().getStatus().getCode());
        externalStats.put("responseTime", externalServiceHealth.getLastResponseTime());
        response.put("externalService", externalStats);
        
        return response;
    }

    /**
     * 重置所有健康狀態
     */
    /*
    [003][M1][Demo Controller]
    input: void
    output: Map<String, Object>
    重置所有組件的健康狀態為UP
    */
    @PostMapping("/reset")
    @IncludeMonitoring(
        name = "health.reset.all",
        description = "Reset all health components to UP status",
        tags = {"operation=write", "type=health", "category=maintenance"}
    )
    public Map<String, Object> resetAllHealthStatus() {
        log.info("Resetting all health status to UP");
        
        testHealthIndicator.setHealthy(true);
        databaseHealth.setHealthy(true);
        externalServiceHealth.setHealthy(true);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "reset_completed");
        response.put("message", "All health components reset to UP status");
        response.put("timestamp", LocalDateTime.now());
        
        return response;
    }
}

/**
 * 測試用Health Indicator
 */
/*
[003][M1][Demo Controller]
測試用Health Indicator，可動態改變健康狀態
*/
@Component
class TestHealthIndicator implements HealthIndicator {
    
    private final AtomicBoolean healthy = new AtomicBoolean(true);
    private final AtomicInteger checkCount = new AtomicInteger(0);
    
    @Override
    public Health health() {
        checkCount.incrementAndGet();
        
        if (healthy.get()) {
            return Health.up()
                .withDetail("status", "All systems operational")
                .withDetail("checkCount", checkCount.get())
                .withDetail("lastCheck", LocalDateTime.now())
                .build();
        } else {
            return Health.down()
                .withDetail("status", "System is down for testing")
                .withDetail("checkCount", checkCount.get())
                .withDetail("lastCheck", LocalDateTime.now())
                .withDetail("reason", "Manually set to DOWN for testing")
                .build();
        }
    }
    
    public void setHealthy(boolean healthy) {
        this.healthy.set(healthy);
    }
    
    public int getCheckCount() {
        return checkCount.get();
    }
}

/**
 * 數據庫健康檢查模擬器
 */
/*
[003][M1][Demo Controller]
數據庫健康檢查模擬器，模擬數據庫連接狀態
*/
@Component
class DatabaseHealthSimulator implements HealthIndicator {
    
    private final AtomicBoolean healthy = new AtomicBoolean(true);
    private final AtomicInteger checkCount = new AtomicInteger(0);
    private final AtomicInteger connectionPoolSize = new AtomicInteger(10);
    
    @Override
    public Health health() {
        checkCount.incrementAndGet();
        
        // 模擬數據庫連接檢查延遲
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(5, 50));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        if (healthy.get()) {
            return Health.up()
                .withDetail("database", "PostgreSQL")
                .withDetail("status", "Connected")
                .withDetail("connectionPool", Map.of(
                    "active", connectionPoolSize.get(),
                    "max", 20,
                    "idle", 20 - connectionPoolSize.get()
                ))
                .withDetail("checkCount", checkCount.get())
                .withDetail("lastCheck", LocalDateTime.now())
                .build();
        } else {
            return Health.down()
                .withDetail("database", "PostgreSQL")
                .withDetail("status", "Connection failed")
                .withDetail("error", "Connection timeout after 30 seconds")
                .withDetail("checkCount", checkCount.get())
                .withDetail("lastCheck", LocalDateTime.now())
                .build();
        }
    }
    
    public void setHealthy(boolean healthy) {
        this.healthy.set(healthy);
    }
    
    public int getCheckCount() {
        return checkCount.get();
    }
    
    public Map<String, Object> getConnectionPoolInfo() {
        return Map.of(
            "active", connectionPoolSize.get(),
            "max", 20,
            "idle", 20 - connectionPoolSize.get()
        );
    }
}

/**
 * 外部服務健康檢查模擬器
 */
/*
[003][M1][Demo Controller]
外部服務健康檢查模擬器，模擬外部API服務狀態
*/
@Component
class ExternalServiceHealthSimulator implements HealthIndicator {
    
    private final AtomicBoolean healthy = new AtomicBoolean(true);
    private final AtomicInteger checkCount = new AtomicInteger(0);
    private volatile long lastResponseTime = 0;
    
    @Override
    public Health health() {
        checkCount.incrementAndGet();
        
        long startTime = System.currentTimeMillis();
        
        // 模擬外部服務調用延遲
        try {
            int delay = healthy.get() ? 
                ThreadLocalRandom.current().nextInt(10, 100) :  // 健康時較快
                ThreadLocalRandom.current().nextInt(1000, 5000); // 不健康時很慢
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        lastResponseTime = System.currentTimeMillis() - startTime;
        
        if (healthy.get()) {
            return Health.up()
                .withDetail("service", "External API")
                .withDetail("endpoint", "https://api.example.com/health")
                .withDetail("status", "Available")
                .withDetail("responseTimeMs", lastResponseTime)
                .withDetail("checkCount", checkCount.get())
                .withDetail("lastCheck", LocalDateTime.now())
                .build();
        } else {
            return Health.down()
                .withDetail("service", "External API")
                .withDetail("endpoint", "https://api.example.com/health")
                .withDetail("status", "Unavailable")
                .withDetail("error", "Service returned HTTP 503")
                .withDetail("responseTimeMs", lastResponseTime)
                .withDetail("checkCount", checkCount.get())
                .withDetail("lastCheck", LocalDateTime.now())
                .build();
        }
    }
    
    public void setHealthy(boolean healthy) {
        this.healthy.set(healthy);
    }
    
    public int getCheckCount() {
        return checkCount.get();
    }
    
    public long getLastResponseTime() {
        return lastResponseTime;
    }
}