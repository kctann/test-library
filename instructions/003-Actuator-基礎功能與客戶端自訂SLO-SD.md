# 003-Actuator基礎功能與客戶端自訂SLO系統 - 系統設計文檔

## 1. 系統概述

### 1.1 設計目標
- 提供 Spring Boot Actuator 基礎監控功能
- 實作 SLI (Service Level Indicators) 數據收集基礎設施
- 支援客戶端完全自訂 SLA/SLO 標準
- 常態開啟監控，支援 YAML 和 Annotation 排除機制
- 提供動態配置調整能力

### 1.2 設計原則
- **Library 職責**: 提供 SLI 收集和 SLO 檢查基礎設施
- **客戶端職責**: 定義自己的 SLA/SLO 標準和違反處理策略
- **Opt-out 策略**: 預設啟用監控，客戶端可選擇性排除
- **多層級配置**: 支援全域、端點、方法層級的 SLO 設定

## 2. 基礎 Actuator 功能設計

### 2.1 核心端點
- **Health Check**: `/actuator/health` - Library 健康狀態檢查
- **Info**: `/actuator/info` - Library 版本和配置資訊  
- **Metrics**: `/actuator/metrics` - 基礎 SLI 指標數據
- **Library SLO**: `/actuator/library-slo` - 客戶端 SLO 管理端點

### 2.2 SLI 數據收集 (Golden Signals)
- **延遲 (Latency)**: API 回應時間 P50/P90/P95/P99
- **流量 (Traffic)**: 每秒請求數 (RPS)
- **錯誤 (Errors)**: 錯誤率百分比
- **飽和度 (Saturation)**: 資源使用率

## 3. 客戶端自訂 SLO 系統

### 3.1 多層級 SLO 配置
```yaml
test-library:
  actuator:
    enabled: true                    # 常態開啟
    
  slo:
    # 全域預設 SLO
    defaults:
      latency-p95: 200ms
      availability: 99.5%
      error-rate: 0.5%
      
    # 端點特定 SLO
    endpoints:
      "/api/users":
        latency-p95: 100ms
        availability: 99.9%
        error-rate: 0.1%
        violation-actions: ["log-violation", "alert-prometheus"]
      "/api/reports":
        latency-p95: 5000ms
        availability: 99.0%
        violation-actions: ["log-violation", "circuit-breaker"]
        
    # 全域違反處理策略
    global-violation-actions:
      - log-violation
```

### 3.2 Annotation 排除和自訂機制
```java
@RestController
public class UserController {
    
    @GetMapping("/api/users")
    @LibrarySLO(
        latencyP95 = "80ms",
        availability = "99.95",
        errorRate = "0.05",
        violationActions = {"log-violation", "alert-prometheus"}
    )
    public List<User> getUsers() {...}
    
    @GetMapping("/internal/health")
    @LibraryMonitoring(enabled = false)  // 排除監控
    public String internalHealth() {...}
}
```

## 4. SLA 動態處理器系統

### 4.1 處理器接口設計
```java
public interface SLAViolationHandler {
    
    /**
     * 處理 SLA 違反事件
     * @param violation SLA 違反事件詳情
     * @param config 客戶端 SLO 配置
     */
    void handleViolation(SLAViolationEvent violation, SLOConfig config);
    
    /**
     * 處理器名稱，用於配置中引用
     */
    String getHandlerName();
    
    /**
     * 是否支援此類型的違反事件
     */
    boolean supports(SLAViolationType type);
}

public class SLAViolationEvent {
    private String endpoint;
    private SLAViolationType violationType;
    private double currentValue;
    private double threshold;
    private LocalDateTime timestamp;
    private Map<String, Object> context;
    // getters/setters
}

public enum SLAViolationType {
    LATENCY_BREACH,
    AVAILABILITY_BREACH, 
    ERROR_RATE_BREACH,
    THROUGHPUT_BELOW_MINIMUM
}
```

### 4.2 內建處理器實作

