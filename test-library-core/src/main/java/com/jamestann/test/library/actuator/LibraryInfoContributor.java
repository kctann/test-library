package com.jamestann.test.library.actuator;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.core.env.Environment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Info contributor for client application enhanced by Test Library.
 * <p>
 * Contributes client application information to the actuator info endpoint
 * including application details, environment configuration, and monitoring status.
 * 
 * @author James Tann
 * @since 1.0.0
 */
/*
[003][M1][Info Contributor]
客戶端應用程式的資訊貢獻者，由 Test Library 增強
提供客戶端應用程式的詳細資訊、環境配置和監控狀態到 /actuator/info
*/
public class LibraryInfoContributor implements InfoContributor {

    private final LibraryActuatorProperties properties;
    private final Environment environment;

    /**
     * Creates a new LibraryInfoContributor.
     * 
     * @param properties the Library actuator properties
     * @param environment the Spring Environment for accessing application properties
     */
    public LibraryInfoContributor(LibraryActuatorProperties properties, Environment environment) {
        this.properties = properties;
        this.environment = environment;
    }

    /**
     * Contributes client application information to the info endpoint.
     * <p>
     * Adds comprehensive client application information including version details,
     * build information, enabled features, and environment configuration.
     * 
     * @param builder the info builder to contribute to
     */
    /*
    [003][M1][Info Contributor]
    input: Info.Builder
    output: void (modifies builder)
    向 Info 端點貢獻客戶端應用程式資訊，包含版本、功能和環境配置
    */
    @Override
    public void contribute(Info.Builder builder) {
        Map<String, Object> applicationInfo = new HashMap<>();
        
        // Add basic application information
        applicationInfo.put("name", getApplicationName());
        applicationInfo.put("description", getApplicationDescription());
        applicationInfo.put("version", getApplicationVersion());
        applicationInfo.put("profiles", getActiveProfiles());
        
        // Add build information if enabled
        if (properties.getInfo().isIncludeBuild()) {
            applicationInfo.put("build", createBuildInfo());
        }
        
        // Add Git information if enabled
        if (properties.getInfo().isIncludeGit()) {
            applicationInfo.put("git", createGitInfo());
        }
        
        // Add monitoring features if enabled
        if (properties.getInfo().isIncludeFeatures()) {
            applicationInfo.put("monitoring", createMonitoringInfo());
        }
        
        // Add environment information
        applicationInfo.put("environment", createEnvironmentInfo());
        
        builder.withDetail("application", applicationInfo);
    }

    /**
     * Creates build information for the client application.
     * 
     * @return Build information map
     */
    /*
    [003][M1][Info Contributor]
    input: void
    output: Map<String, Object>
    創建客戶端應用程式的建置資訊
    */
    private Map<String, Object> createBuildInfo() {
        Map<String, Object> buildInfo = new HashMap<>();
        
        // Get build information from application properties or manifest
        buildInfo.put("version", getApplicationVersion());
        buildInfo.put("timestamp", getBuildTimestamp());
        buildInfo.put("javaVersion", System.getProperty("java.version"));
        buildInfo.put("springBootVersion", getSpringBootVersion());
        
        // Add Maven/Gradle information if available
        String buildTool = getBuildTool();
        if (buildTool != null) {
            buildInfo.put("buildTool", buildTool);
        }
        
        return buildInfo;
    }

    /**
     * Creates Git information for the client application.
     * 
     * @return Git information map
     */
    /*
    [003][M1][Info Contributor]
    input: void
    output: Map<String, Object>
    創建客戶端應用程式的 Git 資訊
    */
    private Map<String, Object> createGitInfo() {
        Map<String, Object> gitInfo = new HashMap<>();
        
        // Try to read from standard git.properties file
        gitInfo.put("branch", environment.getProperty("git.branch", "unknown"));
        gitInfo.put("commit", Map.of(
            "id", environment.getProperty("git.commit.id", "unknown"),
            "time", environment.getProperty("git.commit.time", "unknown"),
            "message", environment.getProperty("git.commit.message.short", "unknown")
        ));
        
        // Additional Git information
        gitInfo.put("tags", environment.getProperty("git.tags", ""));
        gitInfo.put("dirty", environment.getProperty("git.dirty", "unknown"));
        
        return gitInfo;
    }

