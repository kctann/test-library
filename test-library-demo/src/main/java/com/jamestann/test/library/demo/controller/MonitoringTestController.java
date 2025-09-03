/*
[003][M1][Demo Controller]
監控測試Controller
測試AOP監控、註解監控和SLI數據收集功能
*/
package com.jamestann.test.library.demo.controller;

import com.jamestann.test.library.actuator.annotation.ExcludeMonitoring;
import com.jamestann.test.library.actuator.annotation.IncludeMonitoring;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 監控測試Controller - 測試各種AOP監控功能
 * <p>
 * 提供多種測試端點來驗證：
 * - @IncludeMonitoring註解功能
 * - 自定義metrics名稱和tags
 * - 異常處理監控
 * - 不同執行時間的方法監控
 * - 排除監控功能
 * 
 * @author James Tann
 * @since 1.0.0
 */
/*
[003][M1][Demo Controller]
監控測試控制器，提供多種AOP監控測試端點
驗證註解監控、自定義metrics、異常處理等功能
*/
@Slf4j
@RestController
@RequestMapping("/api/monitoring")
public class MonitoringTestController {

    private final Random random = new Random();

    /**
     * 基本監控測試 - 使用自定義metric名稱
     */
    /*
    [003][M1][Demo Controller]
    input: void
    output: Map<String, Object>
    基本監控測試，使用自定義business-semantic metric名稱
    */
    @GetMapping("/basic")
    @IncludeMonitoring(
        name = "user.profile.get",
        description = "Get user profile information",
        tags = {"operation=read", "entity=user", "type=profile"}
    )
    public Map<String, Object> basicMonitoringTest() {
        log.info("Basic monitoring test - simulating user profile retrieval");
        
        // 模擬一些處理時間
        simulateProcessing(50, 200);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("operation", "get_user_profile");
        response.put("timestamp", LocalDateTime.now());
        response.put("processingTime", "simulated");
        
        return response;
    }

    /**
     * 複雜業務操作監控 - 包含參數監控
     */
    /*
    [003][M1][Demo Controller]
    input: String userId, String operation
    output: Map<String, Object>
    複雜業務操作監控，包含參數和返回值監控
    */
    @PostMapping("/business/{userId}")
    @IncludeMonitoring(
        name = "business.operation.execute",
        description = "Execute complex business operation",
        tags = {"operation=write", "entity=business", "complexity=high"},
        includeParameters = true,
        includeReturnType = true
    )
    public Map<String, Object> complexBusinessOperation(
            @PathVariable String userId,
            @RequestParam(defaultValue = "default") String operation) {
        
        log.info("Complex business operation - userId: {}, operation: {}", userId, operation);
        
        // 模擬複雜處理時間
        simulateProcessing(100, 500);
        
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("operation", operation);
        response.put("result", "processed");
        response.put("timestamp", LocalDateTime.now());
        response.put("executionId", generateExecutionId());
        
        return response;
    }

    /**
     * 快速操作測試 - 測試低延遲操作
     */
    /*
    [003][M1][Demo Controller]
    input: void
    output: Map<String, Object>
    快速操作測試，驗證低延遲操作的監控
    */
    @GetMapping("/fast")
    @IncludeMonitoring(
        name = "cache.lookup",
        description = "Fast cache lookup operation",
        tags = {"operation=read", "source=cache", "latency=low"}
    )
    public Map<String, Object> fastOperation() {
        log.info("Fast operation test - simulating cache lookup");
        
        // 模擬快速操作 (10-50ms)
        simulateProcessing(10, 50);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "cache_hit");
        response.put("operation", "fast_lookup");
        response.put("timestamp", LocalDateTime.now());
        response.put("cached", true);
        
