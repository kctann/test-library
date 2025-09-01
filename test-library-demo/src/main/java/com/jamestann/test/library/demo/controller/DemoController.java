/*
[001][專案結構建立]
Demo Controller類別
提供REST API端點來測試Library功能
*/
package com.jamestann.test.library.demo.controller;

import com.jamestann.test.library.config.TestLibraryManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/demo")
@RequiredArgsConstructor
public class DemoController {

    private final TestLibraryManager testLibraryManager;

    @GetMapping("/health")
    public Map<String, Object> health() {
        log.info("Health check requested");
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("libraryEnabled", testLibraryManager.isEnabled());
        response.put("libraryName", testLibraryManager.getProperties().getLibraryName());
        
        return response;
    }

    @GetMapping("/library-info")
    public Map<String, Object> getLibraryInfo() {
        log.info("Library info requested");
        
        Map<String, Object> response = new HashMap<>();
        response.put("enabled", testLibraryManager.getProperties().isEnabled());
        response.put("libraryName", testLibraryManager.getProperties().getLibraryName());
        response.put("performanceMonitoring", testLibraryManager.getProperties().isPerformanceMonitoringEnabled());
        response.put("loggingStandardization", testLibraryManager.getProperties().isLoggingStandardizationEnabled());
        response.put("customActuatorEndpoints", testLibraryManager.getProperties().getActuator().isCustomEndpointsEnabled());
        
        return response;
    }

    @PostMapping("/test-performance")
    public Map<String, Object> testPerformance(@RequestParam(defaultValue = "1000") int iterations) {
        log.info("Performance test requested with {} iterations", iterations);
        
        long startTime = System.currentTimeMillis();
        
        // 模擬一些工作負載
        for (int i = 0; i < iterations; i++) {
            Math.sqrt(i);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        Map<String, Object> response = new HashMap<>();
        response.put("iterations", iterations);
        response.put("duration", duration + "ms");
        response.put("averageTimePerIteration", (duration * 1000.0 / iterations) + "μs");
        
        log.info("Performance test completed: {} iterations in {}ms", iterations, duration);
        
        return response;
    }
}