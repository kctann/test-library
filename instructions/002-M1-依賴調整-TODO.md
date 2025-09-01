# 002-M1-依賴調整-TODO

## 功能名稱
Library Core依賴管理重構

## 實作項目
1. 移除Parent POM對spring-boot-starter-parent的繼承
2. 建立BOM依賴管理機制
3. 調整Spring Boot依賴scope為provided
4. 新增版本相容性檢測機制
5. 建立多Java版本支援profiles
6. 更新Core模組Integration Test策略

## 受影響的程式名稱
- `[001][專案結構建立]Parent POM` - 移除spring-boot-starter-parent繼承，改用BOM
- `[001][專案結構建立]test-library-core/pom.xml` - 調整依賴scope為provided
- `[002][依賴調整]SpringBootVersionDetector` - 新增版本檢測工具類
- `[001][專案結構建立]TestLibraryAutoConfiguration` - 更新支援多版本相容
- `[001][專案結構建立]LibraryInternalIntegrationTest` - 更新Integration Test策略

## TODO List

### TODO 1: 重構Parent POM依賴管理
**描述**: 移除對spring-boot-starter-parent的繼承，改用spring-boot-dependencies BOM管理
**Input**: 現有Parent POM (pom.xml)
**Output**: 使用BOM管理的Parent POM
**檔案**: `pom.xml`
**主要變更**:
- 移除parent標籤中的spring-boot-starter-parent
- 在dependencyManagement中引入spring-boot-dependencies BOM
- 保留現有的多Java版本profiles配置
- 確保Maven properties正確設定

### TODO 2: 調整Core依賴Scope
**描述**: 將test-library-core中所有Spring Boot相關依賴的scope改為provided
**Input**: 現有Core模組POM
**Output**: 使用provided scope的Core POM
**檔案**: `test-library-core/pom.xml`
**調整依賴**:
- spring-boot-starter → provided scope
- spring-boot-starter-actuator → provided scope  
- spring-boot-starter-aop → provided scope
- spring-boot-autoconfigure → provided scope
- spring-boot-configuration-processor → optional + provided scope

### TODO 3: 建立版本檢測機制
**描述**: 新增SpringBootVersionDetector工具類，用於檢測和驗證Spring Boot版本相容性
**Input**: 無（新建類別）
**Output**: SpringBootVersionDetector.java
**檔案**: `test-library-core/src/main/java/com/jamestann/test/library/util/SpringBootVersionDetector.java`
**功能需求**:
- detectSpringBootVersion() 方法檢測當前Spring Boot版本
- isCompatibleVersion(String version) 驗證版本是否在支援範圍內
- getSupportedVersions() 回傳支援的版本清單
- 支援範圍: Spring Boot 2.7.x, 3.0.x, 3.2.x
- 提供詳細的版本相容性資訊

### TODO 4: 更新TestLibraryAutoConfiguration
**描述**: 更新AutoConfiguration以支援多版本相容性，添加版本檢查邏輯
**Input**: 現有TestLibraryAutoConfiguration.java
**Output**: 支援多版本的AutoConfiguration
**檔案**: `test-library-core/src/main/java/com/jamestann/test/library/config/TestLibraryAutoConfiguration.java`
**主要變更**:
- 注入SpringBootVersionDetector
- 在初始化時進行版本相容性檢查
- 添加版本不相容時的警告日誌
- 確保使用向下相容的Spring Boot API
- 添加條件配置支援不同版本

### TODO 5: 更新TestLibraryManager
**描述**: 增強TestLibraryManager以支援版本資訊管理和相容性檢查
**Input**: 現有TestLibraryManager.java
**Output**: 增強版的TestLibraryManager
**檔案**: `test-library-core/src/main/java/com/jamestann/test/library/config/TestLibraryManager.java`
**新增功能**:
- 整合SpringBootVersionDetector
- 提供版本相容性查詢方法
- 在afterPropertiesSet中加入版本檢查
- 添加版本資訊到日誌輸出

### TODO 6: 建立Core Integration Test
**描述**: 建立專門測試Library內部組件整合的測試類別
**Input**: 無（新建測試）
**Output**: LibraryInternalIntegrationTest.java
**檔案**: `test-library-core/src/test/java/com/jamestann/test/library/integration/LibraryInternalIntegrationTest.java`
**測試範圍**:
- AutoConfiguration是否正確載入所有組件
- Properties binding是否正常工作
- TestLibraryManager初始化是否成功
- 版本檢測機制是否正常運作
- 各組件間的依賴注入是否正確