    /**
     * Creates monitoring information provided by Test Library.
     * 
     * @return Monitoring information map
     */
    /*
    [003][M1][Info Contributor]
    input: void
    output: Map<String, Object>
    創建由 Test Library 提供的監控資訊
    */
    private Map<String, Object> createMonitoringInfo() {
        Map<String, Object> monitoring = new HashMap<>();
        
        // Library information
        monitoring.put("testLibrary", Map.of(
            "version", getLibraryVersion(),
            "enabled", properties.isEnabled(),
            "description", "Spring Boot Actuator monitoring and AOP functionality"
        ));
        
        // Enabled monitoring features
        List<String> enabledFeatures = new ArrayList<>();
        List<String> availableFeatures = new ArrayList<>();
        
        if (properties.getHealth().isEnabled()) {
            enabledFeatures.add("enhanced-health-check");
        }
        availableFeatures.add("enhanced-health-check");
        
        if (properties.getInfo().isEnabled()) {
            enabledFeatures.add("enhanced-info");
        }
        availableFeatures.add("enhanced-info");
        
        if (properties.getMetrics().isEnabled()) {
            enabledFeatures.add("sli-collection");
            enabledFeatures.add("golden-signals");
        }
        availableFeatures.add("sli-collection");
        availableFeatures.add("golden-signals");
        
        if (properties.getMetrics().isIncludePercentiles()) {
            enabledFeatures.add("percentiles-monitoring");
        }
        availableFeatures.add("percentiles-monitoring");
        
        monitoring.put("features", Map.of(
            "enabled", enabledFeatures,
            "available", availableFeatures
        ));
        
        return monitoring;
    }

    /**
     * Creates environment information.
     * 
     * @return Environment information map
     */
    /*
    [003][M1][Info Contributor]
    input: void
    output: Map<String, Object>
    創建環境資訊
    */
    private Map<String, Object> createEnvironmentInfo() {
        Map<String, Object> env = new HashMap<>();
        
        env.put("jvm", Map.of(
            "name", System.getProperty("java.vm.name"),
            "vendor", System.getProperty("java.vm.vendor"),
            "version", System.getProperty("java.version")
        ));
        
        env.put("os", Map.of(
            "name", System.getProperty("os.name"),
            "version", System.getProperty("os.version"),
            "arch", System.getProperty("os.arch")
        ));
        
        env.put("timezone", System.getProperty("user.timezone"));
        env.put("encoding", System.getProperty("file.encoding"));
        
        return env;
    }

    /**
     * Gets application name from Spring configuration.
     * 
     * @return Application name
     */
    /*
    [003][M1][Info Contributor]
    input: void
    output: String
    從 Spring 配置獲取應用程式名稱
    */
    private String getApplicationName() {
        return environment.getProperty("spring.application.name", "client-application");
    }

    /**
     * Gets application description from Spring configuration.
     * 
     * @return Application description
     */
    /*
    [003][M1][Info Contributor]
    input: void
    output: String
    從 Spring 配置獲取應用程式描述
    */
    private String getApplicationDescription() {
        return environment.getProperty("info.app.description", 
                "Client application enhanced with Test Library monitoring");
    }

    /**
     * Gets application version from Spring configuration.
     * 
     * @return Application version
     */
    /*
    [003][M1][Info Contributor]
    input: void
    output: String
    從 Spring 配置獲取應用程式版本
    */
    private String getApplicationVersion() {
        return environment.getProperty("info.app.version", 
                environment.getProperty("spring.application.version", "unknown"));
    }

    /**
     * Gets active Spring profiles.
     * 
     * @return Array of active profiles
     */
    /*
    [003][M1][Info Contributor]
    input: void
    output: String[]
    獲取活動的 Spring profiles
    */
    private String[] getActiveProfiles() {
        return environment.getActiveProfiles();
    }

    /**
     * Gets build timestamp from properties.
     * 
     * @return Build timestamp string
     */
    /*
    [003][M1][Info Contributor]
    input: void
    output: String
    從屬性獲取建置時間戳
    */
    private String getBuildTimestamp() {
        return environment.getProperty("info.build.timestamp", 
                environment.getProperty("build.time", LocalDateTime.now().toString()));
    }

    /**
     * Gets build tool information.
     * 
     * @return Build tool name or null
     */
    /*
    [003][M1][Info Contributor]
    input: void
    output: String
    獲取建置工具資訊
    */
    private String getBuildTool() {
        if (environment.getProperty("info.build.artifact") != null) {
            return "maven";
        }
        if (environment.getProperty("info.build.gradle.version") != null) {
            return "gradle";
        }
        return null;
    }

    /**
     * Gets Spring Boot version from classpath.
     * 
     * @return Spring Boot version string
     */
    /*
    [003][M1][Info Contributor]
    input: void
    output: String
    從 classpath 獲取 Spring Boot 版本
    */
    private String getSpringBootVersion() {
        try {
            Package springBootPackage = Package.getPackage("org.springframework.boot");
            if (springBootPackage != null) {
                String version = springBootPackage.getImplementationVersion();
                return version != null ? version : "unknown";
            }
        } catch (Exception e) {
            // Ignore exceptions
        }
        return "unknown";
    }

    /**
     * Gets Test Library version from manifest.
     * 
     * @return Library version string
     */
    /*
    [003][M1][Info Contributor]
    input: void
    output: String
    從 manifest 獲取 Test Library 版本
    */
    private String getLibraryVersion() {
        Package pkg = this.getClass().getPackage();
        String version = pkg != null ? pkg.getImplementationVersion() : null;
        return version != null ? version : "1.0.0-SNAPSHOT";
    }
}