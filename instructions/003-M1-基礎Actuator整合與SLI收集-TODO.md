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

### TODO 8: 建立 M1 測試案例
**描述**: 建立 M1 功能的完整測試套件
**Input**: 所有 M1 實作類別
**Output**: 測試類別
**檔案**: 
- `src/test/java/com/jamestann/test/library/actuator/LibraryActuatorAutoConfigurationTest.java`
- `src/test/java/com/jamestann/test/library/actuator/LibraryHealthIndicatorTest.java`
- `src/test/java/com/jamestann/test/library/actuator/LibrarySLICollectorTest.java`
**實作內容**:
- 自動配置載入測試
- Health Check 回應測試
- SLI 數據收集準確性測試
- Actuator 端點可用性測試

## 驗證標準
1. ✅ /actuator/health 顯示 Library 健康狀態
2. ✅ /actuator/info 顯示 Library 相關資訊
3. ✅ /actuator/metrics 包含 Library SLI 指標
4. ✅ AOP 攔截器自動收集所有 HTTP 請求數據
5. ✅ 支援條件化啟用/停用 Actuator 功能
6. ✅ 所有 SLI 數據準確記錄 (延遲、流量、錯誤率)
7. ✅ 與現有 Spring Boot Actuator 功能無衝突
8. ✅ 測試覆蓋率達到 80% 以上

## 注意事項
- M1 專注於基礎設施建立，不包含 SLO 配置功能
- 所有功能預設啟用，但可透過配置停用
- 確保與不同 Spring Boot 版本的相容性
- SLI 數據收集要考慮效能影響，避免對應用程式效能造成顯著影響
- 所有 Actuator 功能都要有適當的條件化載入機制