# 006-change fileSystem to H2 DB-SD

## 功能概述
將現有的純檔案系統存儲擴展為H2資料庫支援，採用分階段實作確保每個階段都可運行可驗證。精簡JobData介面為核心共通欄位，保持Factory模式價值，讓各來源資料保持完整性的同時支援統一操作介面。透過Converter按需轉換，提供檔案+資料庫雙模式存儲。

## 核心設計原則
1. **JobData精簡化**: 保留JobData介面但精簡為絕對共通的核心欄位
2. **完整資料保持**: 各來源資料類別（Job104Data等）保持完整結構，實作精簡的JobData介面
3. **Factory模式價值**: 保持統一的Repository創建和操作介面
4. **按需轉換**: 透過SourceType驅動的Converter實現格式轉換
5. **雙層儲存**: 檔案系統(debugging) + H2資料庫(查詢效能)
6. **分階段實作**: 每個Milestone都確保系統可運行

## Feature Description

**Given** 目前JobData介面包含太多非共通欄位導致各來源資料格式受限，純檔案系統查詢效能不佳
**When** 使用者需要保持各來源資料完整性，同時要有統一的操作介面和高效的資料庫查詢能力
**Then** 系統精簡JobData為核心共通欄位，保持Factory模式和Repository統一操作，採用按需轉換架構，提供檔案+資料庫雙模式存儲

## JobData介面重新設計

### 精簡後的核心欄位
```java
public interface JobData {
    // 核心識別（帶來源前綴）
    String getJobId();          // 格式: "104_12345", "linkedin_67890" 
    String getSourceType();     // 來源識別: "JOB_104", "LINKEDIN", "CAKEME"
    
    // 基本資訊
    String getTitle();          // 職稱
    String getCompanyId();      // 格式: "104_comp123", "linkedin_comp456"
    String getCompanyName();    // 顯示用公司名稱
    String getUrl();           // 原始URL
    
    // 元資料
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
}
```

### ID前綴配置
在PathConfig中添加來源前綴配置：
- JOB_104: "104_"
- LINKEDIN: "linkedin_"
- CAKEME: "cakeme_"

## Milestones規劃

### Milestone 1: JobData介面精簡化
**目標**: 精簡JobData介面為核心共通欄位，調整Job104Data實作新介面
**範圍**: 
- 精簡JobData介面，移除非共通欄位
- 在PathConfig添加ID前綴配置
- 調整Job104Data實作新的JobData介面
- 確保Repository和Factory正常運作
- 確保爬蟲→檔案存儲流程完全正常
**驗證點**: JobData介面精簡完成，Job104Data正確實作，104爬蟲功能正常，測試通過
**影響程式**: JobData、Job104Data、PathConfig

### Milestone 2: JobEntity設計確認
**目標**: 討論並確定JobEntity的標準化欄位設計
**範圍**: 
- 分析各來源共通欄位
- 確定標準化欄位的資料型別和格式
- 設計原始資料JSON欄位結構
**驗證點**: JobEntity設計文件確認，無程式實作
**交付物**: JobEntity設計文件

### Milestone 3: 多來源資料儲存策略設計
**目標**: 設計不同來源資料如何統一儲存到JobEntity
**範圍**:
- 分析Job104Data、未來JobLinkedInData等格式差異
- 設計原始資料JSON儲存格式
- 確定標準化欄位的映射規則
**驗證點**: 資料儲存策略文件確認
**交付物**: 多來源儲存策略文件

### Milestone 4: Reference Table設計
**目標**: 設計技能、工具、福利等參考表結構
**範圍**:
- JobSkillEntity、JobToolEntity、JobBenefitEntity等設計
- 與JobEntity的關聯關係設計  
- 標準化資料如何分拆到各參考表
**驗證點**: Reference Table設計文件確認
**交付物**: Reference Table ER圖和設計文件

### Milestone 5: H2基礎設施建立
**目標**: 建立H2資料庫連線和Entity框架
**範圍**:
- H2資料庫配置
- JobEntity和Reference Table實體建立
- JPA Repository基礎框架
**驗證點**: 資料庫連線成功，所有Entity建表成功
**影響程式**: application.properties、所有Entity類、基礎Repository

### Milestone 6: 轉換器實作
**目標**: 實作Job104Data到JobEntity的轉換邏輯
**範圍**:
- Job104ToEntityConverter實作
- 原始JSON資料序列化邏輯
- 標準化欄位映射邏輯
- Reference Table資料分拆邏輯
**驗證點**: 轉換邏輯單元測試100%通過
**影響程式**: Job104ToEntityConverter、相關測試

