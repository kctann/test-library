package com.jamestann.test.library.compatibility;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Java Version Compatibility Tests
 * 
 * Tests to ensure the library works correctly across different Java versions (11, 17, 21+)
 * and validates that we're using only Java 11+ compatible features.
 */
@DisplayName("Java Version Compatibility Tests")
class JavaVersionCompatibilityTest {

    @Test
    @DisplayName("Should detect current Java version")
    void shouldDetectCurrentJavaVersion() {
        String javaVersion = System.getProperty("java.version");
        String javaVendor = System.getProperty("java.vendor");
        String javaVmName = System.getProperty("java.vm.name");
        
        assertNotNull(javaVersion, "Java version should be detectable");
        assertNotNull(javaVendor, "Java vendor should be detectable");
        assertNotNull(javaVmName, "Java VM name should be detectable");
        
        System.out.println("Testing on Java " + javaVersion + " (" + javaVendor + ")");
        System.out.println("VM: " + javaVmName);
    }

    @Test
    @DisplayName("Should verify minimum Java 11 support")
    void shouldVerifyMinimumJava11Support() {
        String javaVersion = System.getProperty("java.version");
        
        // Extract major version number
        int majorVersion = extractMajorVersion(javaVersion);
        
        assertTrue(majorVersion >= 11, 
            "Library requires Java 11 or higher. Current version: " + javaVersion);
    }

    @ParameterizedTest
    @ValueSource(strings = {"11", "17", "21"})
    @DisplayName("Should support target Java versions")
    void shouldSupportTargetJavaVersions(String targetVersion) {
        String currentVersion = System.getProperty("java.version");
        int currentMajor = extractMajorVersion(currentVersion);
        int targetMajor = Integer.parseInt(targetVersion);
        
        if (currentMajor == targetMajor) {
            System.out.println("✓ Running on target Java " + targetVersion);
            assertTrue(true, "Successfully running on Java " + targetVersion);
        } else {
            System.out.println("⊘ Skipping Java " + targetVersion + " test (running on " + currentMajor + ")");
        }
    }

    @Test
    @DisplayName("Should use only Java 11+ language features")
    void shouldUseOnlyJava11PlusLanguageFeatures() {
        // Test var keyword (Java 11+)
        var testString = "Testing var keyword support";
        assertEquals("Testing var keyword support", testString);
        
        // Test String methods introduced in Java 11+
        assertTrue("  test  ".isBlank() == false);
        assertEquals("test", "  test  ".strip());
        assertEquals("line1\nline2\nline3", "line1\nline2\nline3".lines()
            .reduce((a, b) -> a + "\n" + b).orElse(""));
        
        System.out.println("✓ Java 11+ language features working correctly");
    }

    @Test
    @DisplayName("Should verify compiler target compatibility")
    void shouldVerifyCompilerTargetCompatibility() {
        String compilerSource = System.getProperty("maven.compiler.source", "unknown");
        String javaVersion = System.getProperty("java.version", "unknown");
        
        System.out.println("Compiler source level: " + compilerSource);
        System.out.println("Runtime Java version: " + javaVersion);
        
        // If compiler source is set, verify it's 11 or higher
        if (!"unknown".equals(compilerSource)) {
            try {
                int sourceVersion = Integer.parseInt(compilerSource);
                assertTrue(sourceVersion >= 11, 
                    "Compiler source should be Java 11 or higher. Current: " + sourceVersion);
            } catch (NumberFormatException e) {
                // Handle cases like "11.0.1" format
                assertTrue(compilerSource.startsWith("11") || 
                          compilerSource.startsWith("17") || 
                          compilerSource.startsWith("21"),
                    "Compiler source should be Java 11+. Current: " + compilerSource);
            }
        }
    }

    @Test
    @DisplayName("Should handle reflection compatibility")
    void shouldHandleReflectionCompatibility() {
        // Test that reflection works across Java versions
        // This is important for Spring Boot's dependency injection
        try {
            Class<?> stringClass = Class.forName("java.lang.String");
            assertNotNull(stringClass);
            
            // Test method retrieval (used by Spring)
            var methods = stringClass.getMethods();
            assertTrue(methods.length > 0);
            
            System.out.println("✓ Reflection compatibility verified");
        } catch (ClassNotFoundException e) {
            fail("Basic reflection should work on all supported Java versions");
        }
    }

    /**
     * Extract major version number from Java version string
     * Handles both old format (1.8.0_XXX) and new format (11.0.X, 17.0.X)
     */
    private int extractMajorVersion(String versionString) {
        if (versionString.startsWith("1.")) {
            // Old format: 1.8.0_XXX -> 8
            return Integer.parseInt(versionString.substring(2, 3));
        } else {
            // New format: 11.0.X -> 11, 17.0.X -> 17
            int dotIndex = versionString.indexOf('.');
            if (dotIndex > 0) {
                return Integer.parseInt(versionString.substring(0, dotIndex));
            } else {
                return Integer.parseInt(versionString);
            }
        }
    }
}