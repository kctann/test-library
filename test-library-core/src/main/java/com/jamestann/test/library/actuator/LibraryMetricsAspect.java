/*
[003][M1][Metrics AOP切面]
基於註解和配置的 AOP 切面，提供精確的方法層級監控
替代舊的基於 Spring 註解的自動監控模式
*/
package com.jamestann.test.library.actuator;

import com.jamestann.test.library.actuator.annotation.IncludeMonitoring;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.time.Duration;

/**
 * AOP aspect for annotation-based and configuration-driven method monitoring.
 * <p>
 * This aspect replaces the old automatic monitoring approach with a precise
 * control system based on custom annotations and package configuration.
 * It monitors methods based on:
 * <ul>
 * <li>@IncludeMonitoring annotations with custom metric names</li>
 * <li>Package-based inclusion/exclusion configuration</li>
 * <li>Spring stereotype annotations when enabled in configuration</li>
 * </ul>
 * 
 * @author James Tann
 * @since 1.0.0
 */
/*
[003][M1][Metrics AOP切面]
基於註解和配置驅動的方法監控 AOP 切面
提供精確的監控控制，替代自動監控模式
*/
@Aspect
public class LibraryMetricsAspect {

    private final LibrarySLICollector sliCollector;
    private final MonitoringConfiguration monitoringConfig;

    /**
     * Creates a new LibraryMetricsAspect.
     * 
     * @param sliCollector the SLI collector instance
     * @param monitoringConfig the monitoring configuration
     */
    public LibraryMetricsAspect(LibrarySLICollector sliCollector, 
                               MonitoringConfiguration monitoringConfig) {
        this.sliCollector = sliCollector;
        this.monitoringConfig = monitoringConfig;
    }

    /**
     * Around advice for methods explicitly marked with @IncludeMonitoring.
     * <p>
     * This has the highest priority and always monitors methods with this annotation,
     * using the custom metric name and configuration specified in the annotation.
     * 
     * @param joinPoint the join point representing the method execution
     * @param includeMonitoring the @IncludeMonitoring annotation
     * @return the result of the method execution
     * @throws Throwable if the method throws an exception
     */
    /*
    [003][M1][Metrics AOP切面]
    input: ProceedingJoinPoint, IncludeMonitoring
    output: Object
    監控明確標記 @IncludeMonitoring 的方法，使用自定義指標名稱
    */
    @Around("@annotation(includeMonitoring)")
    public Object monitorIncludedMethods(ProceedingJoinPoint joinPoint, 
                                        IncludeMonitoring includeMonitoring) throws Throwable {
        return executeWithMonitoring(joinPoint, includeMonitoring);
    }

    /**
     * Around advice for @RestController methods when enabled in configuration.
     * <p>
     * This monitors RestController methods based on package configuration and
     * global RestController monitoring settings.
     * 
     * @param joinPoint the join point representing the method execution
     * @return the result of the method execution
     * @throws Throwable if the method throws an exception
     */
    /*
    [003][M1][Metrics AOP切面]
    input: ProceedingJoinPoint
    output: Object
    當配置啟用時監控 @RestController 方法
    */
    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object monitorRestControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = getMethod(joinPoint);
        Class<?> targetClass = joinPoint.getTarget().getClass();
        
        if (monitoringConfig.shouldMonitorMethod(method, targetClass)) {
            return executeWithMonitoring(joinPoint, null);
        }
        
