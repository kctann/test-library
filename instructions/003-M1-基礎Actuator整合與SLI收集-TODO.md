# 003-M1-基礎Actuator整合與SLI收集-TODO

## 功能名稱
基礎 Actuator 整合與 SLI 數據收集系統

## 實作項目
1. 建立 Library Actuator 自動配置
2. 實作基礎 Health Check 功能
3. 實作 Info Contributor 功能  
4. 實作 Golden Signals SLI 數據收集
5. 建立 AOP 攔截器進行數據收集
6. 實作基礎 Metrics 端點整合

## 受影響的程式名稱
- `[003][M1]com/jamestann/test/library/actuator/LibraryActuatorAutoConfiguration.java` - 自動配置類
- `[003][M1]com/jamestann/test/library/actuator/LibraryActuatorProperties.java` - 配置屬性
- `[003][M1]com/jamestann/test/library/actuator/LibraryHealthIndicator.java` - 健康檢查
- `[003][M1]com/jamestann/test/library/actuator/LibraryInfoContributor.java` - 資訊貢獻者
- `[003][M1]com/jamestann/test/library/actuator/LibrarySLICollector.java` - SLI 數據收集器
- `[003][M1]com/jamestann/test/library/actuator/LibraryMonitoringInterceptor.java` - AOP 攔截器
- `[003][M1]com/jamestann/test/library/actuator/LibraryMetricsAspect.java` - Metrics AOP 切面
- `[003][M1]META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` - 自動配置註冊

## TODO List

### TODO 1: 建立 Actuator 自動配置基礎架構
**描述**: 建立 Library Actuator 的自動配置類和配置屬性類
**Input**: 無（新建檔案）
**Output**: LibraryActuatorAutoConfiguration.java, LibraryActuatorProperties.java
**檔案**: 
- `src/main/java/com/jamestann/test/library/actuator/LibraryActuatorAutoConfiguration.java`
- `src/main/java/com/jamestann/test/library/actuator/LibraryActuatorProperties.java`
**實作內容**:
- 條件化自動配置 (@ConditionalOnClass, @ConditionalOnProperty)
- 基礎配置屬性 (test-library.actuator.enabled)
- Bean 注入和依賴管理
- 與 Spring Boot Actuator 的整合點

### TODO 2: 實作 Library Health Indicator
**描述**: 建立 Library 專用的健康檢查指標，顯示 Library 運行狀態
**Input**: LibraryActuatorAutoConfiguration
**Output**: LibraryHealthIndicator.java
**檔案**: `src/main/java/com/jamestann/test/library/actuator/LibraryHealthIndicator.java`
**實作內容**:
- 實作 HealthIndicator 接口
- 檢查 Library 核心組件狀態
- 提供 Library 版本和功能資訊
- 整合到 /actuator/health 端點

### TODO 3: 實作 Library Info Contributor
**描述**: 建立 Library 資訊貢獻者，提供 Library 相關資訊到 /actuator/info
**Input**: LibraryActuatorAutoConfiguration
**Output**: LibraryInfoContributor.java
**檔案**: `src/main/java/com/jamestann/test/library/actuator/LibraryInfoContributor.java`
**實作內容**:
- 實作 InfoContributor 接口
- 提供 Library 版本、功能、配置資訊
- 整合 Git 資訊和建置時間
- 動態顯示啟用的功能列表

### TODO 4: 建立 SLI 數據收集器
**描述**: 實作 Golden Signals 數據收集基礎設施
**Input**: MeterRegistry, LibraryActuatorProperties
**Output**: LibrarySLICollector.java
**檔案**: `src/main/java/com/jamestann/test/library/actuator/LibrarySLICollector.java`
**實作內容**:
- Golden Signals 數據收集 (Latency, Traffic, Errors, Saturation)
- Timer, Counter, Gauge 指標建立
- 數據標籤管理 (endpoint, method, status)
- 線程安全的數據收集機制

### TODO 5: 實作 AOP 監控攔截器
**描述**: 建立 AOP 攔截器，自動收集所有 Controller 方法的 SLI 數據
**Input**: LibrarySLICollector, LibraryActuatorProperties
**Output**: LibraryMonitoringInterceptor.java
**檔案**: `src/main/java/com/jamestann/test/library/actuator/LibraryMonitoringInterceptor.java`
**實作內容**:
- Web 請求攔截器實作 (HandlerInterceptor)
- 請求開始/結束時間記錄
- 自動收集回應時間、狀態碼、異常資訊
- 支援路徑過濾和排除機制

### TODO 6: 實作 Metrics AOP 切面
**描述**: 建立 AOP 切面，提供更細緻的方法層級監控
**Input**: LibrarySLICollector
**Output**: LibraryMetricsAspect.java
**檔案**: `src/main/java/com/jamestann/test/library/actuator/LibraryMetricsAspect.java`
**實作內容**:
- @Around 切面實作
- 方法執行時間監控
- 異常捕捉和記錄
- 支援自訂 @Timed 註解

