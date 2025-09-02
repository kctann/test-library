# 003-M3-SLA違反處理系統-TODO

## 功能名稱
SLA 違反動態處理系統

## 實作項目
1. 建立 SLA 違反處理器接口和事件模型
2. 實作三種內建違反處理器
3. 建立違反事件處理協調器
4. 實作違反事件生成和分發機制
5. 建立處理器註冊和發現系統
6. 實作違反歷史記錄和查詢功能

## 受影響的程式名稱
- `[003][M3]com/jamestann/test/library/actuator/handler/SLAViolationHandler.java` - 違反處理器接口
- `[003][M3]com/jamestann/test/library/actuator/handler/LogViolationHandler.java` - 日誌處理器
- `[003][M3]com/jamestann/test/library/actuator/handler/PrometheusAlertHandler.java` - Prometheus 告警處理器
- `[003][M3]com/jamestann/test/library/actuator/handler/CircuitBreakerHandler.java` - 熔斷器處理器
- `[003][M3]com/jamestann/test/library/actuator/SLAViolationProcessor.java` - 違反事件處理協調器
- `[003][M3]com/jamestann/test/library/actuator/event/SLAViolationEvent.java` - 違反事件模型
- `[003][M3]com/jamestann/test/library/actuator/event/SLAViolationType.java` - 違反類型枚舉
- `[003][M3]com/jamestann/test/library/actuator/LibraryViolationHistoryService.java` - 違反歷史服務

## TODO List

### TODO 1: 建立 SLA 違反處理器接口和事件模型
**描述**: 設計違反處理系統的核心接口和事件模型
**Input**: 無（新建檔案）
**Output**: SLAViolationHandler.java, SLAViolationEvent.java, SLAViolationType.java
**檔案**: 
- `src/main/java/com/jamestann/test/library/actuator/handler/SLAViolationHandler.java`
- `src/main/java/com/jamestann/test/library/actuator/event/SLAViolationEvent.java`
- `src/main/java/com/jamestann/test/library/actuator/event/SLAViolationType.java`
**實作內容**:
- SLAViolationHandler 接口定義
- SLAViolationEvent 事件模型 (endpoint, type, value, threshold, context)
- SLAViolationType 枚舉 (LATENCY_BREACH, AVAILABILITY_BREACH, ERROR_RATE_BREACH, THROUGHPUT_BELOW_MINIMUM)
- 處理器支援性檢查方法
- 事件序列化和反序列化支援

### TODO 2: 實作日誌違反處理器
**描述**: 實作結構化日誌輸出的 SLA 違反處理器
**Input**: SLAViolationHandler 接口
**Output**: LogViolationHandler.java
**檔案**: `src/main/java/com/jamestann/test/library/actuator/handler/LogViolationHandler.java`
**實作內容**:
- 實作 SLAViolationHandler 接口
- 結構化日誌輸出 (endpoint, violation type, metrics)
- 支援可配置日誌級別 (WARN, ERROR)
- MDC 上下文整合
- TODO: 實作結構化日誌格式 (JSON)
- TODO: 支援日誌聚合標籤

### TODO 3: 實作 Prometheus 告警處理器
**描述**: 實作發送 Prometheus 指標和告警的違反處理器
**Input**: SLAViolationHandler 接口, MeterRegistry
**Output**: PrometheusAlertHandler.java
**檔案**: `src/main/java/com/jamestann/test/library/actuator/handler/PrometheusAlertHandler.java`
**實作內容**:
- 條件化載入 (@ConditionalOnClass Prometheus)
- 違反指標記錄 (library_sla_violations_total)
- 違反事件標籤管理 (endpoint, violation_type, severity)
- 基礎告警指標生成
- TODO: 整合 Prometheus Alertmanager 推送
- TODO: 自訂告警規則生成
- TODO: 告警嚴重程度分級

### TODO 4: 實作熔斷器處理器
**描述**: 實作自動熔斷機制的違反處理器
**Input**: SLAViolationHandler 接口
**Output**: CircuitBreakerHandler.java
**檔案**: `src/main/java/com/jamestann/test/library/actuator/handler/CircuitBreakerHandler.java`
**實作內容**:
- 端點級別熔斷器狀態管理
- 熔斷觸發條件 (連續違反次數、時間窗口)
- 基礎熔斷器狀態 (OPEN, HALF_OPEN, CLOSED)
- 熔斷器狀態持久化
- TODO: 整合 Spring Cloud Circuit Breaker
- TODO: 自動恢復機制
- TODO: 降級服務整合
- TODO: 熔斷器狀態監控端點

### TODO 5: 建立違反事件處理協調器
**描述**: 建立中央化的違反事件處理協調和分發系統
**Input**: List<SLAViolationHandler>, LibrarySLOConfigService
**Output**: SLAViolationProcessor.java
**檔案**: `src/main/java/com/jamestann/test/library/actuator/SLAViolationProcessor.java`
**實作內容**:
- Spring Event 監聽器 (@EventListener)
- 處理器發現和註冊機制
- 並行處理器執行管理
- 處理器異常處理和隔離
- 處理結果統計和監控
- 處理器執行順序控制

