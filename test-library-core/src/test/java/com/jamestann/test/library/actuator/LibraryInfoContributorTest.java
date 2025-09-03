/*
[003][M1][Info Contributor]
Library Info Contributor測試類別
測試客戶端應用程式資訊貢獻功能
*/
package com.jamestann.test.library.actuator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.info.Info;
import org.springframework.core.env.Environment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for LibraryInfoContributor.
 * <p>
 * Tests the info contribution functionality for client application
 * information including library version and monitoring features.
 * 
 * @author James Tann
 * @since 1.0.0
 */
/*
[003][M1][Info Contributor]
LibraryInfoContributor 的單元測試
測試客戶端應用程式資訊貢獻功能
*/
@ExtendWith(MockitoExtension.class)
class LibraryInfoContributorTest {

    @Mock
    private Environment environment;
    
    private LibraryActuatorProperties properties;
    private LibraryInfoContributor infoContributor;

    /**
     * Sets up test fixtures before each test.
     */
    /*
    [003][M1][Info Contributor]
    input: void
    output: void
    在每個測試前設置測試裝置
    */
    @BeforeEach
    void setUp() {
        properties = new LibraryActuatorProperties();
        infoContributor = new LibraryInfoContributor(properties, environment);
    }

    /**
     * Tests basic info contribution when enabled.
     */
    /*
    [003][M1][Info Contributor]
    input: void
    output: void
    測試啟用時的基本資訊貢獻
    */
    @Test
    void testBasicInfoContribution() {
        properties.getInfo().setEnabled(true);
        
        when(environment.getProperty("spring.application.name", "unknown-application"))
            .thenReturn("test-app");
        when(environment.getProperty("server.port", "8080"))
            .thenReturn("9090");
        when(environment.getActiveProfiles())
            .thenReturn(new String[]{"dev", "test"});
        
        Info.Builder builder = new Info.Builder();
        infoContributor.contribute(builder);
        Info info = builder.build();
        
        assertThat(info.getDetails()).containsKey("library");
        
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> libraryInfo = 
            (java.util.Map<String, Object>) info.getDetails().get("library");
        
        assertThat(libraryInfo).containsKey("version");
        assertThat(libraryInfo).containsKey("monitoring");
        assertThat(libraryInfo).containsKey("client-application");
        
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> clientApp = 
            (java.util.Map<String, Object>) libraryInfo.get("client-application");
        
        assertThat(clientApp.get("name")).isEqualTo("test-app");
        assertThat(clientApp.get("server-port")).isEqualTo("9090");
        assertThat(clientApp.get("active-profiles")).isEqualTo("dev,test");
    }

    /**
     * Tests monitoring feature information in info contribution.
     */
    /*
    [003][M1][Info Contributor]
    input: void
    output: void
    測試資訊貢獻中的監控功能資訊
    */
    @Test
    void testMonitoringFeatureInfo() {
        properties.getInfo().setEnabled(true);
        properties.getHealth().setEnabled(true);
        properties.getMetrics().setEnabled(true);
        properties.getMetrics().setIncludePercentiles(true);
        
        Info.Builder builder = new Info.Builder();
        infoContributor.contribute(builder);
        Info info = builder.build();
        
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> libraryInfo = 
            (java.util.Map<String, Object>) info.getDetails().get("library");
        
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> monitoring = 
            (java.util.Map<String, Object>) libraryInfo.get("monitoring");
        
        assertThat(monitoring.get("health-check")).isEqualTo(true);
        assertThat(monitoring.get("metrics-collection")).isEqualTo(true);
        assertThat(monitoring.get("percentiles-enabled")).isEqualTo(true);
        assertThat(monitoring).containsKey("features-enabled");
        
        @SuppressWarnings("unchecked")
        java.util.List<String> features = (java.util.List<String>) monitoring.get("features-enabled");
        assertThat(features).contains("Health Check", "SLI Collection", "HTTP Request Monitoring");
    }

    /**
     * Tests path filters information in info contribution.
     */
    /*
    [003][M1][Info Contributor]
    input: void
    output: void
    測試資訊貢獻中的監控配置資訊
    */
    @Test
    void testMonitoringConfigInfo() {
        properties.getInfo().setEnabled(true);
        properties.getMonitoring().setIncludePackages(java.util.Arrays.asList("com.example.api.**", "com.example.service.**"));
        properties.getMonitoring().setExcludePackages(java.util.Arrays.asList("com.example.internal.**", "com.example.test.**"));
        
        Info.Builder builder = new Info.Builder();
        infoContributor.contribute(builder);
        Info info = builder.build();
        
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> libraryInfo = 
            (java.util.Map<String, Object>) info.getDetails().get("library");
        
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> monitoring = 
            (java.util.Map<String, Object>) libraryInfo.get("monitoring");
        
        assertThat(monitoring).containsKey("path-filters");
        
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> pathFilters = 
            (java.util.Map<String, Object>) monitoring.get("path-filters");
        
        assertThat(pathFilters.get("include")).isEqualTo(java.util.Arrays.asList("/api/**", "/public/**"));
        assertThat(pathFilters.get("exclude")).isEqualTo(java.util.Arrays.asList("/actuator/**", "/health"));
    }