### TODO 7: 更新JavaVersionCompatibilityTest ⚠️ **依賴M3**
**描述**: 擴展現有的Java版本相容性測試，實作真正的多版本相容性驗證
**依賴**: 需要M3的智能CI Workflows完成後才能實作有意義的相容性測試
**真正的相容性測試策略**:
- 透過CI Matrix Strategy設定不同Spring Boot版本組合
- 讓現有Integration Test在不同版本環境下執行
- 觀察測試通過率判斷實際相容性
- 非透過程式碼邏輯判斷版本字串(沒有實際意義)
**檔案**: CI配置檔案 + 現有測試檔案
**狀態**: 🔄 **延後至M3階段實作**

### TODO 8: 建立Maven Compiler Plugin配置
**描述**: 確保Maven compiler plugin在新的POM結構下正常工作，支援多版本編譯
**Input**: Parent POM中的compiler plugin配置
**Output**: 適配新結構的compiler配置
**檔案**: `pom.xml` (Parent)
**配置重點**:
- 確保-parameters編譯參數正確設定（Spring Boot需要）
- 驗證多Java版本profiles的compiler配置
- 確保annotation processing正常工作
- 設定適當的compiler版本和target

### TODO 9: 驗證Library打包結果
**描述**: 驗證Library在新的依賴結構下能正確打包為jar，且不包含provided依賴
**Input**: 打包後的jar檔案
**Output**: 驗證通過的jar檔案結構
**檔案**: `test-library-core/target/test-library-core-1.0.0-SNAPSHOT.jar`
**驗證項目**:
- jar中不包含Spring Boot相關類別（provided scope）
- 包含Library自己的所有類別
- META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports存在
- jar可以被外部專案正常引用
- jar大小合理（不包含不必要的依賴）

### TODO 10: 測試多版本編譯相容性
**描述**: 使用不同Java版本profiles測試Library編譯，確保相容性
**Input**: 完整的Library程式碼
**Output**: 各Java版本編譯成功
**檔案**: 整個專案
**測試指令**:
- `./mvnw clean compile -Pjava11`
- `./mvnw clean compile -Pjava17`  
- `./mvnw clean compile -Pjava21`
- 驗證編譯結果和產生的字節碼版本

### TODO 11: 建立provided依賴測試環境
**描述**: 確保測試環境中provided依賴可以正常使用
**Input**: 現有test dependencies
**Output**: 正確配置的測試依賴
**檔案**: `test-library-core/pom.xml`
**配置要求**:
- 測試環境中Spring Boot依賴使用test scope
- 確保測試時依賴可用，打包時不包含
- 驗證測試和打包行為的一致性

### TODO 12: 建立本地驗證測試
**描述**: 建立簡單的本地驗證專案，模擬用戶使用Library的情境
**Input**: 無（新建測試專案結構）
**Output**: 本地驗證測試
**檔案**: `test-library-core/src/test/java/com/jamestann/test/library/local/LocalValidationTest.java`
**驗證情境**:
- 模擬用戶在自己的Spring Boot專案中引用Library
- 測試AutoConfiguration能否正常載入
- 驗證provided依賴由用戶專案提供時的行為
- 測試Library功能在模擬用戶環境的正確性

## 驗證標準
1. ✅ Parent POM成功移除spring-boot-starter-parent繼承
2. ✅ BOM依賴管理機制正常工作
3. ✅ Core模組所有Spring Boot依賴使用provided scope
4. ✅ SpringBootVersionDetector正確檢測版本相容性
5. ✅ TestLibraryAutoConfiguration支援多版本相容
6. ✅ TestLibraryManager整合版本檢測功能
7. ✅ LibraryInternalIntegrationTest通過
8. ✅ JavaVersionCompatibilityTest新增測試通過  
9. ✅ Library jar打包結果正確（不包含provided依賴）
10. ✅ 多Java版本profiles編譯成功
11. ✅ 測試環境provided依賴配置正確
12. ✅ 本地驗證測試成功模擬用戶使用情境
13. ✅ 無編譯錯誤和警告
14. ✅ AutoConfiguration在新結構下正常工作

## 注意事項
- 此階段重點是Library Core的依賴獨立性，不影響Demo模組
- 必須確保Library不會強制用戶使用特定Spring Boot版本
- provided scope依賴必須在測試環境中可用（test scope依賴）
- 版本相容性檢測要涵蓋主要的LTS版本(2.7.x, 3.0.x, 3.2.x)
- 保持向下相容性，使用Spring Boot最低版本支援的API
- 新的Integration Test專注於Library內部組件整合，不測試外部環境
- 確保META-INF/spring配置檔案正確，支援Spring Boot 3.x的新格式
- 版本檢測機制要提供清楚的錯誤訊息，幫助用戶理解相容性問題