/*
[003][M1][監控配置系統]
監控配置處理類
處理 package-based 監控邏輯和註解掃描
*/
package com.jamestann.test.library.actuator;

import com.jamestann.test.library.actuator.annotation.ExcludeMonitoring;
import com.jamestann.test.library.actuator.annotation.IncludeMonitoring;
import org.springframework.util.AntPathMatcher;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Configuration processor for package-based monitoring.
 * <p>
 * This class handles the logic for determining which methods should be monitored
 * based on package configuration, Spring annotations, and custom monitoring annotations.
 * 
 * @author James Tann
 * @since 1.0.0
 */
/*
[003][M1][監控配置系統]
基於 Package 的監控配置處理器
處理監控決策邏輯，包括包過濾、註解檢查等
*/
public class MonitoringConfiguration {
    
    private final LibraryActuatorProperties.Monitoring monitoring;
    private final AntPathMatcher pathMatcher;
    
    /**
     * Creates a new MonitoringConfiguration.
     * 
     * @param monitoring the monitoring configuration properties
     */
    public MonitoringConfiguration(LibraryActuatorProperties.Monitoring monitoring) {
        this.monitoring = monitoring;
        this.pathMatcher = new AntPathMatcher();
    }
    
    /**
     * Determines if a method should be monitored based on configuration and annotations.
     * <p>
     * Decision logic:
     * 1. Check if monitoring is globally enabled
     * 2. Check for explicit @ExcludeMonitoring annotation
     * 3. Check for explicit @IncludeMonitoring annotation  
     * 4. Check if class package is excluded
     * 5. Check if class package is included or matches Spring annotation patterns
     * 
     * @param method the method to check
     * @param targetClass the class containing the method
     * @return true if the method should be monitored
     */
    /*
    [003][M1][監控配置系統]
    input: Method, Class<?>
    output: boolean
    基於配置和註解決定是否應該監控該方法
    */
    public boolean shouldMonitorMethod(Method method, Class<?> targetClass) {
        // 1. Check if monitoring is globally disabled
        if (!monitoring.isEnabled()) {
            return false;
        }
        
        // 2. Check for explicit exclusion at method level
        if (method.isAnnotationPresent(ExcludeMonitoring.class)) {
            return false;
        }
        
        // 3. Check for explicit exclusion at class level
        if (targetClass.isAnnotationPresent(ExcludeMonitoring.class)) {
            return false;
        }
        
        // 4. Check for explicit inclusion at method level (highest priority)
        if (method.isAnnotationPresent(IncludeMonitoring.class)) {
            return true;
        }
        
        // 5. Check if class package is explicitly excluded
        String packageName = targetClass.getPackage().getName();
        if (isPackageExcluded(packageName)) {
            return false;
        }
        
        // 6. Check if class package is explicitly included
        if (isPackageIncluded(packageName)) {
            return true;
        }
        
        // 7. Check Spring annotation-based monitoring
        return shouldMonitorBySpringAnnotation(targetClass);
    }
    
    /**
     * Gets the custom metric name for a method, if specified.
     * 
     * @param method the method to check
     * @return the custom metric name, or null if not specified
     */
    /*
    [003][M1][監控配置系統]
    input: Method
    output: String
    獲取方法的自定義指標名稱
    */
    public String getCustomMetricName(Method method) {
        IncludeMonitoring annotation = method.getAnnotation(IncludeMonitoring.class);
        return annotation != null ? annotation.name() : null;
    }
    
    /**
     * Gets the custom description for a method, if specified.
     * 
     * @param method the method to check
     * @return the custom description, or null if not specified
     */
    /*
    [003][M1][監控配置系統]
    input: Method
    output: String
    獲取方法的自定義描述
    */
    public String getCustomDescription(Method method) {
        IncludeMonitoring annotation = method.getAnnotation(IncludeMonitoring.class);
        return annotation != null && !annotation.description().isEmpty() ? annotation.description() : null;
    }
    
    /**
     * Gets the custom tags for a method, if specified.
     * 
     * @param method the method to check
     * @return array of custom tags, or empty array if not specified
     */
    /*
    [003][M1][監控配置系統]
    input: Method
    output: String[]
    獲取方法的自定義標籤
    */
    public String[] getCustomTags(Method method) {
        IncludeMonitoring annotation = method.getAnnotation(IncludeMonitoring.class);
        return annotation != null ? annotation.tags() : new String[0];
    }
    
