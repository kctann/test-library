package com.jamestann.test.library.actuator;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration properties for Library Actuator features.
 * 
 * @author James Tann
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "test-library.actuator")
public class LibraryActuatorProperties {

    /**
     * Whether to enable Library Actuator features.
     */
    private boolean enabled = true;

    /**
     * Health check configuration.
     */
    @NestedConfigurationProperty
    private Health health = new Health();

    /**
     * Info contributor configuration.
     */
    @NestedConfigurationProperty
    private Info info = new Info();

    /**
     * SLI metrics collection configuration.
     */
    @NestedConfigurationProperty
    private Metrics metrics = new Metrics();

    /**
     * Package-based monitoring configuration.
     */
    @NestedConfigurationProperty
    private Monitoring monitoring = new Monitoring();

    // Getters and setters
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Health getHealth() {
        return health;
    }

    public void setHealth(Health health) {
        this.health = health;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public void setMetrics(Metrics metrics) {
        this.metrics = metrics;
    }

    public Monitoring getMonitoring() {
        return monitoring;
    }

    public void setMonitoring(Monitoring monitoring) {
        this.monitoring = monitoring;
    }

    /**
     * Health check configuration properties.
     */
    public static class Health {
        /**
         * Whether to enable Library health indicator.
         */
        private boolean enabled = true;

        /**
         * Whether to include detailed component status.
         */
        private boolean showDetails = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isShowDetails() {
            return showDetails;
        }

        public void setShowDetails(boolean showDetails) {
            this.showDetails = showDetails;
        }
    }

    /**
     * Info contributor configuration properties.
     */
    public static class Info {
        /**
         * Whether to enable Library info contributor.
         */
        private boolean enabled = true;

        /**
         * Whether to include Git information.
         */
        private boolean includeGit = false;

        /**
         * Whether to include build information.
         */
        private boolean includeBuild = true;

        /**
         * Whether to include feature flags.
         */
        private boolean includeFeatures = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isIncludeGit() {
            return includeGit;
        }

        public void setIncludeGit(boolean includeGit) {
            this.includeGit = includeGit;
        }

        public boolean isIncludeBuild() {
            return includeBuild;
        }

        public void setIncludeBuild(boolean includeBuild) {
            this.includeBuild = includeBuild;
        }

        public boolean isIncludeFeatures() {
            return includeFeatures;
        }

        public void setIncludeFeatures(boolean includeFeatures) {
            this.includeFeatures = includeFeatures;
        }
    }

    /**
     * SLI metrics collection configuration properties.
     */
    public static class Metrics {
        /**
         * Whether to enable SLI metrics collection.
         */
        private boolean enabled = true;

        /**
         * Whether to collect detailed percentiles.
         */
        private boolean includePercentiles = true;

        /**
         * Percentiles to collect for timing metrics.
         */
        private double[] percentiles = {0.50, 0.90, 0.95, 0.99};

        /**
         * Maximum number of tags per metric.
         */
        private int maxTags = 10;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isIncludePercentiles() {
            return includePercentiles;
        }

        public void setIncludePercentiles(boolean includePercentiles) {
            this.includePercentiles = includePercentiles;
        }

        public double[] getPercentiles() {
            return percentiles;
        }

        public void setPercentiles(double[] percentiles) {
            this.percentiles = percentiles;
        }

        public int getMaxTags() {
            return maxTags;
        }

        public void setMaxTags(int maxTags) {
            this.maxTags = maxTags;
        }
    }

    /**
     * Package-based monitoring configuration properties.
     */
    /*
    [003][M1][監控配置系統]
    基於 Package 的監控配置，替代路徑過濾方式
    提供更精確和業務導向的監控控制
    */
    public static class Monitoring {
        /**
         * Whether to enable package-based monitoring.
         */
        private boolean enabled = true;

        /**
         * Packages to include in monitoring.
         * Methods in these packages will be monitored by default,
         * unless explicitly excluded with @ExcludeMonitoring.
         */
        private List<String> includePackages = new ArrayList<>();

        /**
         * Packages to exclude from monitoring.
         * Methods in these packages will not be monitored,
         * even if they have @IncludeMonitoring annotation.
         */
        private List<String> excludePackages = new ArrayList<>();

        /**
         * Whether to monitor all @RestController methods by default.
         */
        private boolean monitorRestControllers = false;

        /**
         * Whether to monitor all @Controller methods by default.
         */
        private boolean monitorControllers = false;

        /**
         * Whether to monitor all @Service methods by default.
         */
        private boolean monitorServices = false;

        /**
         * Whether to monitor all @Repository methods by default.
         */
        private boolean monitorRepositories = false;

        /**
         * Maximum method execution time (in milliseconds) to record.
         * Methods taking longer will be capped at this value for metrics.
         */
        private long maxExecutionTime = 60000; // 60 seconds

        /**
         * Whether to include method parameters in monitoring.
         * Use with caution to avoid high cardinality or sensitive data exposure.
         */
        private boolean includeParameters = false;

        public Monitoring() {
            // Default excluded packages - framework internals
            excludePackages.add("org.springframework.**");
            excludePackages.add("com.fasterxml.jackson.**");
            excludePackages.add("org.slf4j.**");
            excludePackages.add("ch.qos.logback.**");
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public List<String> getIncludePackages() {
            return includePackages;
        }

        public void setIncludePackages(List<String> includePackages) {
            this.includePackages = includePackages;
        }

        public List<String> getExcludePackages() {
            return excludePackages;
        }

        public void setExcludePackages(List<String> excludePackages) {
            this.excludePackages = excludePackages;
        }

        public boolean isMonitorRestControllers() {
            return monitorRestControllers;
        }

        public void setMonitorRestControllers(boolean monitorRestControllers) {
            this.monitorRestControllers = monitorRestControllers;
        }

        public boolean isMonitorControllers() {
            return monitorControllers;
        }

        public void setMonitorControllers(boolean monitorControllers) {
            this.monitorControllers = monitorControllers;
        }

        public boolean isMonitorServices() {
            return monitorServices;
        }

        public void setMonitorServices(boolean monitorServices) {
            this.monitorServices = monitorServices;
        }

        public boolean isMonitorRepositories() {
            return monitorRepositories;
        }

        public void setMonitorRepositories(boolean monitorRepositories) {
            this.monitorRepositories = monitorRepositories;
        }

        public long getMaxExecutionTime() {
            return maxExecutionTime;
        }

        public void setMaxExecutionTime(long maxExecutionTime) {
            this.maxExecutionTime = maxExecutionTime;
        }

        public boolean isIncludeParameters() {
            return includeParameters;
        }

        public void setIncludeParameters(boolean includeParameters) {
            this.includeParameters = includeParameters;
        }
    }
}