/*
[003][M1][監控註解系統]
排除監控註解
用於明確標記不需要監控的方法或類
*/
package com.jamestann.test.library.actuator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to explicitly exclude a method or class from monitoring.
 * <p>
 * This annotation allows users to exclude specific methods or entire classes
 * from monitoring, even if they fall within monitored packages. This is useful
 * for excluding internal methods, health checks, or test endpoints.
 * 
 * @author James Tann
 * @since 1.0.0
 */
/*
[003][M1][監控註解系統]
用於明確排除監控的方法或類註解
即使在被監控的 package 中也可以排除特定方法
*/
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcludeMonitoring {
    
    /**
     * The reason why this method or class is excluded from monitoring.
     * <p>
     * This is optional but recommended for documentation purposes.
     * It helps other developers understand why monitoring was disabled.
     * <p>
     * Example: "Internal health check", "Performance sensitive method", 
     * "Contains sensitive data"
     * 
     * @return the reason for exclusion
     */
    /*
    [003][M1][監控註解系統]
    input: void
    output: String
    排除監控的原因，用於文檔說明
    */
    String reason() default "";
    
    /**
     * Categories of exclusion for better organization and reporting.
     * <p>
     * This can help classify why different methods are excluded, making it
     * easier to review and audit monitoring exclusions.
     * <p>
     * Common categories: "health-check", "internal", "performance", "security"
     * 
     * @return array of exclusion categories
     */
    /*
    [003][M1][監控註解系統]
    input: void
    output: String[]
    排除類別，用於更好的組織和報告
    */
    String[] categories() default {};
    
    /**
     * Whether this exclusion applies to all monitoring aspects.
     * <p>
     * When false, some basic metrics (like call count) might still be collected
     * while detailed metrics (like execution time, parameters) are excluded.
     * When true, no monitoring data is collected at all.
     * 
     * @return true to completely exclude from all monitoring
     */
    /*
    [003][M1][監控註解系統]
    input: void
    output: boolean
    是否完全排除所有監控（true）或僅排除詳細監控（false）
    */
    boolean complete() default true;
    
    /**
     * Temporary exclusion with expiration information.
     * <p>
     * This is for documentation only and indicates when the exclusion
     * should be reviewed or potentially removed.
     * <p>
     * Format: "YYYY-MM-DD" or descriptive text like "after performance optimization"
     * 
     * @return when this exclusion should be reviewed
     */
    /*
    [003][M1][監控註解系統]
    input: void
    output: String
    臨時排除的過期信息，用於提醒何時重新評估排除決定
    */
    String reviewAfter() default "";
}