### TODO 6: 實作違反歷史記錄服務
**描述**: 建立違反事件的歷史記錄和查詢功能
**Input**: SLAViolationEvent
**Output**: LibraryViolationHistoryService.java
**檔案**: `src/main/java/com/jamestann/test/library/actuator/LibraryViolationHistoryService.java`
**實作內容**:
- 違反事件記錄存儲 (記憶體環形緩衝區)
- 違反統計計算 (頻率、趨勢)
- 查詢 API (按端點、時間範圍、違反類型)
- 資料清理和保留策略
- 違反事件聚合和去重

### TODO 7: 整合違反檢測與處理系統
**描述**: 將 M2 的 SLO 檢查器與 M3 的違反處理系統整合
**Input**: LibrarySLOChecker (M2), SLAViolationProcessor (M3)
**Output**: 更新 LibrarySLOChecker.java
**檔案**: `src/main/java/com/jamestann/test/library/actuator/LibrarySLOChecker.java`
**實作內容**:
- SLO 違反偵測後發送 SLAViolationEvent
- 違反事件生成邏輯
- 重複違反事件抑制機制
- 違反上下文資訊收集

### TODO 8: 更新自動配置以支援 M3 功能
**描述**: 擴展自動配置以包含違反處理系統
**Input**: M3 所有組件
**Output**: 更新 LibraryActuatorAutoConfiguration.java
**檔案**: `src/main/java/com/jamestann/test/library/actuator/LibraryActuatorAutoConfiguration.java`
**實作內容**:
- 註冊內建違反處理器
- 違反事件處理協調器配置
- 條件化載入處理器 (based on classpath)
- 處理器優先級和排序配置

### TODO 9: 建立 M3 測試案例
**描述**: 建立 M3 功能的完整測試套件
**Input**: 所有 M3 實作類別
**Output**: 測試類別
**檔案**: 
- `src/test/java/com/jamestann/test/library/actuator/handler/LogViolationHandlerTest.java`
- `src/test/java/com/jamestann/test/library/actuator/handler/PrometheusAlertHandlerTest.java`
- `src/test/java/com/jamestann/test/library/actuator/handler/CircuitBreakerHandlerTest.java`
- `src/test/java/com/jamestann/test/library/actuator/SLAViolationProcessorTest.java`
- `src/test/java/com/jamestann/test/library/actuator/ViolationHandlingIntegrationTest.java`
**實作內容**:
- 個別處理器功能測試
- 違反事件處理流程測試
- 處理器異常處理測試
- 並行處理器執行測試
- 端到端違反處理整合測試

### TODO 10: 建立處理器配置和擴展機制
**描述**: 提供客戶端自訂處理器的註冊和配置機制
**Input**: SLAViolationHandler 接口
**Output**: LibraryHandlerRegistry.java
**檔案**: `src/main/java/com/jamestann/test/library/actuator/LibraryHandlerRegistry.java`
**實作內容**:
- 動態處理器註冊機制
- 處理器配置參數管理
- 處理器啟用/停用控制
- 客戶端自訂處理器支援

## 驗證標準
1. ✅ SLA 違反能觸發對應的處理器執行
2. ✅ 日誌處理器正確輸出結構化違反資訊
3. ✅ Prometheus 處理器產生正確的指標
4. ✅ 熔斷器處理器在違反時正確觸發熔斷
5. ✅ 處理協調器能並行執行多個處理器
6. ✅ 處理器異常不影響其他處理器執行
7. ✅ 違反歷史記錄能正確查詢和統計
8. ✅ 客戶端可配置啟用/停用特定處理器

## 配置範例
```yaml
test-library:
  slo:
    endpoints:
      "/api/users":
        latency-p95: 100ms
        violation-actions: ["log-violation", "alert-prometheus"]
      "/api/critical":
        latency-p95: 50ms
        violation-actions: ["log-violation", "circuit-breaker", "alert-prometheus"]
    
    violation-handlers:
      log-violation:
        enabled: true
        log-level: "WARN"
        structured-format: true
      alert-prometheus:
        enabled: true
        severity-mapping:
          LATENCY_BREACH: "warning"
          ERROR_RATE_BREACH: "critical"
      circuit-breaker:
        enabled: true
        failure-threshold: 5
        recovery-timeout: "30s"
```

## 處理器擴展範例
```java
@Component
public class CustomWebhookHandler implements SLAViolationHandler {
    
    @Override
    public void handleViolation(SLAViolationEvent violation, SLOConfig config) {
        // 客戶端自訂 Webhook 通知邏輯
        webhookService.sendAlert(violation);
    }
    
    @Override
    public String getHandlerName() {
        return "custom-webhook";
    }
}
```

## 注意事項
- M3 依賴 M1 和 M2 的完整功能，需確保前置組件正常運作
- 處理器執行要考慮效能影響，避免阻塞主要業務流程
- 違反事件處理要有適當的異常隔離，單一處理器失敗不影響其他處理器
- 熔斷器狀態變更要考慮多實例環境的狀態同步問題
- 處理器的 TODO 項目標記了需要外部依賴的進階功能，可在後續版本實作