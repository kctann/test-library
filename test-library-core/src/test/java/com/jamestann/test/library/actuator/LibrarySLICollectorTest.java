/*
[003][M1][SLI數據收集]
Library SLI Collector測試類別
測試 Golden Signals 數據收集的核心功能（純單元測試）
*/
package com.jamestann.test.library.actuator;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for LibrarySLICollector.
 * <p>
 * Tests the core functionality of Golden Signals data collection 
 * without web layer or AOP interactions. Focuses on pure business logic.
 * 
 * @author James Tann
 * @since 1.0.0
 */
/*
[003][M1][SLI數據收集]
LibrarySLICollector 的純單元測試
測試 Golden Signals 數據收集的核心功能，不涉及 Web 層或 AOP 交互
*/
class LibrarySLICollectorTest {

    private MeterRegistry meterRegistry;
    private LibraryActuatorProperties properties;
    private LibrarySLICollector sliCollector;

    /**
     * Sets up test fixtures before each test.
     */
    /*
    [003][M1][SLI數據收集]
    input: void
    output: void
    在每個測試前設置測試裝置
    */
    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        properties = new LibraryActuatorProperties();
        properties.getMetrics().setEnabled(true);
        sliCollector = new LibrarySLICollector(meterRegistry, properties);
    }

    /**
     * Tests basic timer creation and functionality.
     */
    /*
    [003][M1][SLI數據收集]
    input: void
    output: void
    測試基本計時器創建和功能
    */
    @Test
    void testTimerCreationAndBasicFunctionality() {
        String endpoint = "/api/users";
        String method = "GET";
        
        Timer.Sample sample = sliCollector.startTimer(endpoint, method);
        assertThat(sample).isNotNull();
        assertThat(sliCollector.getActiveRequestCount()).isEqualTo(1);
        
        // Simulate processing time
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        sliCollector.stopTimer(sample, endpoint, method, 200, null);
        assertThat(sliCollector.getActiveRequestCount()).isEqualTo(0);
    }

    /**
     * Tests that metrics are created with correct tags.
     */
    /*
    [003][M1][SLI數據收集]
    input: void
    output: void
    測試指標創建時具有正確標籤
    */
    @Test
    void testMetricsCreatedWithCorrectTags() {
        String endpoint = "/api/users";
        String method = "GET";
        int status = 200;
        
        Timer.Sample sample = sliCollector.startTimer(endpoint, method);
        sliCollector.stopTimer(sample, endpoint, method, status, null);
        
        // Verify latency timer
        Timer latencyTimer = meterRegistry.find("application.http.request.duration")
                                         .tag("endpoint", endpoint)
                                         .tag("method", method)
                                         .tag("status", "200")
                                         .timer();
        assertThat(latencyTimer).isNotNull();
        assertThat(latencyTimer.count()).isEqualTo(1);
        
        // Verify traffic counter
        Counter trafficCounter = meterRegistry.find("application.http.request.total")
                                             .tag("endpoint", endpoint)
                                             .tag("method", method)
                                             .counter();
        assertThat(trafficCounter).isNotNull();
        assertThat(trafficCounter.count()).isEqualTo(1);
    }

    /**
     * Tests error detection and recording for HTTP errors.
     */
    /*
    [003][M1][SLI數據收集]
    input: void
    output: void
    測試 HTTP 錯誤的檢測和記錄
    */
    @Test
    void testErrorDetectionForHttpErrors() {
        String endpoint = "/api/users";
        String method = "GET";
        
        // Test 4xx error
        Timer.Sample sample1 = sliCollector.startTimer(endpoint, method);
        sliCollector.stopTimer(sample1, endpoint, method, 404, null);
        
        // Test 5xx error  
        Timer.Sample sample2 = sliCollector.startTimer(endpoint, method);
        sliCollector.stopTimer(sample2, endpoint, method, 500, null);
        
        // Verify error counters
        Counter error404Counter = meterRegistry.find("application.http.request.errors")
                                              .tag("status", "404")
                                              .counter();
        Counter error500Counter = meterRegistry.find("application.http.request.errors")
                                              .tag("status", "500")
                                              .counter();
        
        assertThat(error404Counter).isNotNull();
        assertThat(error404Counter.count()).isEqualTo(1);
        assertThat(error500Counter).isNotNull();
        assertThat(error500Counter.count()).isEqualTo(1);
    }

    /**
     * Tests exception-based error recording.
     */
    /*
    [003][M1][SLI數據收集]
    input: void
    output: void
    測試基於異常的錯誤記錄
    */
    @Test
    void testExceptionBasedErrorRecording() {
        String endpoint = "/api/users";
        String method = "POST";
        RuntimeException exception = new RuntimeException("Database error");
        
        Timer.Sample sample = sliCollector.startTimer(endpoint, method);
        sliCollector.stopTimer(sample, endpoint, method, 200, exception);
        
        Counter errorCounter = meterRegistry.find("application.http.request.errors")
                                          .tag("error_type", "RuntimeException")
                                          .counter();
        
        assertThat(errorCounter).isNotNull();
        assertThat(errorCounter.count()).isEqualTo(1);
    }

    /**
     * Tests method execution recording functionality.
     */
    /*
    [003][M1][SLI數據收集]
    input: void
    output: void
    測試方法執行記錄功能
    */
    @Test
    void testMethodExecutionRecording() {
        String className = "com.example.UserService";
        String methodName = "findUser";
        Duration duration = Duration.ofMillis(50);
        
        sliCollector.recordMethodExecution(className, methodName, duration, null);
        
        Timer methodTimer = meterRegistry.find("application.method.duration")
                                        .tag("class", "UserService") // Should be sanitized
                                        .tag("method", methodName)
                                        .tag("status", "success")
                                        .timer();
        
        assertThat(methodTimer).isNotNull();
        assertThat(methodTimer.count()).isEqualTo(1);
    }

    /**
     * Tests method execution error recording.
     */
    /*
    [003][M1][SLI數據收集]
    input: void
    output: void
    測試方法執行錯誤記錄
    */
    @Test
    void testMethodExecutionErrorRecording() {
        String className = "com.example.UserService";
        String methodName = "createUser";
        Duration duration = Duration.ofMillis(25);
        IllegalArgumentException exception = new IllegalArgumentException("Invalid data");
        
        sliCollector.recordMethodExecution(className, methodName, duration, exception);
        
        // Verify error timer
        Timer errorTimer = meterRegistry.find("application.method.duration")
                                       .tag("status", "error")
                                       .timer();
        assertThat(errorTimer).isNotNull();
        
        // Verify error counter
        Counter errorCounter = meterRegistry.find("application.method.errors")
                                          .tag("exception", "IllegalArgumentException")
                                          .counter();
        assertThat(errorCounter).isNotNull();
        assertThat(errorCounter.count()).isEqualTo(1);
    }

    /**
     * Tests active request count tracking (Saturation metric).
     */
    /*
    [003][M1][SLI數據收集]
    input: void
    output: void
    測試活動請求計數追蹤（飽和度指標）
    */
    @Test
    void testActiveRequestCountTracking() {
        assertThat(sliCollector.getActiveRequestCount()).isEqualTo(0);
        
        // Start multiple timers
        Timer.Sample sample1 = sliCollector.startTimer("/api/users", "GET");
        Timer.Sample sample2 = sliCollector.startTimer("/api/orders", "POST");
        Timer.Sample sample3 = sliCollector.startTimer("/api/products", "GET");
        
        assertThat(sliCollector.getActiveRequestCount()).isEqualTo(3);
        
        // Stop timers one by one
        sliCollector.stopTimer(sample1, "/api/users", "GET", 200, null);
        assertThat(sliCollector.getActiveRequestCount()).isEqualTo(2);
        
        sliCollector.stopTimer(sample2, "/api/orders", "POST", 201, null);
        assertThat(sliCollector.getActiveRequestCount()).isEqualTo(1);
        
        sliCollector.stopTimer(sample3, "/api/products", "GET", 200, null);
        assertThat(sliCollector.getActiveRequestCount()).isEqualTo(0);
    }

    /**
     * Tests endpoint path sanitization to prevent high cardinality.
     */
    /*
    [003][M1][SLI數據收集]
    input: void
    output: void
    測試端點路徑清理以防止高基數
    */
    @Test
    void testEndpointPathSanitization() {
        // Test numeric ID replacement
        Timer.Sample sample1 = sliCollector.startTimer("/api/users/123", "GET");
        sliCollector.stopTimer(sample1, "/api/users/123", "GET", 200, null);
        
        // Test UUID replacement
        Timer.Sample sample2 = sliCollector.startTimer("/api/users/550e8400-e29b-41d4-a716-446655440000", "GET");
        sliCollector.stopTimer(sample2, "/api/users/550e8400-e29b-41d4-a716-446655440000", "GET", 200, null);
        
        // Verify sanitized paths
        Timer idTimer = meterRegistry.find("application.http.request.duration")
                                   .tag("endpoint", "/api/users/{id}")
                                   .timer();
        Timer uuidTimer = meterRegistry.find("application.http.request.duration")
                                     .tag("endpoint", "/api/users/{uuid}")
                                     .timer();
        
        assertThat(idTimer).isNotNull();
        assertThat(uuidTimer).isNotNull();
    }

    /**
     * Tests that no metrics are recorded when disabled.
     */
    /*
    [003][M1][SLI數據收集]
    input: void
    output: void
    測試禁用時不記錄指標
    */
    @Test
    void testMetricsDisabled() {
        properties.getMetrics().setEnabled(false);
        
        Timer.Sample sample = sliCollector.startTimer("/api/test", "GET");
        assertThat(sample).isNull();
        assertThat(sliCollector.getActiveRequestCount()).isEqualTo(0);
        
        sliCollector.recordMethodExecution("TestClass", "testMethod", Duration.ofMillis(10), null);
        
        // Verify no metrics were created (except the initial saturation gauge)
        assertThat(meterRegistry.find("application.http.request.duration").timers()).isEmpty();
        assertThat(meterRegistry.find("application.method.duration").timers()).isEmpty();
    }

    /**
     * Tests class name sanitization.
     */
    /*
    [003][M1][SLI數據收集]
    input: void
    output: void
    測試類名清理
    */
    @Test
    void testClassNameSanitization() {
        sliCollector.recordMethodExecution("com.example.service.UserService", "getUser", Duration.ofMillis(10), null);
        sliCollector.recordMethodExecution("UserController", "createUser", Duration.ofMillis(15), null);
        sliCollector.recordMethodExecution(null, "nullClassMethod", Duration.ofMillis(5), null);
        
        // Verify sanitized class names in metrics
        Timer timer1 = meterRegistry.find("application.method.duration")
                                   .tag("class", "UserService")
                                   .timer();
        Timer timer2 = meterRegistry.find("application.method.duration")
                                   .tag("class", "UserController")
                                   .timer();
        Timer timer3 = meterRegistry.find("application.method.duration")
                                   .tag("class", "unknown")
                                   .timer();
        
        assertThat(timer1).isNotNull();
        assertThat(timer2).isNotNull();
        assertThat(timer3).isNotNull();
    }

    /**
     * Tests exception handling doesn't break metrics collection.
     */
    /*
    [003][M1][SLI數據收集]
    input: void
    output: void
    測試異常處理不會破壞指標收集
    */
    @Test
    void testExceptionHandlingInMetricsCollection() {
        // These should not throw exceptions even with edge case data
        Timer.Sample sample = sliCollector.startTimer(null, null);
        sliCollector.stopTimer(sample, null, null, -1, null);
        
        sliCollector.recordMethodExecution("", "", Duration.ZERO, null);
        sliCollector.recordMethodExecution("test", "test", Duration.ofNanos(-1), null);
        
        // Should still be operational
        assertThat(sliCollector.getActiveRequestCount()).isGreaterThanOrEqualTo(0);
    }
}