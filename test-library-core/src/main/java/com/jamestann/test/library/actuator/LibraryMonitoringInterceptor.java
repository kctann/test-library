package com.jamestann.test.library.actuator;

import io.micrometer.core.instrument.Timer;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Optional basic HTTP statistics interceptor for client application.
 * <p>
 * Provides basic HTTP request statistics collection as a complement to the 
 * main annotation-based monitoring system. This interceptor is optional and
 * provides coarse-grained HTTP-level metrics, while the main monitoring
 * is done through AOP aspects with business-meaningful names.
 * 
 * @author James Tann
 * @since 1.0.0
 * @deprecated Consider using annotation-based monitoring instead for more precise control
 */
/*
[003][M1][AOP攔截器]
可選的基礎 HTTP 統計攔截器
提供粗粒度的 HTTP 層級指標，主要監控通過 AOP 切面實現
*/
@Deprecated
public class LibraryMonitoringInterceptor implements HandlerInterceptor {

    private final LibrarySLICollector sliCollector;
    private final LibraryActuatorProperties properties;
    private final PathMatcher pathMatcher;
    
    // Attribute names for storing request data
    private static final String TIMER_SAMPLE_ATTRIBUTE = "library.timer.sample";
    private static final String REQUEST_START_TIME_ATTRIBUTE = "library.request.start.time";
    private static final String REQUEST_PATH_ATTRIBUTE = "library.request.path";

    /**
     * Creates a new LibraryMonitoringInterceptor.
     * 
     * @param sliCollector the SLI collector instance
     * @param properties the Library actuator properties
     */
    public LibraryMonitoringInterceptor(LibrarySLICollector sliCollector, 
                                       LibraryActuatorProperties properties) {
        this.sliCollector = sliCollector;
        this.properties = properties;
        this.pathMatcher = new AntPathMatcher();
    }

    /**
     * Pre-handle method called before the handler execution.
     * <p>
     * Starts timing the request and stores necessary information for later processing.
     * Checks if the request path should be monitored based on include/exclude filters.
     * 
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler chosen handler to execute
     * @return true to continue with handler execution
     */
    /*
    [003][M1][AOP攔截器]
    input: HttpServletRequest, HttpServletResponse, Object
    output: boolean
    在處理器執行前調用，開始計時請求並檢查是否應該監控此路徑
    */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!properties.getMetrics().isEnabled()) {
            return true;
        }
        
        String requestPath = getRequestPath(request);
        
        // Check if this path should be monitored
        if (!shouldMonitorPath(requestPath)) {
            return true;
        }
        
        // Store request information
        request.setAttribute(REQUEST_PATH_ATTRIBUTE, requestPath);
        request.setAttribute(REQUEST_START_TIME_ATTRIBUTE, System.currentTimeMillis());
        
        // Start timing
        Timer.Sample timerSample = sliCollector.startTimer(requestPath, request.getMethod());
        if (timerSample != null) {
            request.setAttribute(TIMER_SAMPLE_ATTRIBUTE, timerSample);
        }
        
        return true;
    }

    /**
     * After completion method called after handler execution and view rendering.
     * <p>
     * Stops timing and records all SLI metrics including latency, traffic, and errors.
     * This method is always called regardless of handler outcome or exceptions.
     * 
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler chosen handler that was executed
     * @param ex exception thrown during handler execution, if any
     */
    /*
    [003][M1][AOP攔截器]
    input: HttpServletRequest, HttpServletResponse, Object, Exception
    output: void
    在處理完成後調用，停止計時並記錄所有 SLI 指標
    */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                               Object handler, Exception ex) {
        if (!properties.getMetrics().isEnabled()) {
            return;
        }
        
        // Get stored request information
        Timer.Sample timerSample = (Timer.Sample) request.getAttribute(TIMER_SAMPLE_ATTRIBUTE);
        String requestPath = (String) request.getAttribute(REQUEST_PATH_ATTRIBUTE);
        
        if (timerSample == null || requestPath == null) {
            return;
        }
        
        try {
            // Get response information
            int statusCode = response.getStatus();
            String method = request.getMethod();
            
            // Stop timing and record metrics
            sliCollector.stopTimer(timerSample, requestPath, method, statusCode, ex);
            
        } catch (Exception e) {
            // Don't let metrics collection errors affect the main application
            // TODO: [003][M2] Add proper error handling and logging
        } finally {
            // Clean up request attributes
            cleanupRequestAttributes(request);
        }
    }

    /**
     * Determines if a request path should be monitored.
     * <p>
     * This simplified version only excludes common framework paths.
     * For detailed monitoring control, use annotation-based monitoring instead.
     * 
     * @param requestPath the request path to check
     * @return true if the path should be monitored
     */
    /*
    [003][M1][AOP攔截器]
    input: String
    output: boolean
    簡化的路徑監控判斷，僅排除常見框架路徑
    */
    private boolean shouldMonitorPath(String requestPath) {
        // Only exclude common framework paths
        if (requestPath.startsWith("/actuator/") || 
            requestPath.startsWith("/static/") ||
            requestPath.startsWith("/webjars/") ||
            requestPath.equals("/favicon.ico") ||
            requestPath.equals("/error")) {
            return false;
        }
        
        return true;
    }

    /**
     * Extracts the request path from HttpServletRequest.
     * 
     * @param request the HTTP request
     * @return the request path
     */
    /*
    [003][M1][AOP攔截器]
    input: HttpServletRequest
    output: String
    從 HttpServletRequest 提取請求路徑
    */
    private String getRequestPath(HttpServletRequest request) {
        String requestPath = request.getRequestURI();
        String contextPath = request.getContextPath();
        
        // Remove context path if present
        if (contextPath != null && !contextPath.isEmpty() && requestPath.startsWith(contextPath)) {
            requestPath = requestPath.substring(contextPath.length());
        }
        
        // Ensure path starts with '/'
        if (!requestPath.startsWith("/")) {
            requestPath = "/" + requestPath;
        }
        
        return requestPath;
    }

    /**
     * Cleans up request attributes to avoid memory leaks.
     * 
     * @param request the HTTP request
     */
    /*
    [003][M1][AOP攔截器]
    input: HttpServletRequest
    output: void
    清理請求屬性以避免記憶體洩漏
    */
    private void cleanupRequestAttributes(HttpServletRequest request) {
        request.removeAttribute(TIMER_SAMPLE_ATTRIBUTE);
        request.removeAttribute(REQUEST_START_TIME_ATTRIBUTE);
        request.removeAttribute(REQUEST_PATH_ATTRIBUTE);
    }
}