#### 4.2.1 日誌違反處理器
```java
@Component
public class LogViolationHandler implements SLAViolationHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(LogViolationHandler.class);
    
    @Override
    public void handleViolation(SLAViolationEvent violation, SLOConfig config) {
        logger.warn("SLA Violation Detected - Endpoint: {}, Type: {}, Current: {}, Threshold: {}, Time: {}",
                violation.getEndpoint(),
                violation.getViolationType(),
                violation.getCurrentValue(),
                violation.getThreshold(),
                violation.getTimestamp());
                
        // TODO: 實作結構化日誌輸出
        // TODO: 支援可配置的日誌級別
        // TODO: 整合 MDC 上下文資訊
    }
    
    @Override
    public String getHandlerName() {
        return "log-violation";
    }
    
    @Override
    public boolean supports(SLAViolationType type) {
        return true; // 支援所有類型的違反事件
    }
}
```

#### 4.2.2 Prometheus 告警處理器  
```java
@Component
@ConditionalOnClass(name = "io.micrometer.prometheus.PrometheusConfig")
public class PrometheusAlertHandler implements SLAViolationHandler {
    
    private final MeterRegistry meterRegistry;
    
    @Override
    public void handleViolation(SLAViolationEvent violation, SLOConfig config) {
        logger.info("Sending Prometheus alert for SLA violation: {}", violation.getEndpoint());
        
        // 記錄違反指標到 Prometheus
        Counter.builder("library_sla_violations_total")
                .tag("endpoint", violation.getEndpoint())
                .tag("violation_type", violation.getViolationType().name())
                .register(meterRegistry)
                .increment();
                
        // TODO: 整合 Prometheus Alertmanager
        // TODO: 發送自訂 Alert 規則
        // TODO: 支援不同嚴重程度的告警
    }
    
    @Override
    public String getHandlerName() {
        return "alert-prometheus";
    }
    
    @Override
    public boolean supports(SLAViolationType type) {
        return true;
    }
}
```

#### 4.2.3 熔斷器處理器
```java
@Component
public class CircuitBreakerHandler implements SLAViolationHandler {
    
    private final Map<String, CircuitBreakerState> circuitBreakers = new ConcurrentHashMap<>();
    
    @Override
    public void handleViolation(SLAViolationEvent violation, SLOConfig config) {
        logger.info("Activating circuit breaker for endpoint: {}", violation.getEndpoint());
        
        // 觸發熔斷器
        circuitBreakers.computeIfAbsent(violation.getEndpoint(), 
                k -> new CircuitBreakerState()).tripBreaker();
                
        // TODO: 整合 Spring Cloud Circuit Breaker
        // TODO: 實作自動恢復機制
        // TODO: 支援不同的熔斷策略（快速失敗、降級服務等）
        // TODO: 提供熔斷器狀態監控端點
    }
    
    @Override
    public String getHandlerName() {
        return "circuit-breaker";
    }
    
    @Override
    public boolean supports(SLAViolationType type) {
        // 只支援延遲和錯誤率違反
        return type == SLAViolationType.LATENCY_BREACH || 
               type == SLAViolationType.ERROR_RATE_BREACH;
    }
    
    private static class CircuitBreakerState {
        private volatile boolean isOpen = false;
        private LocalDateTime tripTime;
        
        void tripBreaker() {
            this.isOpen = true;
            this.tripTime = LocalDateTime.now();
        }
    }
}
```

## 5. 技術架構設計

### 5.1 核心組件
- **LibraryActuatorAutoConfiguration**: 條件化自動配置
- **LibrarySLICollector**: SLI 數據收集器
- **LibrarySLOChecker**: SLO 合規性檢查器
- **LibrarySLOManager**: 動態 SLO 管理
- **SLAViolationProcessor**: 違反事件處理協調器

### 5.2 數據流向
1. AOP 攔截器收集 SLI 數據
2. SLO 檢查器進行合規性驗證
3. 違反事件觸發客戶端定義的處理動作
4. Actuator 端點提供數據查詢和管理功能

