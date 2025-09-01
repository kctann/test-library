# 002-M2-依賴調整-TODO

## 功能名稱
Demo模組獨立化設計

## 實作項目
1. Demo POM完全獨立化，移除對Parent的繼承
2. 建立多Spring Boot版本測試profiles
3. 重新設計Demo Integration Test策略
4. 建立真實環境驗證測試
5. 更新Demo Controller支援版本資訊展示
6. 驗證Demo在不同版本組合下的運作

## 受影響的程式名稱
- `[001][專案結構建立]test-library-demo/pom.xml` - 完全獨立化，移除parent繼承
- `[001][專案結構建立]DemoController` - 新增版本資訊展示功能
- `[002][依賴調整]LibraryUserScenarioIntegrationTest` - 新建真實環境整合測試
- `[002][依賴調整]DemoVersionValidationTest` - 新建版本相容性驗證測試

## TODO List

### TODO 1: Demo POM完全獨立化
**描述**: 將test-library-demo的POM改為完全獨立，不再繼承test-library-parent
**Input**: 現有demo POM
**Output**: 獨立的demo POM
**檔案**: `test-library-demo/pom.xml`
**主要變更**:
- 移除parent對test-library-parent的繼承
- 使用spring-boot-starter-parent作為parent
- 明確指定test-library-core的版本依賴
- 設定獨立的groupId和artifactId
- 保留spring-boot-maven-plugin配置

### TODO 2: 建立多Spring Boot版本profiles
**描述**: 在Demo中建立支援不同Spring Boot版本的Maven profiles
**Input**: 獨立化後的demo POM
**Output**: 支援多版本的demo POM
**檔案**: `test-library-demo/pom.xml`
**新增profiles**:
- spring-boot-2.7: 使用Spring Boot 2.7.18
- spring-boot-3.0: 使用Spring Boot 3.0.13  
- spring-boot-3.2: 使用Spring Boot 3.2.0 (預設)
- 每個profile包含對應的parent版本和properties設定

### TODO 3: 更新Demo應用程式配置
**描述**: 調整Demo的application.yml，移除與parent相關的配置依賴
**Input**: 現有application.yml
**Output**: 獨立配置的application.yml
**檔案**: `test-library-demo/src/main/resources/application.yml`
**調整內容**:
- 確保Library配置正確
- 移除可能依賴parent的properties引用
- 新增版本檢測相關配置
- 保持Actuator和logging配置

### TODO 4: 增強DemoController版本資訊功能
**描述**: 在DemoController中新增展示Spring Boot版本和Library版本的功能
**Input**: 現有DemoController.java
**Output**: 增強版本資訊的DemoController
**檔案**: `test-library-demo/src/main/java/com/jamestann/test/library/demo/controller/DemoController.java`
**新增功能**:
- 新增/api/demo/version-info端點
- 展示當前Spring Boot版本
- 展示Library版本資訊
- 展示Java版本資訊
- 展示版本相容性狀態

### TODO 5: 建立真實環境整合測試
**描述**: 建立專門測試Library在真實用戶環境下運作的整合測試
**Input**: 無（新建測試）
**Output**: LibraryUserScenarioIntegrationTest.java
**檔案**: `test-library-demo/src/test/java/com/jamestann/test/library/demo/integration/LibraryUserScenarioIntegrationTest.java`
**測試範圍**:
- 完整Spring Boot應用啟動測試
- Library功能在真實環境的運作
- Actuator端點功能驗證
- REST API與Library整合測試
- 版本相容性在真實環境的驗證

### TODO 6: 建立版本相容性驗證測試
**描述**: 建立專門測試不同版本組合相容性的測試類別
**Input**: 無（新建測試）
**Output**: DemoVersionValidationTest.java
**檔案**: `test-library-demo/src/test/java/com/jamestann/test/library/demo/validation/DemoVersionValidationTest.java`
**測試內容**:
- 測試Demo在不同Spring Boot版本下的啟動
- 驗證Library AutoConfiguration在各版本的載入
- 測試API端點在不同版本的回應
- 驗證Actuator功能的版本相容性

### TODO 7: 建立Cross-Version測試輔助工具
**描述**: 建立輔助工具類別，用於跨版本測試的共用功能
**Input**: 無（新建工具類別）
**Output**: CrossVersionTestHelper.java
**檔案**: `test-library-demo/src/test/java/com/jamestann/test/library/demo/util/CrossVersionTestHelper.java`
**功能需求**:
- 版本資訊萃取工具
- 測試環境設定輔助方法
- 版本相容性判斷邏輯
- 測試結果比較工具

