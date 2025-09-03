package com.jamestann.test.library.actuator;

import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.Timer.Sample;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * SLI (Service Level Indicator) collector for client application server.
 * <p>
 * Implements Golden Signals data collection including Latency, Traffic, Errors, and Saturation.
 * Provides thread-safe metrics collection with configurable percentiles and tags for the client server.
 * 
 * @author James Tann
 * @since 1.0.0
 */
/*
[003][M1][SLI數據收集]
客戶端伺服器的 SLI 數據收集器，實作 Golden Signals 數據收集
收集客戶端應用程式的延遲、流量、錯誤率、飽和度指標
*/
public class LibrarySLICollector {

    private final MeterRegistry meterRegistry;
    private final LibraryActuatorProperties properties;
    
    // Golden Signals metrics
    private final ConcurrentHashMap<String, Timer> latencyTimers;
    private final ConcurrentHashMap<String, Counter> trafficCounters;
    private final ConcurrentHashMap<String, Counter> errorCounters;
    private final AtomicLong activeRequests;
    
    // Metric name constants
    private static final String METRIC_PREFIX = "application";
    private static final String LATENCY_METRIC = METRIC_PREFIX + ".http.request.duration";
    private static final String TRAFFIC_METRIC = METRIC_PREFIX + ".http.request.total";
    private static final String ERROR_METRIC = METRIC_PREFIX + ".http.request.errors";
    private static final String SATURATION_METRIC = METRIC_PREFIX + ".http.request.active";
    private static final String METHOD_METRIC = METRIC_PREFIX + ".method.duration";

    /**
     * Creates a new LibrarySLICollector.
     * 
     * @param meterRegistry the Micrometer meter registry
     * @param properties the Library actuator properties
     */
    public LibrarySLICollector(MeterRegistry meterRegistry, LibraryActuatorProperties properties) {
        this.meterRegistry = meterRegistry;
        this.properties = properties;
        
        this.latencyTimers = new ConcurrentHashMap<>();
        this.trafficCounters = new ConcurrentHashMap<>();
        this.errorCounters = new ConcurrentHashMap<>();
        this.activeRequests = new AtomicLong(0);
        
        initializeMetrics();
    }

    /**
     * Initializes base metrics and gauges.
     */
    /*
    [003][M1][SLI數據收集]
    input: void
    output: void
    初始化基礎指標和計量器
    */
    private void initializeMetrics() {
        // Register active requests gauge (Saturation)
        Gauge.builder(SATURATION_METRIC, activeRequests, AtomicLong::get)
             .description("Number of currently active HTTP requests in the application")
             .register(meterRegistry);
    }

    /**
     * Starts timing an HTTP request and returns a timer sample.
     * <p>
     * This method should be called at the beginning of HTTP request processing
     * to start measuring latency (Golden Signal: Latency).
     * 
     * @param endpoint the request endpoint
     * @param method the HTTP method
     * @return Timer sample for stopping later
     */
    /*
    [003][M1][SLI數據收集]
    input: String endpoint, String method
    output: Timer.Sample
    開始計時 HTTP 請求並返回計時器樣本，測量延遲（Golden Signal: Latency）
    */
    public Timer.Sample startTimer(String endpoint, String method) {
        if (!properties.getMetrics().isEnabled()) {
            return null;
        }
        
        activeRequests.incrementAndGet();
        return Timer.start(meterRegistry);
    }

    /**
     * Stops timing and records the HTTP request completion.
     * <p>
     * Records latency, traffic, and error metrics based on the request outcome.
     * 
     * @param sample the timer sample from startTimer
     * @param endpoint the request endpoint
     * @param method the HTTP method
     * @param status the HTTP status code
     * @param exception the exception if any occurred
     */
    /*
    [003][M1][SLI數據收集]
    input: Timer.Sample, String, String, int, Throwable
    output: void
    停止計時並記錄 HTTP 請求完成，記錄延遲、流量和錯誤指標
    */
    public void stopTimer(Timer.Sample sample, String endpoint, String method, 
                         int status, Throwable exception) {
        if (!properties.getMetrics().isEnabled() || sample == null) {
            return;
        }
        
        try {
            activeRequests.decrementAndGet();
            
            // Record latency (Golden Signal: Latency)
            Timer latencyTimer = getLatencyTimer(endpoint, method, status);
            sample.stop(latencyTimer);
            
            // Record traffic (Golden Signal: Traffic)
            recordTraffic(endpoint, method, status);
            
            // Record errors if applicable (Golden Signal: Errors)
            if (isError(status) || exception != null) {
                recordError(endpoint, method, status, exception);
            }
            
        } catch (Exception e) {
            // Don't let metrics collection errors affect the main application
            // TODO: [003][M2] Add proper error handling and logging
        }
    }