### Milestone 7: 雙模式儲存整合
**目標**: 整合檔案系統和H2資料庫同步儲存
**範圍**:
- DualModeRepository實作
- 同時寫入檔案和資料庫的邏輯
- 錯誤處理和回滾機制
**驗證點**: 檔案和資料庫都正確儲存，資料一致性驗證通過
**影響程式**: DualModeRepository、JobDataRepositoryFactory

### Milestone 8: 查詢功能實作
**目標**: 實作H2資料庫的查詢功能
**範圍**:
- 基本CRUD操作
- 複雜查詢(技能、薪資範圍等)
- 原始資料還原功能
**驗證點**: 查詢功能測試通過，效能符合預期
**影響程式**: JpaJobRepository、UnifiedQueryService

### Milestone 9: 資料遷移工具
**目標**: 將現有檔案資料遷移到H2資料庫
**範圍**:
- 批量讀取現有JSON檔案
- 轉換並匯入H2資料庫
- 資料完整性驗證
**驗證點**: 現有資料100%成功遷移，無資料遺漏
**影響程式**: DataMigrationService

## Update Code/New Code History (append)

### Milestone 1 (已完成 - 2025-08-28)
- NEW-[006-M1][JobData介面精簡化]JobData.java - 精簡為核心共通欄位介面
- UPDATE-[006-M1][JobData介面精簡化]Job104Data.java - 實作精簡JobData介面，新增companyId和companyName欄位
- UPDATE-[006-M1][JobData介面精簡化]PathConfig.java - 添加ID前綴配置
- UPDATE-[006-M1][JobData介面精簡化]JobDataRepository.java - 精簡為MVP版本，只包含核心方法
- UPDATE-[006-M1][JobData介面精簡化]Job104DataRepository.java - 實作MVP版Repository

### Milestone 2 (已完成 - 2025-08-28)  
- NEW-[006-M2][JobEntity設計確認]JobEntity.java - 主要Job資料表，對應8qkjj.json所有欄位
- NEW-[006-M2][JobEntity設計確認]JobCategoryRef.java - 工作分類參考表
- NEW-[006-M2][JobEntity設計確認]JobToolRef.java - 工具參考表
- NEW-[006-M2][JobEntity設計確認]JobSkillRef.java - 技能參考表
- NEW-[006-M2][JobEntity設計確認]JobLegalBenefitRef.java - 法定福利參考表
- NEW-[006-M2][JobEntity設計確認]JobOtherBenefitRef.java - 其他福利參考表
- NEW-[006-M2][JobEntity設計確認]JobCategoryMapping.java - Job與工作分類多對多關聯表
- NEW-[006-M2][JobEntity設計確認]JobToolMapping.java - Job與工具多對多關聯表
- NEW-[006-M2][JobEntity設計確認]JobSkillMapping.java - Job與技能多對多關聯表
- NEW-[006-M2][JobEntity設計確認]JobLegalBenefitMapping.java - Job與法定福利多對多關聯表
- NEW-[006-M2][JobEntity設計確認]JobOtherBenefitMapping.java - Job與其他福利多對多關聯表
- NEW-[006-M2][JobEntity設計確認]JobCompanyPhoto.java - Job與公司照片一對多關聯表

### Milestone 3 (已完成 - 2025-08-28)
- NEW-[006-M3][多來源資料儲存策略設計]ConversionException.java - 轉換過程異常封裝類別
- NEW-[006-M3][多來源資料儲存策略設計]ConversionResult.java - 轉換結果封裝類別，支援成功/失敗狀態
- NEW-[006-M3][多來源資料儲存策略設計]BaseJobDataConverter.java - Template Method模式抽象基類，定義轉換流程
- NEW-[006-M3][多來源資料儲存策略設計]ReferenceTableManager.java - 參考表統一管理器，支援快取和批量處理
- NEW-[006-M3][多來源資料儲存策略設計]JobCategoryRefRepository.java - 工作分類參考表Repository
- NEW-[006-M3][多來源資料儲存策略設計]JobToolRefRepository.java - 工具參考表Repository
- NEW-[006-M3][多來源資料儲存策略設計]JobSkillRefRepository.java - 技能參考表Repository
- NEW-[006-M3][多來源資料儲存策略設計]JobLegalBenefitRefRepository.java - 法定福利參考表Repository
- NEW-[006-M3][多來源資料儲存策略設計]JobOtherBenefitRefRepository.java - 其他福利參考表Repository
- NEW-[006-M3][多來源資料儲存策略設計]Job104DataConverter.java - 104資料具體轉換器
- NEW-[006-M3][多來源資料儲存策略設計]JobLinkedInData.java - LinkedIn測試資料結構（不同欄位格式）
- NEW-[006-M3][多來源資料儲存策略設計]JobLinkedInDataConverter.java - LinkedIn測試轉換器
- NEW-[006-M3][多來源資料儲存策略設計]ConverterFactory.java - Factory模式動態轉換器建立機制

