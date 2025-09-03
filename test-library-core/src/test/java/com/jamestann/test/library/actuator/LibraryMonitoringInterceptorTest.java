/*
[003][M1][AOP攔截器]
Library Monitoring Interceptor測試類別
測試 Web 請求攔截和監控功能（Mock HTTP 對象）
*/
package com.jamestann.test.library.actuator;

import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for LibraryMonitoringInterceptor.
 * <p>
 * Tests HTTP request interception and monitoring functionality
 * using Mock HTTP objects without full Spring context.
 * 
 * @author James Tann
 * @since 1.0.0
 */
/*
[003][M1][AOP攔截器]
LibraryMonitoringInterceptor 的單元測試
使用 Mock HTTP 對象測試請求攔截和監控功能
*/
@ExtendWith(MockitoExtension.class)
class LibraryMonitoringInterceptorTest {

    @Mock
    private LibrarySLICollector sliCollector;
    
    @Mock
    private Timer.Sample timerSample;
    
    private LibraryActuatorProperties properties;
    private LibraryMonitoringInterceptor interceptor;

    /**
     * Sets up test fixtures before each test.
     */
    /*
    [003][M1][AOP攔截器]
    input: void
    output: void
    在每個測試前設置測試裝置
    */
    @BeforeEach
    void setUp() {
        properties = new LibraryActuatorProperties();
        properties.getMetrics().setEnabled(true);
        interceptor = new LibraryMonitoringInterceptor(sliCollector, properties);
    }

    /**
     * Tests successful request monitoring flow.
     */
    /*
    [003][M1][AOP攔截器]
    input: void
    output: void
    測試成功的請求監控流程
    */
    @Test
    void testSuccessfulRequestMonitoring() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users");
        MockHttpServletResponse response = new MockHttpServletResponse();
        Object handler = new Object();
        
        when(sliCollector.startTimer(anyString(), anyString())).thenReturn(timerSample);
        
        // Test preHandle
        boolean result = interceptor.preHandle(request, response, handler);
        assertThat(result).isTrue();
        
        verify(sliCollector).startTimer("/api/users", "GET");
        assertThat(request.getAttribute("library.timer.sample")).isNotNull();
        assertThat(request.getAttribute("library.request.path")).isEqualTo("/api/users");
        assertThat(request.getAttribute("library.request.start.time")).isNotNull();
        
        // Test afterCompletion
        response.setStatus(200);
        interceptor.afterCompletion(request, response, handler, null);
        
        verify(sliCollector).stopTimer(timerSample, "/api/users", "GET", 200, null);
        
