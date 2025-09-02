# 003-M2-客戶端SLO配置系統-TODO

## 功能名稱
客戶端自訂 SLA/SLO 配置系統

## 實作項目
1. 建立多層級 SLO 配置屬性系統
2. 實作 Annotation 支援和排除機制
3. 建立 SLO 合規性檢查器
4. 實作路徑過濾和匹配機制
5. 建立配置驗證和預設值系統
6. 實作 SLO 配置管理服務

## 受影響的程式名稱
- `[003][M2]com/jamestann/test/library/actuator/LibrarySLOProperties.java` - SLO 配置屬性
- `[003][M2]com/jamestann/test/library/actuator/annotation/LibrarySLO.java` - SLO 自訂註解
- `[003][M2]com/jamestann/test/library/actuator/annotation/LibraryMonitoring.java` - 監控控制註解
- `[003][M2]com/jamestann/test/library/actuator/LibrarySLOChecker.java` - SLO 合規性檢查器
- `[003][M2]com/jamestann/test/library/actuator/LibraryPathMatcher.java` - 路徑匹配器
- `[003][M2]com/jamestann/test/library/actuator/LibrarySLOConfigService.java` - SLO 配置管理服務
- `[003][M2]com/jamestann/test/library/actuator/model/SLOConfig.java` - SLO 配置模型
- `[003][M2]com/jamestann/test/library/actuator/model/EndpointSLO.java` - 端點 SLO 模型

## TODO List

### TODO 1: 建立 SLO 配置屬性系統
**描述**: 建立支援多層級 SLO 配置的屬性類和模型
**Input**: 無（新建檔案）
**Output**: LibrarySLOProperties.java, SLOConfig.java, EndpointSLO.java
**檔案**: 
- `src/main/java/com/jamestann/test/library/actuator/LibrarySLOProperties.java`
- `src/main/java/com/jamestann/test/library/actuator/model/SLOConfig.java`
- `src/main/java/com/jamestann/test/library/actuator/model/EndpointSLO.java`
**實作內容**:
- @ConfigurationProperties("test-library.slo") 屬性綁定
- 全域預設 SLO 配置 (defaults)
- 端點特定 SLO 配置 (endpoints)
- 違反處理動作配置 (violation-actions)
- 配置驗證和預設值處理

### TODO 2: 實作 SLO 自訂註解系統
**描述**: 建立方法層級的 SLO 自訂和監控控制註解
**Input**: 無（新建檔案）
**Output**: LibrarySLO.java, LibraryMonitoring.java
**檔案**: 
- `src/main/java/com/jamestann/test/library/actuator/annotation/LibrarySLO.java`
- `src/main/java/com/jamestann/test/library/actuator/annotation/LibraryMonitoring.java`
**實作內容**:
- @LibrarySLO 註解：支援方法層級 SLO 覆寫
- @LibraryMonitoring 註解：支援監控開關控制
- 註解參數驗證和預設值
- 與 AOP 系統整合點設計