    /**
     * Records a method execution with custom metric name and tags.
     * <p>
     * This method accepts user-defined metric names and tags, eliminating
     * the need for path or class name sanitization. Used for annotation-based
     * method monitoring with business-meaningful metric names.
     * 
     * @param metricName the custom metric name (e.g., "user.login", "order.create")
     * @param description optional description for the metric
     * @param tags additional tags for the metric
     * @param duration the execution duration
     * @param exception the exception if any occurred
     */
    /*
    [003][M1][SLI數據收集]
    input: String, String, String[], Duration, Throwable
    output: void
    記錄方法執行，使用自定義指標名稱和標籤，無需路徑或類名清理
    */
    public void recordMethodExecution(String metricName, String description, String[] tags,
                                     Duration duration, Throwable exception) {
        if (!properties.getMetrics().isEnabled()) {
            return;
        }
        
        // Build the timer with custom metric name
        Timer.Builder timerBuilder = Timer.builder(METRIC_PREFIX + ".method." + metricName)
             .description(description != null ? description : "Method execution time: " + metricName)
             .tag("status", exception == null ? "success" : "error");
        
        // Add custom tags
        for (String tag : tags) {
            String[] keyValue = parseTag(tag);
            if (keyValue.length == 2) {
                timerBuilder.tag(keyValue[0], keyValue[1]);
            }
        }
        
        timerBuilder.register(meterRegistry).record(duration);
        
        // Record error counter if exception occurred
        if (exception != null) {
            Counter.Builder counterBuilder = Counter.builder(METRIC_PREFIX + ".method." + metricName + ".errors")
                   .description("Method execution errors: " + metricName)
                   .tag("exception", exception.getClass().getSimpleName());
            
            // Add same custom tags to error counter
            for (String tag : tags) {
                String[] keyValue = parseTag(tag);
                if (keyValue.length == 2) {
                    counterBuilder.tag(keyValue[0], keyValue[1]);
                }
            }
            
            counterBuilder.register(meterRegistry).increment();
        }
    }

    /**
     * Records a method execution with legacy class/method name format.
     * <p>
     * This method is kept for backward compatibility but will generate
     * a simplified metric name without complex sanitization.
     * 
     * @param className the class name
     * @param methodName the method name
     * @param duration the execution duration
     * @param exception the exception if any occurred
     * @deprecated Use recordMethodExecution(String, String, String[], Duration, Throwable) instead
     */
    /*
    [003][M1][SLI數據收集]
    input: String, String, Duration, Throwable
    output: void
    記錄方法執行的遺留格式，為了向後兼容保留但會生成簡化的指標名稱
    */
    @Deprecated
    public void recordMethodExecution(String className, String methodName, 
                                     Duration duration, Throwable exception) {
        if (!properties.getMetrics().isEnabled()) {
            return;
        }
        
        // Generate simple metric name: class.method
        String simpleClassName = getSimpleClassName(className);
        String metricName = simpleClassName.toLowerCase() + "." + methodName;
        String description = "Legacy method monitoring: " + simpleClassName + "." + methodName;
        
        recordMethodExecution(metricName, description, new String[0], duration, exception);
    }

    /**
     * Gets or creates a latency timer for the given parameters.
     * 
     * @param endpoint the request endpoint
     * @param method the HTTP method
     * @param status the HTTP status code
     * @return Timer instance
     */
    /*
    [003][M1][SLI數據收集]
    input: String, String, int
    output: Timer
    獲取或創建給定參數的延遲計時器
    */
    private Timer getLatencyTimer(String endpoint, String method, int status) {
        String key = endpoint + ":" + method + ":" + status;
        return latencyTimers.computeIfAbsent(key, k -> createLatencyTimer(endpoint, method, status));
    }

