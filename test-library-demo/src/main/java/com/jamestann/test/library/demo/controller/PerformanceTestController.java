/*
[003][M1][Demo Controller]
性能測試Controller
測試不同類型的性能負載和監控數據收集
*/
package com.jamestann.test.library.demo.controller;

import com.jamestann.test.library.actuator.annotation.IncludeMonitoring;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 * 性能測試Controller - 測試不同類型的性能負載
 * <p>
 * 提供多種性能測試端點來驗證：
 * - CPU密集型操作監控
 * - Memory密集型操作監控
 * - I/O密集型操作監控
 * - 併發操作監控
 * - 資源使用情況監控
 * 
 * @author James Tann
 * @since 1.0.0
 */
/*
[003][M1][Demo Controller]
性能測試控制器，提供各種性能負載測試端點
驗證不同類型負載下的監控數據收集
*/
@Slf4j
@RestController
@RequestMapping("/api/performance")
public class PerformanceTestController {

    private final Random random = new Random();

    /**
     * CPU密集型操作測試
     */
    /*
    [003][M1][Demo Controller]
    input: int complexity
    output: Map<String, Object>
    CPU密集型操作測試，測試高CPU使用率下的監控
    */
    @GetMapping("/cpu-intensive")
    @IncludeMonitoring(
        name = "performance.cpu.intensive",
        description = "CPU intensive operation test",
        tags = {"type=cpu", "resource=high", "category=performance"}
    )
    public Map<String, Object> cpuIntensiveTest(@RequestParam(defaultValue = "1000") int complexity) {
        log.info("CPU intensive test - complexity: {}", complexity);
        
        long startTime = System.nanoTime();
        
        // CPU密集型計算：質數計算
        List<Integer> primes = findPrimes(complexity);
        
        // 數學運算密集型操作
        double result = performMathCalculations(complexity);
        
        long endTime = System.nanoTime();
        double executionTimeMs = (endTime - startTime) / 1_000_000.0;
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "completed");
        response.put("operation", "cpu_intensive");
        response.put("complexity", complexity);
        response.put("primesFound", primes.size());
        response.put("mathResult", result);
        response.put("executionTimeMs", executionTimeMs);
        response.put("timestamp", LocalDateTime.now());
        