    /**
     * Tests info contribution when disabled.
     */
    /*
    [003][M1][Info Contributor]
    input: void
    output: void
    測試禁用時的資訊貢獻
    */
    @Test
    void testInfoContributionWhenDisabled() {
        properties.getInfo().setEnabled(false);
        
        Info.Builder builder = new Info.Builder();
        infoContributor.contribute(builder);
        Info info = builder.build();
        
        assertThat(info.getDetails()).containsKey("library");
        
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> libraryInfo = 
            (java.util.Map<String, Object>) info.getDetails().get("library");
        
        assertThat(libraryInfo.get("status")).isEqualTo("disabled");
        assertThat(libraryInfo).doesNotContainKey("client-application");
        assertThat(libraryInfo).doesNotContainKey("monitoring");
    }

    /**
     * Tests client application information with default values.
     */
    /*
    [003][M1][Info Contributor]
    input: void
    output: void
    測試客戶端應用程式資訊的預設值
    */
    @Test
    void testClientApplicationInfoWithDefaults() {
        properties.getInfo().setEnabled(true);
        
        when(environment.getProperty("spring.application.name", "unknown-application"))
            .thenReturn("unknown-application");
        when(environment.getProperty("server.port", "8080"))
            .thenReturn("8080");
        when(environment.getActiveProfiles())
            .thenReturn(new String[]{});
        
        Info.Builder builder = new Info.Builder();
        infoContributor.contribute(builder);
        Info info = builder.build();
        
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> libraryInfo = 
            (java.util.Map<String, Object>) info.getDetails().get("library");
        
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> clientApp = 
            (java.util.Map<String, Object>) libraryInfo.get("client-application");
        
        assertThat(clientApp.get("name")).isEqualTo("unknown-application");
        assertThat(clientApp.get("server-port")).isEqualTo("8080");
        assertThat(clientApp.get("active-profiles")).isEqualTo("none");
    }

    /**
     * Tests library version information.
     */
    /*
    [003][M1][Info Contributor]
    input: void
    output: void
    測試函式庫版本資訊
    */
    @Test
    void testLibraryVersionInfo() {
        properties.getInfo().setEnabled(true);
        
        Info.Builder builder = new Info.Builder();
        infoContributor.contribute(builder);
        Info info = builder.build();
        
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> libraryInfo = 
            (java.util.Map<String, Object>) info.getDetails().get("library");
        
        assertThat(libraryInfo).containsKey("version");
        assertThat(libraryInfo).containsKey("description");
        
        String version = (String) libraryInfo.get("version");
        String description = (String) libraryInfo.get("description");
        
        assertThat(version).isNotBlank();
        assertThat(description).contains("client application monitoring");
    }

    /**
     * Tests monitoring status with mixed feature states.
     */
    /*
    [003][M1][Info Contributor]
    input: void
    output: void
    測試混合功能狀態的監控狀態
    */
    @Test
    void testMonitoringStatusWithMixedFeatureStates() {
        properties.getInfo().setEnabled(true);
        properties.getHealth().setEnabled(true);
        properties.getMetrics().setEnabled(false);
        
        Info.Builder builder = new Info.Builder();
        infoContributor.contribute(builder);
        Info info = builder.build();
        
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> libraryInfo = 
            (java.util.Map<String, Object>) info.getDetails().get("library");
        
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> monitoring = 
            (java.util.Map<String, Object>) libraryInfo.get("monitoring");
        
        assertThat(monitoring.get("health-check")).isEqualTo(true);
        assertThat(monitoring.get("metrics-collection")).isEqualTo(false);
        
        @SuppressWarnings("unchecked")
        java.util.List<String> features = (java.util.List<String>) monitoring.get("features-enabled");
        assertThat(features).contains("Health Check");
        assertThat(features).doesNotContain("SLI Collection", "HTTP Request Monitoring");
    }

    /**
     * Tests exception handling in info contribution.
     */
    /*
    [003][M1][Info Contributor]
    input: void
    output: void
    測試資訊貢獻中的異常處理
    */
    @Test
    void testExceptionHandlingInInfoContribution() {
        properties.getInfo().setEnabled(true);
        
        when(environment.getProperty("spring.application.name", "unknown-application"))
            .thenThrow(new RuntimeException("Environment error"));
        
        Info.Builder builder = new Info.Builder();
        
        // Should not throw exception even if environment access fails
        infoContributor.contribute(builder);
        Info info = builder.build();
        
        assertThat(info.getDetails()).containsKey("library");
        
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> libraryInfo = 
            (java.util.Map<String, Object>) info.getDetails().get("library");
        
        // Should still have basic info
        assertThat(libraryInfo).containsKey("version");
    }
}