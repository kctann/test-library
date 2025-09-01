/*
[001][專案結構建立]
Test Library配置屬性類別
管理Library的配置參數
*/
package com.jamestann.test.library.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "test.library")
public class TestLibraryProperties {

    /**
     * 是否啟用Test Library功能
     */
    private boolean enabled = true;

    /**
     * Library名稱，用於AOP效能監控標識
     */
    private String libraryName = "test-library";

    /**
     * 是否啟用效能監控
     */
    private boolean performanceMonitoringEnabled = true;

    /**
     * 是否啟用Log標準化
     */
    private boolean loggingStandardizationEnabled = true;

    /**
     * Actuator端點配置
     */
    private ActuatorConfig actuator = new ActuatorConfig();

    @Data
    public static class ActuatorConfig {
        /**
         * 是否啟用自訂Actuator端點
         */
        private boolean customEndpointsEnabled = true;

        /**
         * 自訂端點路徑前綴
         */
        private String endpointPathPrefix = "test-library";
    }
}