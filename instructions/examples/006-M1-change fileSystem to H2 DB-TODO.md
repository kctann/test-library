# 006-M1-change fileSystem to H2 DB-TODO

## 功能名稱
JobData介面精簡化

## 實作項目
1. 精簡JobData介面，移除非共通欄位，保留核心共通欄位
2. 在PathConfig中添加ID前綴配置
3. 調整Job104Data實作新的JobData介面
4. 添加companyId和companyName欄位支援
5. 確保Repository和Factory正常運作
6. 確保爬蟲功能正常運作
7. 運行測試確保無回歸問題

## 受影響的程式名稱
- `[005][refactor feature 001 with design pattern]JobData` - 精簡為核心共通欄位
- `[005][refactor feature 001 with design pattern]Job104Data` - 調整實作新的JobData介面
- `[005][refactor feature 001 with design pattern]PathConfig` - 添加ID前綴配置

## TODO List

### TODO 1: 恢復JobData介面
**描述**: 將被註解的JobData介面恢復，準備進行精簡化
**Input**: 現有被註解的JobData.java
**Output**: 恢復的JobData.java
**檔案**: `src/main/java/com/jamestann/jobhunter/model/JobData.java`

### TODO 2: 精簡JobData介面
**描述**: 移除JobData介面中的非共通欄位，保留核心共通欄位
**Input**: 現有完整的JobData介面
**Output**: 精簡後的JobData介面，只包含核心共通欄位
**檔案**: `src/main/java/com/jamestann/jobhunter/model/JobData.java`
**新增欄位**:
- `String getCompanyId()` - 公司ID，用於關聯
- `String getCompanyName()` - 公司顯示名稱
**移除欄位**: 所有非核心共通欄位（薪資、工作條件、要求條件、福利等）

### TODO 3: 在PathConfig添加ID前綴配置
**描述**: 在PathConfig中添加各來源的ID前綴配置
**Input**: 現有PathConfig.java
**Output**: 包含ID前綴配置的PathConfig
**檔案**: `src/main/java/com/jamestann/jobhunter/config/PathConfig.java`
**新增配置**:
- JOB_104_PREFIX = "104_"
- LINKEDIN_PREFIX = "linkedin_" 
- CAKEME_PREFIX = "cakeme_"

### TODO 4: 調整Job104Data實作新介面
**描述**: 修改Job104Data類別，實作精簡後的JobData介面
**Input**: 現有Job104Data.java
**Output**: 實作新JobData介面的Job104Data
**檔案**: `src/main/java/com/jamestann/jobhunter/model/Job104Data.java`
**主要變更**:
- 添加companyId和companyName欄位
- 調整jobId格式使用前綴
- 移除不再需要的介面方法實作

### TODO 5: 檢查Repository相容性
**描述**: 檢查Job104DataRepository是否與精簡後的JobData介面相容
**Input**: 現有Job104DataRepository
**Output**: 確保Repository正常運作
**檔案**: `src/main/java/com/jamestann/jobhunter/repository/Job104DataRepository.java`

### TODO 6: 檢查並修復相關依賴
**描述**: 檢查其他可能使用JobData介面的類別，確保與精簡後的介面相容
**Input**: 相關使用JobData的類別
**Output**: 確保相容性的相關類別
**檔案**: 
- Service層相關檔案
- Controller層相關檔案
- Factory相關檔案

### TODO 7: 實作ID生成邏輯
**描述**: 實作使用前綴的jobId和companyId生成邏輯
**Input**: 現有ID生成邏輯
**Output**: 使用前綴的ID生成邏輯
**檔案**: Job104Data或相關Service
**格式**: 
- jobId: "104_" + 原始ID
- companyId: "104_comp" + 公司ID

### TODO 8: 運行爬蟲測試
**描述**: 確保104爬蟲功能完全正常，檔案儲存正確，新ID格式正常運作
**Input**: 爬蟲測試案例
**Output**: 測試通過結果和正確的檔案輸出
**檔案**: 執行完整的爬蟲流程測試

### TODO 9: 運行單元測試
**描述**: 運行所有相關的單元測試，確保無回歸問題
**Input**: 現有測試案例
**Output**: 全部測試通過
**檔案**: 
- `src/test/java/com/jamestann/jobhunter/service/`
- `src/test/java/com/jamestann/jobhunter/repository/`

### TODO 10: 驗證檔案輸出和資料格式
**描述**: 驗證檔案系統的輸出結構和內容正確性，特別是新的ID格式
**Input**: 測試產生的輸出檔案
**Output**: 檔案結構、內容和ID格式驗證通過
**檔案**: 檢查 `output/104/job/` 相關目錄結構

## 驗證標準
1. ✅ JobData介面成功精簡為核心共通欄位
2. ✅ Job104Data正確實作新的JobData介面
3. ✅ ID前綴配置正確添加到PathConfig
4. ✅ jobId和companyId使用正確的前綴格式
5. ✅ Repository和Factory正常運作
6. ✅ 104爬蟲功能完全正常運作
7. ✅ 檔案正確儲存到指定位置
8. ✅ 所有單元測試通過
9. ✅ 無編譯錯誤
10. ✅ 檔案內容格式正確，包含新的ID格式
11. ✅ 系統可正常啟動和運行

## 注意事項
- 此階段重點是JobData介面精簡化，保持Factory模式價值
- 保持現有檔案系統的完整功能
- 確保Job104Data保持原有的完整資料結構，同時實作精簡的JobData介面
- 新增的companyId和companyName要正確對應
- ID格式要使用正確的前綴
- 不涉及資料庫相關開發