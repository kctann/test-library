/*
[002][依賴調整]SpringBootVersionDetector
功能說明: 檢測和驗證Spring Boot版本相容性的工具類
Input: 無 (透過Spring Boot API檢測)
Output: 版本資訊、相容性檢查結果
*/
package com.jamestann.test.library.util;

import org.springframework.boot.SpringBootVersion;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Boot版本檢測工具類
 * 提供版本檢測、相容性驗證和支援版本資訊查詢功能
 */
@Component
public class SpringBootVersionDetector {

    /**
     * 支援的Spring Boot版本清單
     */
    private static final List<String> SUPPORTED_VERSION_PREFIXES = Arrays.asList(
            "2.7", "3.0", "3.1", "3.2"
    );

    /**
     * 檢測當前Spring Boot版本
     *
     * @return 當前Spring Boot版本字串
     */
    public String detectSpringBootVersion() {
        return SpringBootVersion.getVersion();
    }

    /**
     * 驗證版本是否在支援範圍內
     *
     * @param version 要檢查的版本字串
     * @return true如果版本受支援，false否則
     */
    public boolean isCompatibleVersion(String version) {
        if (version == null || version.trim().isEmpty()) {
            return false;
        }

        return SUPPORTED_VERSION_PREFIXES.stream()
                .anyMatch(supportedPrefix -> version.startsWith(supportedPrefix));
    }

    /**
     * 檢查當前運行環境的Spring Boot版本是否相容
     *
     * @return true如果當前版本受支援，false否則
     */
    public boolean isCurrentVersionCompatible() {
        String currentVersion = detectSpringBootVersion();
        return isCompatibleVersion(currentVersion);
    }

    /**
     * 回傳支援的版本清單
     *
     * @return 支援的版本前綴清單
     */
    public List<String> getSupportedVersions() {
        return List.copyOf(SUPPORTED_VERSION_PREFIXES);
    }

    /**
     * 取得詳細的版本相容性資訊
     *
     * @return 版本相容性報告
     */
    public VersionCompatibilityReport getCompatibilityReport() {
        String currentVersion = detectSpringBootVersion();
        boolean isCompatible = isCompatibleVersion(currentVersion);
        
        return new VersionCompatibilityReport(
                currentVersion,
                isCompatible,
                SUPPORTED_VERSION_PREFIXES,
                generateCompatibilityMessage(currentVersion, isCompatible)
        );
    }

    /**
     * 產生相容性訊息
     */
    private String generateCompatibilityMessage(String currentVersion, boolean isCompatible) {
        if (isCompatible) {
            return String.format("Spring Boot %s is supported by test-library", currentVersion);
        } else {
            return String.format("Spring Boot %s is NOT supported. Supported versions: %s", 
                    currentVersion, String.join(", ", SUPPORTED_VERSION_PREFIXES));
        }
    }

    /**
     * 版本相容性報告類別
     */
    public static class VersionCompatibilityReport {
        private final String currentVersion;
        private final boolean isCompatible;
        private final List<String> supportedVersions;
        private final String message;

        public VersionCompatibilityReport(String currentVersion, boolean isCompatible, 
                List<String> supportedVersions, String message) {
            this.currentVersion = currentVersion;
            this.isCompatible = isCompatible;
            this.supportedVersions = List.copyOf(supportedVersions);
            this.message = message;
        }

        public String getCurrentVersion() {
            return currentVersion;
        }

        public boolean isCompatible() {
            return isCompatible;
        }

        public List<String> getSupportedVersions() {
            return supportedVersions;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public String toString() {
            return String.format("VersionCompatibilityReport{currentVersion='%s', isCompatible=%s, message='%s'}", 
                    currentVersion, isCompatible, message);
        }
    }
}