### TODO 7: 註冊自動配置
**描述**: 將 LibraryActuatorAutoConfiguration 註冊到 Spring Boot 自動配置系統
**Input**: LibraryActuatorAutoConfiguration
**Output**: 更新 AutoConfiguration.imports
**檔案**: `src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
**實作內容**:
- 添加 com.jamestann.test.library.actuator.LibraryActuatorAutoConfiguration
- 確保配置載入順序正確

### TODO 8: 建立 M1 單元測試案例
**描述**: 建立 M1 核心組件的單元測試套件
**Input**: 所有 M1 實作類別
**Output**: 單元測試類別
**檔案**: 
- `src/test/java/com/jamestann/test/library/actuator/LibraryActuatorAutoConfigurationTest.java`
- `src/test/java/com/jamestann/test/library/actuator/LibraryHealthIndicatorTest.java`
- `src/test/java/com/jamestann/test/library/actuator/LibrarySLICollectorTest.java`
**實作內容**:
- 自動配置載入測試
- Health Check 回應測試
- SLI 數據收集準確性測試
- LibrarySLICollector 多線程安全測試
- LibraryMonitoringInterceptor 異常處理測試
- 各組件條件化載入測試
- 配置屬性驗證測試

### TODO 9: 在 Demo 中建立測試端點 Controller
**描述**: 在 Demo 模組建立測試端點，供手動測試和整合測試使用
**Input**: 無（新建檔案）
**Output**: ActuatorTestController.java
**檔案**: `test-library-demo/src/main/java/com/jamestann/test/library/demo/controller/ActuatorTestController.java`
**實作內容**:
- `GET /test/fast` - 快速回應端點 (< 50ms)
- `GET /test/normal` - 正常回應端點 (50-200ms)
- `GET /test/slow` - 慢速回應端點 (300ms+，使用 Thread.sleep)
- `GET /test/error` - 錯誤端點 (拋出 RuntimeException)
- `GET /test/cpu-intensive` - CPU 密集運算端點
- `GET /test/batch` - 批量處理多個子請求端點

### TODO 10: 建立 M1 核心功能整合測試
**描述**: 建立 M1 核心功能的整合測試，驗證組件間協作
**Input**: M1 所有組件
**Output**: M1IntegrationTest.java
**檔案**: `test-library-core/src/test/java/com/jamestann/test/library/actuator/M1IntegrationTest.java`
**實作內容**:
- LibraryActuatorAutoConfiguration 自動載入測試
- LibraryHealthIndicator 與 Spring Boot Health 整合測試
- LibraryInfoContributor 與 Spring Boot Info 整合測試
- LibrarySLICollector 數據收集整合測試
- AOP 攔截器與 Spring MVC 整合測試
- 組件間依賴關係驗證測試

### TODO 11: 建立 Demo 完整功能測試
**描述**: 建立 Demo 應用程式的完整功能測試，驗證端到端流程
**Input**: ActuatorTestController, M1 所有組件
**Output**: ActuatorFunctionalTest.java
**檔案**: `test-library-demo/src/test/java/com/jamestann/test/library/demo/ActuatorFunctionalTest.java`
**實作內容**:
- Spring Boot 測試環境啟動
- 呼叫各種測試端點 (fast, normal, slow, error)
- 檢查 `/actuator/health` 包含 Library 健康狀態
- 檢查 `/actuator/info` 包含 Library 資訊
- 檢查 `/actuator/metrics` 包含 Library SLI 指標
- 驗證 Golden Signals 四個維度數據收集

### TODO 12: 建立 SLI 數據準確性驗證測試
**描述**: 建立專門驗證 SLI 數據收集準確性的測試
**Input**: ActuatorTestController, LibrarySLICollector
**Output**: SLIDataAccuracyTest.java
**檔案**: `test-library-demo/src/test/java/com/jamestann/test/library/demo/SLIDataAccuracyTest.java`
**實作內容**:
- 發送已知數量的請求到測試端點
- 驗證請求計數準確性 (Traffic)
- 驗證錯誤率統計準確性 (Errors)
- 驗證延遲數據範圍合理性 (Latency)
- 驗證資源使用監控 (Saturation)
- 測試並發請求下的數據準確性

### TODO 13: 建立 Actuator 端點可用性測試
**描述**: 建立專門測試 Actuator 端點可用性和格式的測試
**Input**: Actuator 端點
**Output**: ActuatorEndpointsTest.java
**檔案**: `test-library-demo/src/test/java/com/jamestann/test/library/demo/ActuatorEndpointsTest.java`
**實作內容**:
- `/actuator/health` 端點可訪問性和回應格式測試
- `/actuator/info` 端點內容驗證測試
- `/actuator/metrics` 端點 Library 指標存在測試
- JSON 回應結構正確性驗證
- 端點啟用/停用配置功能測試
- 異常情況下端點穩定性測試

## 驗證標準
1. ✅ /actuator/health 顯示 Library 健康狀態
2. ✅ /actuator/info 顯示 Library 相關資訊
3. ✅ /actuator/metrics 包含 Library SLI 指標
4. ✅ AOP 攔截器自動收集所有 HTTP 請求數據
5. ✅ 支援條件化啟用/停用 Actuator 功能
6. ✅ 所有 SLI 數據準確記錄 (延遲、流量、錯誤率)
7. ✅ 與現有 Spring Boot Actuator 功能無衝突
8. ✅ 所有 M1 單元測試通過
9. ✅ Demo 整合測試成功啟動並驗證 SLI 收集
10. ✅ Actuator 端點可訪問且返回正確數據格式
11. ✅ Golden Signals 四個維度的數據都能收集
12. ✅ 異常情況下系統保持穩定
13. ✅ CI 測試套件執行時間 < 3 分鐘

## 注意事項
- M1 專注於基礎設施建立，不包含 SLO 配置功能
- 所有功能預設啟用，但可透過配置停用
- 確保與不同 Spring Boot 版本的相容性
- SLI 數據收集要考慮效能影響，避免對應用程式效能造成顯著影響
- 所有 Actuator 功能都要有適當的條件化載入機制