/*
[003][M1][監控註解系統]
包含監控註解
用於明確標記需要監控的方法
*/
package com.jamestann.test.library.actuator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to explicitly include a method for monitoring.
 * <p>
 * This annotation allows users to precisely control which methods should be monitored,
 * providing custom metric names and descriptions for business-meaningful monitoring.
 * 
 * @author James Tann
 * @since 1.0.0
 */
/*
[003][M1][監控註解系統]
用於明確包含監控的方法註解
提供自定義指標名稱和業務語意的監控
*/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IncludeMonitoring {
    
    /**
     * The custom metric name for this method.
     * <p>
     * This should be a business-meaningful name that reflects what the method does,
     * rather than technical implementation details.
     * <p>
     * Example: "user.login", "order.create", "report.generate"
     * 
     * @return the metric name
     */
    /*
    [003][M1][監控註解系統]
    input: void
    output: String
    自定義指標名稱，應該反映業務邏輯而非技術實現
    */
    String name();
    
    /**
     * Optional description of what this method does.
     * <p>
     * This description will be included in the metric metadata and can help
     * with monitoring dashboard documentation.
     * 
     * @return the description of the monitored method
     */
    /*
    [003][M1][監控註解系統]
    input: void
    output: String
    方法描述，用於監控儀表板文檔
    */
    String description() default "";
    
    /**
     * Additional tags to be applied to the metrics.
     * <p>
     * Tags should be in the format "key=value". Multiple tags can be specified.
     * <p>
     * Example: {"operation=read", "resource=user", "priority=high"}
     * 
     * @return array of tag strings in key=value format
     */
    /*
    [003][M1][監控註解系統]
    input: void
    output: String[]
    額外的指標標籤，格式為 key=value
    */
    String[] tags() default {};
    
    /**
     * Whether to record method parameters in metrics.
     * <p>
     * When enabled, method parameter information will be included in metrics.
     * Use with caution to avoid high cardinality metrics or sensitive data exposure.
     * 
     * @return true to include parameter information in metrics
     */
    /*
    [003][M1][監控註解系統]
    input: void
    output: boolean
    是否在指標中記錄方法參數（謹慎使用以避免高基數或敏感數據）
    */
    boolean includeParameters() default false;
    
    /**
     * Whether to record the return value type in metrics.
     * <p>
     * When enabled, the return value type will be included as a metric tag.
     * This can be useful for monitoring methods that return different types
     * based on business logic.
     * 
     * @return true to include return type information in metrics
     */
    /*
    [003][M1][監控註解系統]
    input: void
    output: boolean
    是否在指標中記錄返回值類型
    */
    boolean includeReturnType() default false;
    
    /**
     * Custom success criteria for this method.
     * <p>
     * By default, a method is considered successful if it completes without throwing
     * an exception. This can be overridden to define custom success criteria.
     * <p>
     * Example: "response.status == 200", "result != null"
     * 
     * @return success criteria expression (currently for documentation only)
     */
    /*
    [003][M1][監控註解系統]
    input: void
    output: String
    自定義成功標準（目前僅用於文檔，未來可擴展為 SpEL 表達式）
    */
    String successCriteria() default "";
}