### TODO 8: 更新Demo啟動類別
**描述**: 確保Demo啟動類別在新的獨立結構下正常工作
**Input**: 現有TestLibraryDemoApplication.java
**Output**: 適配獨立結構的啟動類別
**檔案**: `test-library-demo/src/main/java/com/jamestann/test/library/demo/TestLibraryDemoApplication.java`
**檢查項目**:
- 確保@SpringBootApplication註解正確
- 驗證package掃描範圍
- 確保Library AutoConfiguration正確載入
- 新增啟動時版本資訊日誌

### TODO 9: 建立Demo專用的測試配置
**描述**: 建立Demo專用的測試配置，支援不同版本的測試環境
**Input**: 無（新建配置檔）
**Output**: 測試專用配置檔案
**檔案**: `test-library-demo/src/test/resources/application-test.yml`
**配置內容**:
- 測試環境專用的Library配置
- 簡化的Actuator配置用於測試
- 測試日誌設定
- 支援不同版本profile的配置

### TODO 10: 驗證Demo獨立編譯和運行
**描述**: 驗證Demo在完全獨立的情況下能正確編譯和運行
**Input**: 完整的獨立Demo專案
**Output**: 編譯和運行成功的驗證
**檔案**: 整個test-library-demo模組
**驗證步驟**:
- 獨立編譯: `cd test-library-demo && ../mvnw clean compile`
- 獨立測試: `cd test-library-demo && ../mvnw test`
- 獨立運行: `cd test-library-demo && ../mvnw spring-boot:run`
- 驗證所有API端點正常回應

### TODO 11: 測試多版本profile切換
**描述**: 測試Demo在不同Spring Boot版本profile下的編譯和運行
**Input**: 配置好profiles的Demo專案
**Output**: 各版本profile正常運作
**檔案**: test-library-demo模組
**測試指令**:
- `../mvnw clean test -Pspring-boot-2.7`
- `../mvnw clean test -Pspring-boot-3.0`
- `../mvnw clean test -Pspring-boot-3.2`
- 驗證每個版本都能正常編譯和測試

### TODO 12: 建立版本矩陣驗證文檔
**描述**: 建立文檔記錄Demo在不同版本組合下的測試結果
**Input**: 測試結果數據
**Output**: 版本矩陣驗證文檔
**檔案**: `test-library-demo/VERSION-MATRIX.md`
**內容包含**:
- Java版本 × Spring Boot版本的測試矩陣
- 每個組合的測試結果
- 已知問題和限制
- 推薦的版本組合建議

### TODO 13: 更新Demo的README
**描述**: 更新Demo的README檔案，說明新的獨立化架構和使用方式
**Input**: 現有README或新建
**Output**: 更新的README.md
**檔案**: `test-library-demo/README.md`
**內容包含**:
- Demo的新角色定位（驗證平台+使用範例）
- 不同版本profile的使用方式
- API端點說明和範例
- 版本相容性資訊
- 本地開發和測試指南

## 驗證標準
1. ✅ Demo POM完全獨立，不依賴Parent POM
2. ✅ 多Spring Boot版本profiles正常工作
3. ✅ Demo在各版本profile下能正確編譯
4. ✅ Demo在各版本profile下能正常運行
5. ✅ Library AutoConfiguration在Demo中正確載入
6. ✅ DemoController版本資訊功能正常
7. ✅ LibraryUserScenarioIntegrationTest通過
8. ✅ DemoVersionValidationTest通過
9. ✅ 所有API端點在不同版本下正常回應
10. ✅ Actuator功能在各版本正常運作
11. ✅ 版本矩陣測試完成並記錄
12. ✅ Demo README文檔完整更新
13. ✅ 無編譯錯誤和警告
14. ✅ Demo可作為用戶使用範例

## 注意事項
- Demo完全獨立後，要確保不影響Library Core的開發和測試
- 版本profiles要涵蓋主要的Spring Boot LTS版本
- Integration Test要真實模擬用戶使用情境，不只是技術驗證
- Demo要同時具備驗證平台和使用範例的雙重功能
- 各版本profile的測試要確保Library功能一致性
- 要考慮不同版本API差異對Library功能的影響
- Demo的獨立性要讓它能在任何支援的環境下正常運作
- 版本資訊展示要幫助用戶理解當前環境和相容性狀況
- 測試覆蓋度要確保Demo真正驗證了Library的核心功能