### 5.3 違反處理流程
```java
@Component
public class SLAViolationProcessor {
    
    private final List<SLAViolationHandler> handlers;
    
    @EventListener
    public void processViolation(SLAViolationEvent event) {
        SLOConfig config = sloConfigService.getConfigForEndpoint(event.getEndpoint());
        
        // 執行配置的處理器
        for (String handlerName : config.getViolationActions()) {
            SLAViolationHandler handler = findHandler(handlerName);
            if (handler != null && handler.supports(event.getViolationType())) {
                try {
                    handler.handleViolation(event, config);
                } catch (Exception e) {
                    logger.error("Error in SLA violation handler: {}", handlerName, e);
                }
            }
        }
    }
}
```

## 6. PENDING 功能清單

### 6.1 外部整合功能 (需要外部實體驗證)
- **Prometheus Alertmanager 整合**: 發送結構化告警到 Alertmanager
- **Spring Cloud Circuit Breaker 整合**: 與 Spring Cloud 熔斷器整合
- **外部監控系統整合**: 支援 Grafana、DataDog 等監控平台
- **Webhook 通知**: 支援自訂 Webhook 端點通知
- **消息隊列告警**: 整合 RabbitMQ、Kafka 進行告警通知

### 6.2 進階功能 (後續擴展)
- **自適應 SLO**: 根據歷史數據自動調整 SLO 標準
- **SLA 報告生成**: 自動生成週期性 SLA 合規報告
- **違反事件聚合**: 智能合併重複的違反事件
- **預測性告警**: 基於趨勢預測潛在的 SLA 違反

## 7. 動態配置系統

### 7.1 運行時 SLO 調整
- REST API 支援動態修改 SLO 標準
- 配置變更即時生效，無需重啟
- 支援 SLO 配置的版本管理和回滾

### 7.2 路徑過濾機制
- 支援 Ant-style 路徑匹配
- include/exclude 路徑配置
- 動態添加/移除監控路徑

## 8. 實作檔案清單

### 8.1 AutoConfiguration
- `LibraryActuatorAutoConfiguration.java`
- `LibraryActuatorProperties.java`
- `LibrarySLOProperties.java`

### 8.2 SLA 違反處理
- `SLAViolationHandler.java` (interface)
- `LogViolationHandler.java`
- `PrometheusAlertHandler.java`
- `CircuitBreakerHandler.java`
- `SLAViolationProcessor.java`

### 8.3 核心功能
- `LibrarySLICollector.java`
- `LibrarySLOChecker.java`
- `LibraryHealthIndicator.java`
- `LibraryInfoContributor.java`

### 8.4 SLO 管理
- `LibrarySLOManager.java`
- `LibrarySLOEndpoint.java`

### 8.5 AOP 和攔截
- `LibraryMonitoringInterceptor.java`
- `LibraryMetricsAspect.java`

### 8.6 事件和模型
- `SLAViolationEvent.java`
- `SLAViolationType.java` (enum)
- `SLOConfig.java`

## 9. 測試策略

### 9.1 功能測試
- SLI 數據收集準確性測試
- SLO 合規性檢查測試
- 違反處理器執行測試
- 動態配置調整測試

### 9.2 整合測試
- 不同 Spring Boot 版本相容性測試
- Actuator 端點可用性測試
- SLA 違反處理流程測試
- 多處理器同時執行測試

## 10. Milestone 規劃

### M1: 基礎 Actuator 整合與 SLI 收集
- 基本 Health Check、Info、Metrics 端點
- Golden Signals 數據收集
- AOP 攔截器實作

### M2: 客戶端 SLO 配置系統
- YAML 多層級 SLO 配置
- Annotation 支援和排除機制
- 基本的 SLO 合規性檢查

### M3: SLA 違反處理系統
- SLA 違反處理器接口和實作
- 三個內建處理器
- 違反事件處理協調器

### M4: 動態配置與管理功能
- SLO 管理端點
- 運行時配置調整
- 管理和監控功能

## 11. 安全和隱私考量

### 11.1 數據保護
- 自動過濾敏感請求參數
- 可配置的數據脫敏規則
- 限制 SLI 數據的存取權限

### 11.2 預設安全策略
- 敏感端點預設不監控
- 可配置的白名單/黑名單機制
- 支援基於角色的 Actuator 端點存取控制