# [001][專案結構建立]-TODO.md

## featureName
專案結構建立

## 條列實作項目
- Multi-Module Maven專案架構建立
- Library核心模組建立(test-library-core)
- Demo測試模組建立(test-library-demo)
- Spring Boot Auto Configuration機制建立
- 測試架構建立
- 專案文件結構建立

## 條列受影響的程式名稱
### Multi-Module架構
- UPDATE-[001][專案結構建立]pom.xml - 重構為Parent POM
- NEW-[001][專案結構建立]test-library-core/pom.xml - Library模組POM
- NEW-[001][專案結構建立]test-library-demo/pom.xml - Demo模組POM

### Library核心模組(test-library-core)
- DELETE-[001][專案結構建立]TestLibraryApplication.java - 移除主類別
- NEW-[001][專案結構建立]TestLibraryAutoConfiguration.java - Auto Configuration
- NEW-[001][專案結構建立]TestLibraryProperties.java - 配置屬性類別
- NEW-[001][專案結構建立]TestLibraryManager.java - Library管理類別
- NEW-[001][專案結構建立]org.springframework.boot.autoconfigure.AutoConfiguration.imports - Auto Configuration檔案
- NEW-[001][專案結構建立]TestLibraryAutoConfigurationTest.java - Auto Configuration測試

### Demo測試模組(test-library-demo)
- NEW-[001][專案結構建立]TestLibraryDemoApplication.java - Demo主應用程式
- NEW-[001][專案結構建立]DemoController.java - 測試API Controller
- NEW-[001][專案結構建立]application.yml - Demo配置檔案
- NEW-[001][專案結構建立]TestLibraryDemoApplicationTest.java - Demo測試類別

### 測試架構
- MOVE-[001][專案結構建立]BaseUnitTest.java - 移至test-library-core
- MOVE-[001][專案結構建立]BaseIntegrationTest.java - 移至test-library-core
- MOVE-[001][專案結構建立]BasePerformanceTest.java - 移至test-library-core
- MOVE-[001][專案結構建立]BaseMatrixTest.java - 移至test-library-core
- NEW-[001][專案結構建立]application-test.yml - 測試配置檔案

## TODO list：順序條列要完成的工作

### M1階段：Multi-Module Maven專案架構
1. **建立Parent POM**
   - Input: 多模組專案管理需求
   - Output: Parent POM管理子模組版本和依賴
   - 檔案名稱: pom.xml
   - 簡單描述: 重構為Parent POM，統一管理子模組

2. **建立子模組目錄結構**
   - Input: Library和Demo分離需求
   - Output: test-library-core/和test-library-demo/目錄
   - 檔案名稱: 目錄結構
   - 簡單描述: 建立Multi-Module標準目錄結構

### M2階段：Library核心模組(test-library-core)
3. **建立Library模組POM**
   - Input: Library依賴需求，無SpringBoot plugin
   - Output: test-library-core/pom.xml，純JAR打包
   - 檔案名稱: test-library-core/pom.xml
   - 簡單描述: Library模組POM，不包含executable jar配置

4. **移除應用程式主類別，建立Auto Configuration**
   - Input: Library架構需求，Spring Boot Auto Configuration規範
   - Output: 
     - 刪除TestLibraryApplication.java
     - 新增TestLibraryAutoConfiguration.java
     - 新增TestLibraryProperties.java
     - 新增TestLibraryManager.java
   - 檔案名稱: 
     - src/main/java/com/jamestann/test/library/config/TestLibraryAutoConfiguration.java
     - src/main/java/com/jamestann/test/library/config/TestLibraryProperties.java
     - src/main/java/com/jamestann/test/library/config/TestLibraryManager.java
   - 簡單描述: 建立Library Auto Configuration機制，讓其他專案自動配置

5. **建立Auto Configuration導入檔案**
   - Input: Spring Boot Auto Configuration機制需求
   - Output: META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
   - 檔案名稱: src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
   - 簡單描述: 讓Spring Boot自動偵測並載入Library配置

### M3階段：Demo測試模組(test-library-demo)
6. **建立Demo模組POM**
   - Input: Web應用程式需求，引用Library依賴
   - Output: test-library-demo/pom.xml，包含SpringBoot plugin
   - 檔案名稱: test-library-demo/pom.xml
   - 簡單描述: Demo應用程式POM，引用Library作為依賴

7. **建立Demo應用程式**
   - Input: 測試Library功能需求
   - Output: 
     - TestLibraryDemoApplication.java (主類別)
     - DemoController.java (測試API)
     - application.yml (配置檔案)
   - 檔案名稱:
     - src/main/java/com/jamestann/test/library/demo/TestLibraryDemoApplication.java
     - src/main/java/com/jamestann/test/library/demo/controller/DemoController.java
     - src/main/resources/application.yml
   - 簡單描述: 建立完整的Web應用程式來測試Library功能

### M4階段：測試架構整合
8. **移動和更新測試結構**
   - Input: 分模組測試需求
   - Output: 
     - Library模組：Auto Configuration測試
     - Demo模組：集成測試
     - 保持原有的多層次測試架構
   - 檔案名稱:
     - test-library-core/src/test/java/com/jamestann/test/library/config/TestLibraryAutoConfigurationTest.java
     - test-library-demo/src/test/java/com/jamestann/test/library/demo/TestLibraryDemoApplicationTest.java
   - 簡單描述: 適應Multi-Module架構的測試結構

### M5階段：文件結構更新
9. **更新專案文件**
   - Input: 新架構變更紀錄需求
   - Output: 更新SD.md和TODO.md反映新架構
   - 檔案名稱:
     - instructions/001-專案結構建立-SD.md
     - instructions/001-專案結構建立-TODO.md
   - 簡單描述: 文件同步更新，記錄架構重構過程