### TODO 3: 實作路徑匹配和過濾系統
**描述**: 建立支援 Ant-style 路徑匹配的過濾器
**Input**: LibrarySLOProperties
**Output**: LibraryPathMatcher.java
**檔案**: `src/main/java/com/jamestann/test/library/actuator/LibraryPathMatcher.java`
**實作內容**:
- Ant-style 路徑模式匹配 (/**、/api/*)
- include/exclude 路徑清單處理
- 路徑優先級排序機制
- 效能優化的匹配演算法

### TODO 4: 建立 SLO 合規性檢查器
**描述**: 實作 SLO 合規性檢查和違反偵測邏輯
**Input**: LibrarySLOProperties, 收集的 SLI 數據
**Output**: LibrarySLOChecker.java
**檔案**: `src/main/java/com/jamestann/test/library/actuator/LibrarySLOChecker.java`
**實作內容**:
- 延遲 SLO 檢查 (P95, P99 百分位數)
- 可用性 SLO 檢查
- 錯誤率 SLO 檢查
- 吞吐量 SLO 檢查
- 違反事件生成和發送

### TODO 5: 實作 SLO 配置管理服務
**描述**: 建立統一的 SLO 配置管理和查詢服務
**Input**: LibrarySLOProperties, 註解解析結果
**Output**: LibrarySLOConfigService.java
**檔案**: `src/main/java/com/jamestann/test/library/actuator/LibrarySLOConfigService.java`
**實作內容**:
- 多層級配置合併邏輯 (全域 → 端點 → 方法)
- 配置快取和刷新機制
- 註解配置解析和應用
- 配置變更通知機制

### TODO 6: 整合 M1 組件與 SLO 系統
**描述**: 將 M1 的 SLI 收集器與 M2 的 SLO 檢查器整合
**Input**: LibrarySLICollector (M1), LibrarySLOChecker (M2)
**Output**: 更新 LibraryMonitoringInterceptor.java
**檔案**: `src/main/java/com/jamestann/test/library/actuator/LibraryMonitoringInterceptor.java`
**實作內容**:
- 在數據收集後進行 SLO 檢查
- 根據路徑匹配決定監控範圍
- 註解配置的運行時解析
- SLO 違反事件的觸發機制

### TODO 7: 建立配置驗證和預設值系統
**描述**: 實作 SLO 配置的驗證邏輯和智慧預設值
**Input**: LibrarySLOProperties
**Output**: LibrarySLOConfigValidator.java
**檔案**: `src/main/java/com/jamestann/test/library/actuator/LibrarySLOConfigValidator.java`
**實作內容**:
- 配置值範圍驗證 (0-100% 可用性等)
- 配置衝突檢測
- 智慧預設值分配
- 配置變更影響分析

### TODO 8: 更新自動配置以支援 M2 功能
**描述**: 擴展 LibraryActuatorAutoConfiguration 以包含 M2 組件
**Input**: M2 所有組件
**Output**: 更新 LibraryActuatorAutoConfiguration.java
**檔案**: `src/main/java/com/jamestann/test/library/actuator/LibraryActuatorAutoConfiguration.java`
**實作內容**:
- 註冊 M2 新增的 Bean
- 條件化配置 SLO 功能
- 確保組件間依賴關係正確
- 配置屬性驗證整合

### TODO 9: 建立 M2 測試案例
**描述**: 建立 M2 功能的完整測試套件
**Input**: 所有 M2 實作類別
**Output**: 測試類別
**檔案**: 
- `src/test/java/com/jamestann/test/library/actuator/LibrarySLOCheckerTest.java`
- `src/test/java/com/jamestann/test/library/actuator/LibraryPathMatcherTest.java`
- `src/test/java/com/jamestann/test/library/actuator/LibrarySLOConfigServiceTest.java`
- `src/test/java/com/jamestann/test/library/actuator/annotation/AnnotationIntegrationTest.java`
**實作內容**:
- SLO 配置解析測試
- 路徑匹配邏輯測試
- 註解功能整合測試
- SLO 合規性檢查測試

## 驗證標準
1. ✅ 支援 YAML 多層級 SLO 配置 (全域、端點、方法)
2. ✅ @LibrarySLO 註解可覆寫端點 SLO 設定
3. ✅ @LibraryMonitoring(enabled=false) 可排除特定端點
4. ✅ 路徑過濾器正確匹配 include/exclude 規則
5. ✅ SLO 合規性檢查準確偵測違反情況
6. ✅ 配置變更可即時生效，無需重啟
7. ✅ 配置驗證能捕捉無效的 SLO 設定
8. ✅ 多層級配置合併邏輯正確運作

## 配置範例
```yaml
test-library:
  slo:
    defaults:
      latency-p95: 200ms
      availability: 99.5%
      error-rate: 0.5%
    endpoints:
      "/api/users":
        latency-p95: 100ms
        availability: 99.9%
        error-rate: 0.1%
      "/api/reports":
        latency-p95: 5000ms
        availability: 99.0%
    path-filters:
      include: ["/api/**", "/v1/**"]
      exclude: ["/internal/**", "/admin/**"]
```

## 註解使用範例
```java
@RestController
public class UserController {
    
    @GetMapping("/api/users")
    @LibrarySLO(latencyP95 = "80ms", availability = "99.95")
    public List<User> getUsers() {...}
    
    @GetMapping("/internal/health")
    @LibraryMonitoring(enabled = false)
    public String internalHealth() {...}
}
```

## 注意事項
- M2 建立在 M1 基礎上，需確保 M1 功能完整運作
- 配置系統要支援熱重載，避免重啟應用程式
- 路徑匹配效能要優化，避免影響請求處理速度
- SLO 檢查要考慮統計學意義，避免短期波動造成誤報
- 註解解析要快取結果，避免重複反射操作