/*
[003][M1][Demo Controller]
Metrics測試Controller
測試SLI數據收集和Golden Signals監控功能
*/
package com.jamestann.test.library.demo.controller;

import com.jamestann.test.library.actuator.annotation.IncludeMonitoring;
import io.micrometer.core.instrument.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Metrics測試Controller - 測試SLI數據收集和監控功能
 * <p>
 * 提供多種Metrics測試端點來驗證：
 * - Golden Signals數據收集 (Latency, Traffic, Errors, Saturation)
 * - 自定義Metrics創建和更新
 * - Timer、Counter、Gauge等不同類型的Metrics
 * - Metrics查詢和統計功能
 * - SLI數據驗證
 * 
 * @author James Tann
 * @since 1.0.0
 */
/*
[003][M1][Demo Controller]
Metrics測試控制器，提供SLI數據收集和Golden Signals測試端點
驗證各種監控指標的收集和統計功能
*/
@Slf4j
@RestController
@RequestMapping("/api/metrics")
public class MetricsTestController {

    private final MeterRegistry meterRegistry;
    
    // 自定義Metrics
    private final Counter customCounter;
    private final Timer customTimer;
    private final Gauge customGauge;
    private final DistributionSummary customSummary;
    
    // 測試用的原子變量
    private final AtomicLong gaugeValue = new AtomicLong(100);
    private final AtomicLong requestCount = new AtomicLong(0);

    public MetricsTestController(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        
        // 初始化自定義Metrics
        this.customCounter = Counter.builder("demo.custom.counter")
            .description("Custom counter for demo purposes")
            .tag("component", "metrics-test")
            .register(meterRegistry);
            
        this.customTimer = Timer.builder("demo.custom.timer")
            .description("Custom timer for demo purposes")
            .tag("component", "metrics-test")
            .register(meterRegistry);
            
        this.customGauge = Gauge.builder("demo.custom.gauge")
            .description("Custom gauge for demo purposes")
            .tag("component", "metrics-test")
            .register(meterRegistry, gaugeValue, AtomicLong::get);
            
        this.customSummary = DistributionSummary.builder("demo.custom.summary")
            .description("Custom distribution summary for demo purposes")
            .tag("component", "metrics-test")
            .register(meterRegistry);
    }

    /**
     * Golden Signals測試 - Latency
     */
    /*
    [003][M1][Demo Controller]
    input: int delayMs
    output: Map<String, Object>
    測試Latency監控，模擬不同延遲的操作
    */
    @GetMapping("/latency-test")
    @IncludeMonitoring(
        name = "golden.signals.latency",
        description = "Golden Signals - Latency test",
        tags = {"signal=latency", "type=golden", "category=sli"}
    )
    public Map<String, Object> latencyTest(@RequestParam(defaultValue = "100") int delayMs) {
        log.info("Latency test - delay: {}ms", delayMs);
        
        long startTime = System.nanoTime();
        
        // 模擬指定延遲
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        long endTime = System.nanoTime();
        double actualDelayMs = (endTime - startTime) / 1_000_000.0;
        
        // 記錄自定義Timer
        customTimer.record(Duration.ofNanos(endTime - startTime));
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "completed");
        response.put("requestedDelayMs", delayMs);
        response.put("actualDelayMs", actualDelayMs);
        response.put("timestamp", LocalDateTime.now());
        