        return joinPoint.proceed();
    }

    /**
     * Around advice for @Controller methods when enabled in configuration.
     * 
     * @param joinPoint the join point representing the method execution
     * @return the result of the method execution
     * @throws Throwable if the method throws an exception
     */
    /*
    [003][M1][Metrics AOP切面]
    input: ProceedingJoinPoint
    output: Object
    當配置啟用時監控 @Controller 方法
    */
    @Around("@within(org.springframework.stereotype.Controller)")
    public Object monitorControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = getMethod(joinPoint);
        Class<?> targetClass = joinPoint.getTarget().getClass();
        
        if (monitoringConfig.shouldMonitorMethod(method, targetClass)) {
            return executeWithMonitoring(joinPoint, null);
        }
        
        return joinPoint.proceed();
    }

    /**
     * Around advice for @Service methods when enabled in configuration.
     * 
     * @param joinPoint the join point representing the method execution
     * @return the result of the method execution
     * @throws Throwable if the method throws an exception
     */
    /*
    [003][M1][Metrics AOP切面]
    input: ProceedingJoinPoint
    output: Object
    當配置啟用時監控 @Service 方法
    */
    @Around("@within(org.springframework.stereotype.Service)")
    public Object monitorServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = getMethod(joinPoint);
        Class<?> targetClass = joinPoint.getTarget().getClass();
        
        if (monitoringConfig.shouldMonitorMethod(method, targetClass)) {
            return executeWithMonitoring(joinPoint, null);
        }
        
        return joinPoint.proceed();
    }

    /**
     * Around advice for @Repository methods when enabled in configuration.
     * 
     * @param joinPoint the join point representing the method execution
     * @return the result of the method execution
     * @throws Throwable if the method throws an exception
     */
    /*
    [003][M1][Metrics AOP切面]
    input: ProceedingJoinPoint
    output: Object
    當配置啟用時監控 @Repository 方法
    */
    @Around("@within(org.springframework.stereotype.Repository)")
    public Object monitorRepositoryMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = getMethod(joinPoint);
        Class<?> targetClass = joinPoint.getTarget().getClass();
        
        if (monitoringConfig.shouldMonitorMethod(method, targetClass)) {
            return executeWithMonitoring(joinPoint, null);
        }
        
        return joinPoint.proceed();
    }

    /**
     * Executes method with monitoring, handling both annotated and configured monitoring.
     * 
     * @param joinPoint the method join point
     * @param includeMonitoring the @IncludeMonitoring annotation, or null if not present
     * @return the method execution result
     * @throws Throwable if the method throws an exception
     */
    /*
    [003][M1][Metrics AOP切面]
    input: ProceedingJoinPoint, IncludeMonitoring
    output: Object
    執行帶監控的方法，處理註解和配置驅動的監控
    */
    private Object executeWithMonitoring(ProceedingJoinPoint joinPoint, 
                                        IncludeMonitoring includeMonitoring) throws Throwable {
        long startTime = System.nanoTime();
        Method method = getMethod(joinPoint);
        Class<?> targetClass = joinPoint.getTarget().getClass();
        
        Throwable exception = null;
        Object result = null;
        
        try {
            result = joinPoint.proceed();
            return result;
            
        } catch (Throwable throwable) {
            exception = throwable;
            throw throwable;
            
        } finally {
            long endTime = System.nanoTime();
            Duration duration = Duration.ofNanos(endTime - startTime);
            
            try {
                recordMethodMetrics(method, targetClass, includeMonitoring, duration, exception);
            } catch (Exception e) {
                // Don't let metrics collection errors affect the main application
                // TODO: [003][M2] Add proper error handling and logging
            }
        }
    }

    /**
     * Records method execution metrics using the appropriate metric name and tags.
     * 
     * @param method the executed method
     * @param targetClass the target class
     * @param includeMonitoring the @IncludeMonitoring annotation, or null
     * @param duration the execution duration
     * @param exception the exception if any occurred
     */
    /*
    [003][M1][Metrics AOP切面]
    input: Method, Class<?>, IncludeMonitoring, Duration, Throwable
    output: void
    記錄方法執行指標，使用適當的指標名稱和標籤
    */
    private void recordMethodMetrics(Method method, Class<?> targetClass, 
                                   IncludeMonitoring includeMonitoring, 
                                   Duration duration, Throwable exception) {
        if (includeMonitoring != null) {
            // Use custom metric name and configuration from annotation
            String metricName = includeMonitoring.name();
            String description = monitoringConfig.getCustomDescription(method);
            String[] tags = monitoringConfig.getCustomTags(method);
            
            sliCollector.recordMethodExecution(metricName, description, tags, duration, exception);
        } else {
            // Use legacy format for configuration-based monitoring
            String className = targetClass.getName();
            String methodName = method.getName();
            
            sliCollector.recordMethodExecution(className, methodName, duration, exception);
        }
    }

    /**
     * Gets the Method object from a ProceedingJoinPoint.
     * 
     * @param joinPoint the join point
     * @return the Method object
     */
    /*
    [003][M1][Metrics AOP切面]
    input: ProceedingJoinPoint
    output: Method
    從 ProceedingJoinPoint 獲取 Method 對象
    */
    private Method getMethod(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod();
    }
}