        return response;
    }

    /**
     * 慢操作測試 - 測試高延遲操作
     */
    /*
    [003][M1][Demo Controller]
    input: void
    output: Map<String, Object>
    慢操作測試，驗證高延遲操作的監控
    */
    @GetMapping("/slow")
    @IncludeMonitoring(
        name = "database.complex.query",
        description = "Complex database query operation",
        tags = {"operation=read", "source=database", "latency=high", "complexity=high"}
    )
    public Map<String, Object> slowOperation() {
        log.info("Slow operation test - simulating complex database query");
        
        // 模擬慢操作 (500-2000ms)
        simulateProcessing(500, 2000);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "completed");
        response.put("operation", "complex_query");
        response.put("timestamp", LocalDateTime.now());
        response.put("recordsProcessed", random.nextInt(10000) + 1000);
        
        return response;
    }

    /**
     * 異常測試 - 測試異常情況的監控
     */
    /*
    [003][M1][Demo Controller]
    input: String errorType
    output: Map<String, Object>
    異常測試，驗證異常情況的監控和錯誤追蹤
    */
    @GetMapping("/error")
    @IncludeMonitoring(
        name = "error.handling.test",
        description = "Error handling test for monitoring",
        tags = {"operation=test", "type=error", "category=intentional"}
    )
    public Map<String, Object> errorTest(@RequestParam(defaultValue = "runtime") String errorType) {
        log.info("Error test - errorType: {}", errorType);
        
        // 模擬一些處理時間
        simulateProcessing(100, 300);
        
        // 根據參數拋出不同類型的異常
        switch (errorType.toLowerCase()) {
            case "runtime":
                throw new RuntimeException("Intentional runtime exception for monitoring test");
            case "illegal":
                throw new IllegalArgumentException("Intentional illegal argument exception");
            case "null":
                throw new NullPointerException("Intentional null pointer exception");
            case "custom":
                throw new CustomBusinessException("Custom business exception for testing");
            default:
                // 50% 機率成功，50% 機率拋出異常
                if (random.nextBoolean()) {
                    throw new RuntimeException("Random exception for load testing");
                }
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "No error occurred");
        response.put("timestamp", LocalDateTime.now());
        
        return response;
    }

    /**
     * 批量操作測試 - 測試高頻調用監控
     */
    /*
    [003][M1][Demo Controller]
    input: int count
    output: Map<String, Object>
    批量操作測試，驗證高頻調用的監控統計
    */
    @PostMapping("/batch")
    @IncludeMonitoring(
        name = "batch.process",
        description = "Batch processing operation",
        tags = {"operation=write", "type=batch", "frequency=high"}
    )
    public Map<String, Object> batchOperation(@RequestParam(defaultValue = "10") int count) {
        log.info("Batch operation test - processing {} items", count);
        
        long startTime = System.currentTimeMillis();
        
        // 模擬批量處理
        for (int i = 0; i < count; i++) {
            simulateProcessing(10, 50);  // 每個項目10-50ms
        }
        
        long endTime = System.currentTimeMillis();
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "completed");
        response.put("itemsProcessed", count);
        response.put("totalTime", (endTime - startTime) + "ms");
        response.put("averageTime", ((endTime - startTime) / count) + "ms");
        response.put("timestamp", LocalDateTime.now());
        
        return response;
    }

    /**
     * 不監控的內部方法測試 - 使用@ExcludeMonitoring
     */
    /*
    [003][M1][Demo Controller]
    input: void
    output: Map<String, Object>
    測試@ExcludeMonitoring註解，此方法不應該被監控
    */
    @GetMapping("/internal")
    @ExcludeMonitoring(reason = "Internal utility endpoint, no business value for monitoring")
    public Map<String, Object> internalOperation() {
        log.info("Internal operation - should not be monitored");
        
        simulateProcessing(50, 100);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "internal_operation_completed");
        response.put("monitored", false);
        response.put("timestamp", LocalDateTime.now());
        response.put("note", "This endpoint is excluded from monitoring");
        
        return response;
    }

    /**
     * 條件監控測試 - 根據參數決定處理邏輯
     */
    /*
    [003][M1][Demo Controller]
    input: String mode
    output: Map<String, Object>
    條件監控測試，根據不同參數執行不同邏輯
    */
    @GetMapping("/conditional")
    @IncludeMonitoring(
        name = "conditional.operation",
        description = "Conditional processing based on mode",
        tags = {"operation=conditional", "type=dynamic"}
    )
    public Map<String, Object> conditionalOperation(@RequestParam(defaultValue = "normal") String mode) {
        log.info("Conditional operation - mode: {}", mode);
        
        Map<String, Object> response = new HashMap<>();
        response.put("mode", mode);
        response.put("timestamp", LocalDateTime.now());
        
        switch (mode.toLowerCase()) {
            case "fast":
                simulateProcessing(20, 80);
                response.put("processing", "fast_path");
                break;
            case "slow":
                simulateProcessing(800, 1500);
                response.put("processing", "slow_path");
                break;
            case "error":
                simulateProcessing(100, 200);
                throw new RuntimeException("Conditional error mode triggered");
            default:
                simulateProcessing(100, 300);
                response.put("processing", "normal_path");
        }
        
        response.put("status", "completed");
        return response;
    }

    /**
     * 模擬處理時間
     */
    /*
    [003][M1][Demo Controller]
    input: int minMs, int maxMs
    output: void
    工具方法：模擬指定範圍的處理時間
    */
    private void simulateProcessing(int minMs, int maxMs) {
        try {
            int delay = ThreadLocalRandom.current().nextInt(minMs, maxMs + 1);
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Processing simulation interrupted", e);
        }
    }

    /**
     * 生成執行ID
     */
    /*
    [003][M1][Demo Controller]
    input: void
    output: String
    工具方法：生成唯一執行ID
    */
    private String generateExecutionId() {
        return "exec_" + System.currentTimeMillis() + "_" + random.nextInt(1000);
    }

    /**
     * 自定義業務異常
     */
    /*
    [003][M1][Demo Controller]
    自定義業務異常類，用於測試異常監控
    */
    public static class CustomBusinessException extends RuntimeException {
        public CustomBusinessException(String message) {
            super(message);
        }
    }
}