        // Verify attributes are cleaned up
        assertThat(request.getAttribute("library.timer.sample")).isNull();
        assertThat(request.getAttribute("library.request.path")).isNull();
        assertThat(request.getAttribute("library.request.start.time")).isNull();
    }

    /**
     * Tests request monitoring with exception.
     */
    /*
    [003][M1][AOP攔截器]
    input: void
    output: void
    測試帶異常的請求監控
    */
    @Test
    void testRequestMonitoringWithException() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/users");
        MockHttpServletResponse response = new MockHttpServletResponse();
        Object handler = new Object();
        RuntimeException exception = new RuntimeException("Service error");
        
        when(sliCollector.startTimer(anyString(), anyString())).thenReturn(timerSample);
        
        // Test preHandle
        interceptor.preHandle(request, response, handler);
        
        // Test afterCompletion with exception
        response.setStatus(500);
        interceptor.afterCompletion(request, response, handler, exception);
        
        verify(sliCollector).stopTimer(timerSample, "/api/users", "POST", 500, exception);
    }

    /**
     * Tests path filtering with include patterns.
     */
    /*
    [003][M1][AOP攔截器]
    input: void
    output: void
    測試包含模式的路徑過濾
    */
    @Test
    void testBasicPathFiltering() {
        // Should monitor normal API paths
        MockHttpServletRequest apiRequest = new MockHttpServletRequest("GET", "/api/users");
        boolean result1 = interceptor.preHandle(apiRequest, new MockHttpServletResponse(), new Object());
        assertThat(result1).isTrue();
        verify(sliCollector).startTimer("/api/users", "GET");
        
        // Should not monitor actuator paths (framework exclusion)
        reset(sliCollector);
        MockHttpServletRequest actuatorRequest = new MockHttpServletRequest("GET", "/actuator/health");
        boolean result2 = interceptor.preHandle(actuatorRequest, new MockHttpServletResponse(), new Object());
        assertThat(result2).isTrue();
        verify(sliCollector, never()).startTimer(anyString(), anyString());
    }

    /**
     * Tests path filtering with exclude patterns.
     */
    /*
    [003][M1][AOP攔截器]
    input: void
    output: void
    測試排除模式的路徑過濾
    */
    @Test
    void testFrameworkPathExclusion() {
        // Should not monitor /actuator/health (built-in framework exclusion)
        MockHttpServletRequest actuatorRequest = new MockHttpServletRequest("GET", "/actuator/health");
        boolean result1 = interceptor.preHandle(actuatorRequest, new MockHttpServletResponse(), new Object());
        assertThat(result1).isTrue();
        verify(sliCollector, never()).startTimer(anyString(), anyString());
        
        // Should not monitor /error (built-in framework exclusion) 
        MockHttpServletRequest errorRequest = new MockHttpServletRequest("GET", "/error");
        boolean result2 = interceptor.preHandle(errorRequest, new MockHttpServletResponse(), new Object());
        assertThat(result2).isTrue();
        verify(sliCollector, never()).startTimer(anyString(), anyString());
        
        // Should monitor /api/users
        MockHttpServletRequest apiRequest = new MockHttpServletRequest("GET", "/api/users");
        boolean result3 = interceptor.preHandle(apiRequest, new MockHttpServletResponse(), new Object());
        assertThat(result3).isTrue();
        verify(sliCollector).startTimer("/api/users", "GET");
    }

    /**
     * Tests request path extraction and normalization.
     */
    /*
    [003][M1][AOP攔截器]
    input: void
    output: void
    測試請求路徑提取和正規化
    */
    @Test
    void testRequestPathExtraction() {
        // Test with context path
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/myapp/api/users");
        request.setContextPath("/myapp");
        
        when(sliCollector.startTimer(anyString(), anyString())).thenReturn(timerSample);
        
        interceptor.preHandle(request, new MockHttpServletResponse(), new Object());
        
        verify(sliCollector).startTimer("/api/users", "GET");
        assertThat(request.getAttribute("library.request.path")).isEqualTo("/api/users");
    }

    /**
     * Tests request path normalization edge cases.
     */
    /*
    [003][M1][AOP攔截器]
    input: void
    output: void
    測試請求路徑正規化邊緣情況
    */
    @Test
    void testRequestPathNormalizationEdgeCases() {
        when(sliCollector.startTimer(anyString(), anyString())).thenReturn(timerSample);
        
        // Test root path
        MockHttpServletRequest rootRequest = new MockHttpServletRequest("GET", "");
        interceptor.preHandle(rootRequest, new MockHttpServletResponse(), new Object());
        verify(sliCollector).startTimer("/", "GET");
        
        // Test path without leading slash
        reset(sliCollector);
        when(sliCollector.startTimer(anyString(), anyString())).thenReturn(timerSample);
        MockHttpServletRequest noSlashRequest = new MockHttpServletRequest("GET", "api/users");
        interceptor.preHandle(noSlashRequest, new MockHttpServletResponse(), new Object());
        verify(sliCollector).startTimer("/api/users", "GET");
    }

    /**
     * Tests monitoring disabled scenario.
     */
    /*
    [003][M1][AOP攔截器]
    input: void
    output: void
    測試監控禁用場景
    */
    @Test
    void testMonitoringDisabled() {
        properties.getMetrics().setEnabled(false);
        
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users");
        MockHttpServletResponse response = new MockHttpServletResponse();
        Object handler = new Object();
        
        // Test preHandle - should return true but not start monitoring
        boolean result = interceptor.preHandle(request, response, handler);
        assertThat(result).isTrue();
        verify(sliCollector, never()).startTimer(anyString(), anyString());
        
        // Test afterCompletion - should not call stopTimer
        interceptor.afterCompletion(request, response, handler, null);
        verify(sliCollector, never()).stopTimer(any(), anyString(), anyString(), anyInt(), any());
    }

    /**
     * Tests handling of missing timer sample in afterCompletion.
     */
    /*
    [003][M1][AOP攔截器]
    input: void
    output: void
    測試 afterCompletion 中缺少計時器樣本的處理
    */
    @Test
    void testMissingTimerSampleInAfterCompletion() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users");
        MockHttpServletResponse response = new MockHttpServletResponse();
        Object handler = new Object();
        
        // Don't call preHandle, so no timer sample is stored
        interceptor.afterCompletion(request, response, handler, null);
        
        // Should not call stopTimer
        verify(sliCollector, never()).stopTimer(any(), anyString(), anyString(), anyInt(), any());
    }

    /**
     * Tests error handling in metrics collection.
     */
    /*
    [003][M1][AOP攔截器]
    input: void
    output: void
    測試指標收集中的錯誤處理
    */
    @Test
    void testErrorHandlingInMetricsCollection() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users");
        MockHttpServletResponse response = new MockHttpServletResponse();
        Object handler = new Object();
        
        when(sliCollector.startTimer(anyString(), anyString())).thenReturn(timerSample);
        doThrow(new RuntimeException("Metrics error")).when(sliCollector)
          .stopTimer(any(), anyString(), anyString(), anyInt(), any());
        
        // Should not throw exception even if metrics collection fails
        interceptor.preHandle(request, response, handler);
        
        // This should not throw any exception
        try {
            interceptor.afterCompletion(request, response, handler, null);
            // If we get here, no exception was thrown - test passes
        } catch (Exception e) {
            fail("Expected no exception, but got: " + e.getMessage());
        }
        
        // Attributes should still be cleaned up
        assertThat(request.getAttribute("library.timer.sample")).isNull();
    }

    /**
     * Tests complex path patterns with Ant matcher.
     */
    /*
    [003][M1][AOP攔截器]
    input: void
    output: void
    測試使用 Ant 匹配器的複雜路徑模式
    */
    @Test
    void testSimplifiedPathHandling() {
        // Should monitor normal API paths
        MockHttpServletRequest apiRequest = new MockHttpServletRequest("GET", "/api/v1/users/123");
        boolean result1 = interceptor.preHandle(apiRequest, new MockHttpServletResponse(), new Object());
        assertThat(result1).isTrue();
        verify(sliCollector).startTimer("/api/v1/users/123", "GET");
        
        // Should not monitor static resources
        reset(sliCollector);
        MockHttpServletRequest staticRequest = new MockHttpServletRequest("GET", "/static/css/style.css");
        boolean result2 = interceptor.preHandle(staticRequest, new MockHttpServletResponse(), new Object());
        assertThat(result2).isTrue();
        verify(sliCollector, never()).startTimer(anyString(), anyString());
    }

    private static org.assertj.core.api.ThrowableAssert.ThrowingCallable assertThatCode(Runnable runnable) {
        return () -> runnable.run();
    }
}