## Update History (append)
- 2025-08-28: 重新規劃006功能，拆分為9個Milestone確保分階段安全實作
- 2025-08-28: 完成Milestone 1 - JobData介面精簡化和Repository MVP實作
- 2025-08-28: 完成Milestone 2 - JobEntity設計確認，建立完整的Entity架構和Reference Tables設計
- 2025-08-28: 完成Milestone 3 - 多來源資料儲存策略設計，建立完整的轉換器架構和驗證機制

## Milestone 2 設計總結

### Entity 架構設計
1. **JobEntity**: 主表包含8qkjj.json所有欄位，除jobId外皆可為null
2. **Reference Tables**: 5個獨立參考表，支援多Job共用和標準化管理
3. **Mapping Tables**: 5個多對多關聯表 + 1個一對多關聯表，完整支援複雜關聯查詢

### 特殊欄位處理策略
- **JSON欄位**: languageRequirements、originalJsonData 使用 @Column(columnDefinition = "JSON")
- **長文字欄位**: description、otherRequirements、benefitsDescription 使用 @Column(columnDefinition = "TEXT")
- **新增欄位**: companyUrlFromSource 支援來源網站公司頁面URL

### 資料庫索引策略
針對高頻查詢欄位建立索引：
- source_type: 來源類型查詢
- work_location: 地點篩選
- salary_min/salary_max: 薪資範圍查詢  
- experience: 經驗要求查詢
- created_at/updated_at: 時間範圍查詢

### 設計原則驗證
✅ 保留完整原始資料 (originalJsonData JSON欄位)
✅ 支援標準化查詢 (Reference Tables + Mapping Tables)  
✅ 高效能資料庫索引 (7個關鍵欄位索引)
✅ 彈性擴展架構 (Reference Tables 可跨Job共用)

## Milestone 3 設計總結

### 轉換器架構設計
1. **Template Method 模式**: BaseJobDataConverter 定義統一轉換流程，子類實現具體邏輯
2. **Factory 模式**: ConverterFactory 根據 SourceType 動態建立轉換器實例
3. **Manager 模式**: ReferenceTableManager 統一管理參考表的 CRUD 和快取
4. **Result 封裝**: ConversionResult 提供統一的成功/失敗結果處理

### 多來源支援驗證
- **Job104Data**: 原有格式，使用 Map<String,String> 結構存儲 tools/skills/benefits
- **JobLinkedInData**: 測試格式，採用不同欄位命名和巢狀物件結構
- **轉換差異**: 成功驗證不同來源資料格式的轉換能力和架構擴展性

### 技術特色
- **Spring 整合**: 充分利用依賴注入和 Bean 管理
- **快取機制**: ConverterFactory 和 ReferenceTableManager 都支援快取
- **批量處理**: ReferenceTableManager 提供批量查找和建立功能
- **異常處理**: 完整的 ConversionException 和 ConversionResult 錯誤處理

### 轉換流程
```
JobData -> ConverterFactory.getConverter() -> BaseJobDataConverter.convert()
├── validateInputData()     # 資料驗證
├── mapBasicFields()        # 基本欄位映射
├── processReferenceData()  # 參考表處理
├── serializeOriginalJson() # JSON序列化
└── validateResult()        # 結果驗證
-> ConversionResult (Success/Failure)
```

### 擴展性驗證
✅ 新增來源只需實作 Converter 和更新 ConverterFactory 映射
✅ ReferenceTableManager 統一處理所有參考表，新增類型零程式碼修改  
✅ ConversionResult 統一處理成功/失敗，支援異常鏈追蹤
✅ 測試驗證：104 和 LinkedIn 兩種不同格式成功轉換