        return response;
    }

    /**
     * Golden Signals測試 - Traffic
     */
    /*
    [003][M1][Demo Controller]
    input: void
    output: Map<String, Object>
    測試Traffic監控，增加請求計數
    */
    @GetMapping("/traffic-test")
    @IncludeMonitoring(
        name = "golden.signals.traffic",
        description = "Golden Signals - Traffic test",
        tags = {"signal=traffic", "type=golden", "category=sli"}
    )
    public Map<String, Object> trafficTest() {
        long currentCount = requestCount.incrementAndGet();
        log.info("Traffic test - request count: {}", currentCount);
        
        // 增加自定義Counter
        customCounter.increment();
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "completed");
        response.put("requestCount", currentCount);
        response.put("timestamp", LocalDateTime.now());
        
        return response;
    }

    /**
     * Golden Signals測試 - Errors
     */
    /*
    [003][M1][Demo Controller]
    input: String errorType, double errorRate
    output: Map<String, Object>
    測試Error監控，模擬不同類型的錯誤
    */
    @GetMapping("/error-test")
    @IncludeMonitoring(
        name = "golden.signals.error",
        description = "Golden Signals - Error test",
        tags = {"signal=error", "type=golden", "category=sli"}
    )
    public Map<String, Object> errorTest(
            @RequestParam(defaultValue = "runtime") String errorType,
            @RequestParam(defaultValue = "0.3") double errorRate) {
        
        log.info("Error test - errorType: {}, errorRate: {}", errorType, errorRate);
        
        // 根據錯誤率決定是否拋出異常
        if (ThreadLocalRandom.current().nextDouble() < errorRate) {
            // 記錄錯誤Counter
            Counter.builder("demo.errors")
                .description("Demo error counter")
                .tag("error_type", errorType)
                .register(meterRegistry)
                .increment();
                
            switch (errorType.toLowerCase()) {
                case "runtime":
                    throw new RuntimeException("Intentional runtime error for metrics testing");
                case "business":
                    throw new BusinessException("Business logic error for metrics testing");
                case "validation":
                    throw new IllegalArgumentException("Validation error for metrics testing");
                default:
                    throw new RuntimeException("Unknown error type: " + errorType);
            }
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("errorType", errorType);
        response.put("errorRate", errorRate);
        response.put("message", "No error occurred in this request");
        response.put("timestamp", LocalDateTime.now());
        
        return response;
    }

    /**
     * Golden Signals測試 - Saturation
     */
    /*
    [003][M1][Demo Controller]
    input: int load
    output: Map<String, Object>
    測試Saturation監控，模擬系統負載
    */
    @GetMapping("/saturation-test")
    @IncludeMonitoring(
        name = "golden.signals.saturation",
        description = "Golden Signals - Saturation test",
        tags = {"signal=saturation", "type=golden", "category=sli"}
    )
    public Map<String, Object> saturationTest(@RequestParam(defaultValue = "50") int load) {
        log.info("Saturation test - load: {}", load);
        
        // 更新Gauge值來表示當前負載
        gaugeValue.set(load);
        
        // 記錄DistributionSummary
        customSummary.record(load);
        
        // 獲取系統資源使用情況
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        double memoryUsagePercent = (double) usedMemory / runtime.maxMemory() * 100;
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "completed");
        response.put("currentLoad", load);
        response.put("memoryUsagePercent", memoryUsagePercent);
        response.put("availableProcessors", runtime.availableProcessors());
        response.put("timestamp", LocalDateTime.now());
        
        return response;
    }

    /**
     * 創建自定義Metrics
     */
    /*
    [003][M1][Demo Controller]
    input: String name, String type, double value
    output: Map<String, Object>
    動態創建自定義Metrics
    */
    @PostMapping("/custom")
    @IncludeMonitoring(
        name = "metrics.custom.create",
        description = "Create custom metrics dynamically",
        tags = {"operation=create", "type=custom", "category=metrics"}
    )
    public Map<String, Object> createCustomMetric(
            @RequestParam String name,
            @RequestParam String type,
            @RequestParam double value) {
        
        log.info("Creating custom metric - name: {}, type: {}, value: {}", name, type, value);
        
        try {
            switch (type.toLowerCase()) {
                case "counter":
                    Counter counter = Counter.builder("demo.custom." + name)
                        .description("Dynamic custom counter: " + name)
                        .tag("dynamic", "true")
                        .register(meterRegistry);
                    counter.increment(value);
                    break;
                    
                case "gauge":
                    AtomicLong gaugeRef = new AtomicLong((long) value);
                    Gauge.builder("demo.custom." + name)
                        .description("Dynamic custom gauge: " + name)
                        .tag("dynamic", "true")
                        .register(meterRegistry, gaugeRef, AtomicLong::get);
                    break;
                    
                case "timer":
                    Timer timer = Timer.builder("demo.custom." + name)
                        .description("Dynamic custom timer: " + name)
                        .tag("dynamic", "true")
                        .register(meterRegistry);
                    timer.record((long) value, java.util.concurrent.TimeUnit.MILLISECONDS);
                    break;
                    
                case "summary":
                    DistributionSummary summary = DistributionSummary.builder("demo.custom." + name)
                        .description("Dynamic custom summary: " + name)
                        .tag("dynamic", "true")
                        .register(meterRegistry);
                    summary.record(value);
                    break;
                    
                default:
                    throw new IllegalArgumentException("Unsupported metric type: " + type);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "created");
            response.put("metricName", "demo.custom." + name);
            response.put("metricType", type);
            response.put("initialValue", value);
            response.put("timestamp", LocalDateTime.now());
            
            return response;
            
        } catch (Exception e) {
            log.error("Failed to create custom metric", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("supportedTypes", List.of("counter", "gauge", "timer", "summary"));
            errorResponse.put("timestamp", LocalDateTime.now());
            
            return errorResponse;
        }
    }

    /**
     * 查詢Metrics統計
     */
    /*
    [003][M1][Demo Controller]
    input: String metricName
    output: Map<String, Object>
    查詢指定Metrics的統計資訊
    */
    @GetMapping("/stats")
    @IncludeMonitoring(
        name = "metrics.stats.query",
        description = "Query metrics statistics",
        tags = {"operation=read", "type=stats", "category=metrics"}
    )
    public Map<String, Object> getMetricsStats(@RequestParam(required = false) String metricName) {
        log.info("Querying metrics statistics - metricName: {}", metricName);
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        
        if (metricName != null && !metricName.isEmpty()) {
            // 查詢特定Metric
            Meter meter = meterRegistry.find(metricName).meter();
            if (meter != null) {
                response.put("metricName", metricName);
                response.put("metricType", meter.getId().getType());
                response.put("tags", meter.getId().getTags());
                
                // 根據類型獲取值
                if (meter instanceof Counter counter) {
                    response.put("count", counter.count());
                } else if (meter instanceof Timer timer) {
                    response.put("count", timer.count());
                    response.put("totalTimeMs", timer.totalTime(java.util.concurrent.TimeUnit.MILLISECONDS));
                    response.put("meanTimeMs", timer.mean(java.util.concurrent.TimeUnit.MILLISECONDS));
                    response.put("maxTimeMs", timer.max(java.util.concurrent.TimeUnit.MILLISECONDS));
                } else if (meter instanceof Gauge gauge) {
                    response.put("value", gauge.value());
                } else if (meter instanceof DistributionSummary summary) {
                    response.put("count", summary.count());
                    response.put("totalAmount", summary.totalAmount());
                    response.put("mean", summary.mean());
                    response.put("max", summary.max());
                }
            } else {
                response.put("error", "Metric not found: " + metricName);
            }
        } else {
            // 查詢所有Demo相關的Metrics
            Map<String, Object> demoMetrics = new HashMap<>();
            
            // Custom Counter
            demoMetrics.put("customCounter", customCounter.count());
            
            // Custom Timer
            Map<String, Object> timerStats = new HashMap<>();
            timerStats.put("count", customTimer.count());
            timerStats.put("totalTimeMs", customTimer.totalTime(java.util.concurrent.TimeUnit.MILLISECONDS));
            timerStats.put("meanTimeMs", customTimer.mean(java.util.concurrent.TimeUnit.MILLISECONDS));
            timerStats.put("maxTimeMs", customTimer.max(java.util.concurrent.TimeUnit.MILLISECONDS));
            demoMetrics.put("customTimer", timerStats);
            
            // Custom Gauge
            demoMetrics.put("customGauge", customGauge.value());
            
            // Custom Summary
            Map<String, Object> summaryStats = new HashMap<>();
            summaryStats.put("count", customSummary.count());
            summaryStats.put("totalAmount", customSummary.totalAmount());
            summaryStats.put("mean", customSummary.mean());
            summaryStats.put("max", customSummary.max());
            demoMetrics.put("customSummary", summaryStats);
            
            response.put("demoMetrics", demoMetrics);
            response.put("requestCount", requestCount.get());
        }
        
        return response;
    }

    /**
     * 批量Metrics測試
     */
    /*
    [003][M1][Demo Controller]
    input: int operations
    output: Map<String, Object>
    批量執行多種Metrics操作，測試高頻更新
    */
    @PostMapping("/batch-test")
    @IncludeMonitoring(
        name = "metrics.batch.test",
        description = "Batch metrics operations test",
        tags = {"operation=batch", "type=mixed", "category=performance"}
    )
    public Map<String, Object> batchMetricsTest(@RequestParam(defaultValue = "100") int operations) {
        log.info("Batch metrics test - operations: {}", operations);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < operations; i++) {
            // 隨機執行不同的Metrics操作
            int operation = ThreadLocalRandom.current().nextInt(4);
            
            switch (operation) {
                case 0:
                    // Counter increment
                    customCounter.increment();
                    break;
                case 1:
                    // Timer record
                    customTimer.record(ThreadLocalRandom.current().nextInt(10, 100), 
                                     java.util.concurrent.TimeUnit.MILLISECONDS);
                    break;
                case 2:
                    // Gauge update
                    gaugeValue.set(ThreadLocalRandom.current().nextLong(0, 1000));
                    break;
                case 3:
                    // Summary record
                    customSummary.record(ThreadLocalRandom.current().nextDouble(1.0, 100.0));
                    break;
            }
        }
        
        long endTime = System.currentTimeMillis();
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "completed");
        response.put("operations", operations);
        response.put("executionTimeMs", endTime - startTime);
        response.put("operationsPerSecond", (operations * 1000.0) / (endTime - startTime));
        response.put("timestamp", LocalDateTime.now());
        
        return response;
    }

    /**
     * 獲取所有可用的Metrics名稱
     */
    /*
    [003][M1][Demo Controller]
    input: String prefix
    output: Map<String, Object>
    獲取所有可用的Metrics名稱列表
    */
    @GetMapping("/list")
    @IncludeMonitoring(
        name = "metrics.list.all",
        description = "List all available metrics",
        tags = {"operation=read", "type=list", "category=metrics"}
    )
    public Map<String, Object> listMetrics(@RequestParam(required = false) String prefix) {
        log.info("Listing metrics - prefix: {}", prefix);
        
        List<String> metricNames = meterRegistry.getMeters().stream()
            .map(meter -> meter.getId().getName())
            .filter(name -> prefix == null || prefix.isEmpty() || name.startsWith(prefix))
            .distinct()
            .sorted()
            .toList();
        
        Map<String, Object> response = new HashMap<>();
        response.put("totalCount", metricNames.size());
        response.put("metrics", metricNames);
        response.put("prefix", prefix);
        response.put("timestamp", LocalDateTime.now());
        
        return response;
    }

    /**
     * 自定義業務異常
     */
    public static class BusinessException extends RuntimeException {
        public BusinessException(String message) {
            super(message);
        }
    }
}