    /**
     * Checks if a package is explicitly excluded.
     * 
     * @param packageName the package name to check
     * @return true if the package is excluded
     */
    /*
    [003][M1][監控配置系統]
    input: String
    output: boolean
    檢查包是否被明確排除
    */
    private boolean isPackageExcluded(String packageName) {
        for (String excludePattern : monitoring.getExcludePackages()) {
            if (pathMatcher.match(excludePattern, packageName)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks if a package is explicitly included.
     * 
     * @param packageName the package name to check
     * @return true if the package is included
     */
    /*
    [003][M1][監控配置系統]
    input: String
    output: boolean
    檢查包是否被明確包含
    */
    private boolean isPackageIncluded(String packageName) {
        // If no packages are explicitly included, don't match anything here
        // (let Spring annotation matching handle it)
        if (monitoring.getIncludePackages().isEmpty()) {
            return false;
        }
        
        for (String includePattern : monitoring.getIncludePackages()) {
            if (pathMatcher.match(includePattern, packageName)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks if a class should be monitored based on Spring annotations.
     * 
     * @param targetClass the class to check
     * @return true if the class matches Spring annotation monitoring criteria
     */
    /*
    [003][M1][監控配置系統]
    input: Class<?>
    output: boolean
    基於 Spring 註解檢查是否應該監控該類
    */
    private boolean shouldMonitorBySpringAnnotation(Class<?> targetClass) {
        // Check @RestController
        if (monitoring.isMonitorRestControllers() && 
            targetClass.isAnnotationPresent(org.springframework.web.bind.annotation.RestController.class)) {
            return true;
        }
        
        // Check @Controller
        if (monitoring.isMonitorControllers() && 
            targetClass.isAnnotationPresent(org.springframework.stereotype.Controller.class)) {
            return true;
        }
        
        // Check @Service
        if (monitoring.isMonitorServices() && 
            targetClass.isAnnotationPresent(org.springframework.stereotype.Service.class)) {
            return true;
        }
        
        // Check @Repository
        if (monitoring.isMonitorRepositories() && 
            targetClass.isAnnotationPresent(org.springframework.stereotype.Repository.class)) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Gets the exclusion reason for a method or class, if specified.
     * 
     * @param method the method to check
     * @param targetClass the class to check
     * @return the exclusion reason, or null if not excluded or no reason specified
     */
    /*
    [003][M1][監控配置系統]
    input: Method, Class<?>
    output: String
    獲取排除監控的原因說明
    */
    public String getExclusionReason(Method method, Class<?> targetClass) {
        // Check method level exclusion first
        ExcludeMonitoring methodExclusion = method.getAnnotation(ExcludeMonitoring.class);
        if (methodExclusion != null && !methodExclusion.reason().isEmpty()) {
            return methodExclusion.reason();
        }
        
        // Check class level exclusion
        ExcludeMonitoring classExclusion = targetClass.getAnnotation(ExcludeMonitoring.class);
        if (classExclusion != null && !classExclusion.reason().isEmpty()) {
            return classExclusion.reason();
        }
        
        return null;
    }
    
    /**
     * Checks if parameter information should be included for a method.
     * 
     * @param method the method to check
     * @return true if parameters should be included in monitoring
     */
    /*
    [003][M1][監控配置系統]
    input: Method
    output: boolean
    檢查是否應該在監控中包含參數信息
    */
    public boolean shouldIncludeParameters(Method method) {
        IncludeMonitoring annotation = method.getAnnotation(IncludeMonitoring.class);
        if (annotation != null) {
            return annotation.includeParameters();
        }
        return monitoring.isIncludeParameters();
    }
    
    /**
     * Checks if return type information should be included for a method.
     * 
     * @param method the method to check
     * @return true if return type should be included in monitoring
     */
    /*
    [003][M1][監控配置系統]
    input: Method
    output: boolean
    檢查是否應該在監控中包含返回值類型信息
    */
    public boolean shouldIncludeReturnType(Method method) {
        IncludeMonitoring annotation = method.getAnnotation(IncludeMonitoring.class);
        return annotation != null && annotation.includeReturnType();
    }
}