        return response;
    }

    /**
     * Memory密集型操作測試
     */
    /*
    [003][M1][Demo Controller]
    input: int sizeMB
    output: Map<String, Object>
    Memory密集型操作測試，測試高記憶體使用下的監控
    */
    @GetMapping("/memory-intensive")
    @IncludeMonitoring(
        name = "performance.memory.intensive",
        description = "Memory intensive operation test",
        tags = {"type=memory", "resource=high", "category=performance"}
    )
    public Map<String, Object> memoryIntensiveTest(@RequestParam(defaultValue = "10") int sizeMB) {
        log.info("Memory intensive test - size: {}MB", sizeMB);
        
        long startTime = System.currentTimeMillis();
        Runtime runtime = Runtime.getRuntime();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
        
        // 記憶體密集型操作：創建大型數據結構
        List<byte[]> memoryBlocks = new ArrayList<>();
        List<Map<String, Object>> dataStructures = new ArrayList<>();
        
        try {
            // 創建指定大小的記憶體塊
            for (int i = 0; i < sizeMB; i++) {
                byte[] block = new byte[1024 * 1024]; // 1MB block
                Arrays.fill(block, (byte) (i % 256));
                memoryBlocks.add(block);
                
                // 創建複雜數據結構
                Map<String, Object> complexData = createComplexDataStructure(1000);
                dataStructures.add(complexData);
            }
            
            // 模擬數據處理
            processMemoryData(memoryBlocks, dataStructures);
            
        } finally {
            // 清理大型對象（促進GC）
            memoryBlocks.clear();
            dataStructures.clear();
            System.gc(); // 建議垃圾回收
        }
        
        long endTime = System.currentTimeMillis();
        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "completed");
        response.put("operation", "memory_intensive");
        response.put("requestedSizeMB", sizeMB);
        response.put("executionTimeMs", endTime - startTime);
        response.put("memoryUsedMB", (memoryAfter - memoryBefore) / (1024.0 * 1024.0));
        response.put("timestamp", LocalDateTime.now());
        
        return response;
    }

    /**
     * I/O密集型操作測試
     */
    /*
    [003][M1][Demo Controller]
    input: int operations
    output: Map<String, Object>
    I/O密集型操作測試，模擬高I/O負載下的監控
    */
    @GetMapping("/io-intensive")
    @IncludeMonitoring(
        name = "performance.io.intensive",
        description = "I/O intensive operation test",
        tags = {"type=io", "resource=high", "category=performance"}
    )
    public Map<String, Object> ioIntensiveTest(@RequestParam(defaultValue = "100") int operations) {
        log.info("I/O intensive test - operations: {}", operations);
        
        long startTime = System.currentTimeMillis();
        
        // 模擬I/O密集型操作
        List<String> ioResults = new ArrayList<>();
        
        for (int i = 0; i < operations; i++) {
            // 模擬文件I/O操作
            String data = simulateFileIO(i);
            ioResults.add(data);
            
            // 模擬網絡I/O延遲
            simulateNetworkIO();
            
            // 模擬數據庫I/O操作
            simulateDatabaseIO(i);
        }
        
        long endTime = System.currentTimeMillis();
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "completed");
        response.put("operation", "io_intensive");
        response.put("operations", operations);
        response.put("executionTimeMs", endTime - startTime);
        response.put("averageTimePerOperation", (double)(endTime - startTime) / operations);
        response.put("ioResultsCount", ioResults.size());
        response.put("timestamp", LocalDateTime.now());
        
        return response;
    }

    /**
     * 併發操作測試
     */
    /*
    [003][M1][Demo Controller]
    input: int threads
    output: Map<String, Object>
    併發操作測試，測試多線程環境下的監控
    */
    @GetMapping("/concurrent")
    @IncludeMonitoring(
        name = "performance.concurrent.test",
        description = "Concurrent operations test",
        tags = {"type=concurrent", "resource=multi", "category=performance"}
    )
    public Map<String, Object> concurrentTest(@RequestParam(defaultValue = "5") int threads) {
        log.info("Concurrent test - threads: {}", threads);
        
        long startTime = System.currentTimeMillis();
        
        // 創建並發任務
        List<CompletableFuture<String>> futures = new ArrayList<>();
        
        for (int i = 0; i < threads; i++) {
            final int taskId = i;
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                return performConcurrentTask(taskId);
            });
            futures.add(future);
        }
        
        // 等待所有任務完成
        List<String> results = futures.stream()
            .map(CompletableFuture::join)
            .toList();
        
        long endTime = System.currentTimeMillis();
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "completed");
        response.put("operation", "concurrent_test");
        response.put("threadsUsed", threads);
        response.put("executionTimeMs", endTime - startTime);
        response.put("tasksCompleted", results.size());
        response.put("results", results);
        response.put("timestamp", LocalDateTime.now());
        
        return response;
    }

    /**
     * 混合負載測試
     */
    /*
    [003][M1][Demo Controller]
    input: void
    output: Map<String, Object>
    混合負載測試，同時測試CPU、Memory、I/O
    */
    @GetMapping("/mixed-load")
    @IncludeMonitoring(
        name = "performance.mixed.load",
        description = "Mixed workload performance test",
        tags = {"type=mixed", "resource=all", "category=performance", "complexity=high"}
    )
    public Map<String, Object> mixedLoadTest() {
        log.info("Mixed load test - combining CPU, Memory, and I/O operations");
        
        long startTime = System.currentTimeMillis();
        Map<String, Object> results = new HashMap<>();
        
        // 並行執行不同類型的負載
        CompletableFuture<Void> cpuTask = CompletableFuture.runAsync(() -> {
            List<Integer> primes = findPrimes(500);
            results.put("cpuTask", "completed - found " + primes.size() + " primes");
        });
        
        CompletableFuture<Void> memoryTask = CompletableFuture.runAsync(() -> {
            List<Map<String, Object>> data = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                data.add(createComplexDataStructure(100));
            }
            results.put("memoryTask", "completed - created " + data.size() + " structures");
        });
        
        CompletableFuture<Void> ioTask = CompletableFuture.runAsync(() -> {
            List<String> ioResults = new ArrayList<>();
            for (int i = 0; i < 50; i++) {
                ioResults.add(simulateFileIO(i));
                simulateNetworkIO();
            }
            results.put("ioTask", "completed - " + ioResults.size() + " operations");
        });
        
        // 等待所有任務完成
        CompletableFuture.allOf(cpuTask, memoryTask, ioTask).join();
        
        long endTime = System.currentTimeMillis();
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "completed");
        response.put("operation", "mixed_load");
        response.put("executionTimeMs", endTime - startTime);
        response.put("taskResults", results);
        response.put("timestamp", LocalDateTime.now());
        
        return response;
    }

    /**
     * 資源監控端點
     */
    /*
    [003][M1][Demo Controller]
    input: void
    output: Map<String, Object>
    獲取當前系統資源使用情況
    */
    @GetMapping("/resources")
    @IncludeMonitoring(
        name = "system.resources.check",
        description = "System resources monitoring",
        tags = {"type=system", "category=monitoring"}
    )
    public Map<String, Object> getResourceUsage() {
        Runtime runtime = Runtime.getRuntime();
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        
        // 記憶體資訊
        Map<String, Object> memory = new HashMap<>();
        memory.put("totalMemoryMB", runtime.totalMemory() / (1024.0 * 1024.0));
        memory.put("freeMemoryMB", runtime.freeMemory() / (1024.0 * 1024.0));
        memory.put("usedMemoryMB", (runtime.totalMemory() - runtime.freeMemory()) / (1024.0 * 1024.0));
        memory.put("maxMemoryMB", runtime.maxMemory() / (1024.0 * 1024.0));
        response.put("memory", memory);
        
        // CPU資訊
        Map<String, Object> cpu = new HashMap<>();
        cpu.put("availableProcessors", runtime.availableProcessors());
        response.put("cpu", cpu);
        
        // JVM資訊
        Map<String, Object> jvm = new HashMap<>();
        jvm.put("javaVersion", System.getProperty("java.version"));
        jvm.put("javaVendor", System.getProperty("java.vendor"));
        response.put("jvm", jvm);
        
        return response;
    }

    // === 工具方法 ===

    /**
     * 尋找質數
     */
    private List<Integer> findPrimes(int limit) {
        List<Integer> primes = new ArrayList<>();
        for (int i = 2; i <= limit; i++) {
            if (isPrime(i)) {
                primes.add(i);
            }
        }
        return primes;
    }

    /**
     * 檢查是否為質數
     */
    private boolean isPrime(int n) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;
        
        for (int i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 執行數學計算
     */
    private double performMathCalculations(int iterations) {
        double result = 0.0;
        for (int i = 0; i < iterations; i++) {
            result += Math.sqrt(i * Math.PI);
            result += Math.sin(i / 100.0);
            result += Math.log(i + 1);
        }
        return result;
    }

    /**
     * 創建複雜數據結構
     */
    private Map<String, Object> createComplexDataStructure(int size) {
        Map<String, Object> data = new HashMap<>();
        List<Map<String, Object>> items = new ArrayList<>();
        
        for (int i = 0; i < size; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", i);
            item.put("name", "Item_" + i);
            item.put("value", random.nextDouble() * 1000);
            item.put("timestamp", LocalDateTime.now());
            item.put("tags", Arrays.asList("tag1", "tag2", "tag3"));
            items.add(item);
        }
        
        data.put("items", items);
        data.put("metadata", Map.of(
            "size", size,
            "created", LocalDateTime.now(),
            "checksum", items.hashCode()
        ));
        
        return data;
    }

    /**
     * 處理記憶體數據
     */
    private void processMemoryData(List<byte[]> blocks, List<Map<String, Object>> structures) {
        // 模擬數據處理
        for (byte[] block : blocks) {
            // 簡單的數據處理
            int sum = 0;
            for (byte b : block) {
                sum += b;
            }
        }
        
        for (Map<String, Object> structure : structures) {
            // 簡單的數據處理
            structure.get("items");
        }
    }

    /**
     * 模擬文件I/O
     */
    private String simulateFileIO(int id) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            String data = "File operation " + id + " - " + LocalDateTime.now();
            baos.write(data.getBytes());
            
            // 模擬I/O延遲
            Thread.sleep(ThreadLocalRandom.current().nextInt(5, 20));
            
            return baos.toString();
        } catch (IOException | InterruptedException e) {
            return "Error in file IO: " + e.getMessage();
        }
    }

    /**
     * 模擬網絡I/O
     */
    private void simulateNetworkIO() {
        try {
            // 模擬網絡延遲
            Thread.sleep(ThreadLocalRandom.current().nextInt(10, 50));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 模擬數據庫I/O
     */
    private void simulateDatabaseIO(int id) {
        try {
            // 模擬數據庫查詢延遲
            Thread.sleep(ThreadLocalRandom.current().nextInt(20, 100));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 執行併發任務
     */
    private String performConcurrentTask(int taskId) {
        try {
            // 模擬不同的工作負載
            Thread.sleep(ThreadLocalRandom.current().nextInt(100, 500));
            
            // 執行一些計算
            double result = IntStream.range(0, 1000)
                .parallel()
                .mapToDouble(i -> Math.sqrt(i * taskId))
                .sum();
                
            return "Task-" + taskId + " completed with result: " + 
                   BigDecimal.valueOf(result).setScale(2, RoundingMode.HALF_UP);
                   
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Task-" + taskId + " interrupted";
        }
    }
}