    /**
     * Creates a new latency timer with appropriate configuration.
     * 
     * @param endpoint the request endpoint
     * @param method the HTTP method
     * @param status the HTTP status code
     * @return configured Timer instance
     */
    /*
    [003][M1][SLI數據收集]
    input: String, String, int
    output: Timer
    創建具有適當配置的新延遲計時器
    */
    private Timer createLatencyTimer(String endpoint, String method, int status) {
        Timer.Builder builder = Timer.builder(LATENCY_METRIC)
                                    .description("HTTP request duration for client application")
                                    .tag("endpoint", endpoint)
                                    .tag("method", method)
                                    .tag("status", String.valueOf(status));
        
        // Add percentiles if enabled
        if (properties.getMetrics().isIncludePercentiles()) {
            builder.publishPercentiles(properties.getMetrics().getPercentiles());
        }
        
        return builder.register(meterRegistry);
    }

    /**
     * Records traffic metrics (Golden Signal: Traffic).
     * 
     * @param endpoint the request endpoint
     * @param method the HTTP method
     * @param status the HTTP status code
     */
    /*
    [003][M1][SLI數據收集]
    input: String, String, int
    output: void
    記錄流量指標（Golden Signal: Traffic）
    */
    private void recordTraffic(String endpoint, String method, int status) {
        String key = endpoint + ":" + method;
        Counter counter = trafficCounters.computeIfAbsent(key, k -> 
            Counter.builder(TRAFFIC_METRIC)
                   .description("Total HTTP requests to client application")
                   .tag("endpoint", endpoint)
                   .tag("method", method)
                   .register(meterRegistry)
        );
        counter.increment();
    }

    /**
     * Records error metrics (Golden Signal: Errors).
     * 
     * @param endpoint the request endpoint
     * @param method the HTTP method
     * @param status the HTTP status code
     * @param exception the exception if any
     */
    /*
    [003][M1][SLI數據收集]
    input: String, String, int, Throwable
    output: void
    記錄錯誤指標（Golden Signal: Errors）
    */
    private void recordError(String endpoint, String method, int status, Throwable exception) {
        String key = endpoint + ":" + method + ":" + status;
        Counter counter = errorCounters.computeIfAbsent(key, k -> 
            Counter.builder(ERROR_METRIC)
                   .description("HTTP request errors in client application")
                   .tag("endpoint", endpoint)
                   .tag("method", method)
                   .tag("status", String.valueOf(status))
                   .tag("error_type", exception != null ? 
                        exception.getClass().getSimpleName() : "http_error")
                   .register(meterRegistry)
        );
        counter.increment();
    }

    /**
     * Determines if a status code represents an error.
     * 
     * @param status the HTTP status code
     * @return true if the status represents an error
     */
    /*
    [003][M1][SLI數據收集]
    input: int
    output: boolean
    判斷狀態碼是否代表錯誤
    */
    private boolean isError(int status) {
        return status >= 400;
    }

    /**
     * Parses a tag string in "key=value" format.
     * 
     * @param tag the tag string
     * @return array with key and value, or empty array if invalid format
     */
    /*
    [003][M1][SLI數據收集]
    input: String
    output: String[]
    解析 key=value 格式的標籤字串
    */
    private String[] parseTag(String tag) {
        if (tag == null || !tag.contains("=")) {
            return new String[0];
        }
        return tag.split("=", 2);
    }

    /**
     * Gets simple class name from fully qualified class name.
     * 
     * @param className the full class name
     * @return the simple class name
     */
    /*
    [003][M1][SLI數據收集]
    input: String
    output: String
    從完整類別名稱獲取簡單類別名稱
    */
    private String getSimpleClassName(String className) {
        if (className == null) {
            return "unknown";
        }
        
        int lastDotIndex = className.lastIndexOf('.');
        return lastDotIndex >= 0 ? className.substring(lastDotIndex + 1) : className;
    }

    /**
     * Gets current active request count (Golden Signal: Saturation).
     * 
     * @return current active request count
     */
    /*
    [003][M1][SLI數據收集]
    input: void
    output: long
    獲取當前活動請求數量（Golden Signal: Saturation）
    */
    public long getActiveRequestCount() {
        return activeRequests.get();
    }
}