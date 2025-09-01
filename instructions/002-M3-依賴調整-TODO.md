# 002-M3-依賴調整-TODO

## 功能名稱
智能CI Workflows設計

## 實作項目
1. 建立Core專用CI pipeline
2. 建立Demo驗證CI pipeline
3. 實作智能路徑觸發機制
4. 建立版本矩陣測試workflows
5. 建立發布前完整驗證Workflow

## 受影響的程式名稱
- `[002][依賴調整].github/workflows/core-ci.yml` - 新建Core專用CI流程
- `[002][依賴調整].github/workflows/demo-ci.yml` - 新建Demo驗證CI流程
- `[002][依賴調整].github/workflows/smart-ci.yml` - 新建智能觸發CI流程
- `[002][依賴調整].github/workflows/matrix-ci.yml` - 新建版本矩陣測試流程
- `[002][依賴調整].github/workflows/release-validation.yml` - 新建發布前驗證流程

## TODO List

### TODO 1: 建立Core專用CI Pipeline
**描述**: 建立專門針對Library Core的CI流程，當Core相關檔案變更時觸發
**Input**: 無（新建workflow）
**Output**: core-ci.yml
**檔案**: `.github/workflows/core-ci.yml`
**流程設計**:
- 觸發條件: test-library-core/** 和 pom.xml 檔案變更
- 支援多Java版本測試 (11, 17, 21)
- 執行Core單元測試和整合測試
- 驗證Library打包結果
- 快速反饋機制（優化執行時間）

### TODO 2: 建立Demo驗證CI Pipeline
**描述**: 建立專門針對Demo模組的CI流程，當Demo相關檔案變更時觸發
**Input**: 無（新建workflow）
**Output**: demo-ci.yml
**檔案**: `.github/workflows/demo-ci.yml`
**流程設計**:
- 觸發條件: test-library-demo/** 檔案變更
- 同時觸發當 test-library-core/** 變更時（依賴關係）
- 支援多Spring Boot版本測試 (2.7, 3.0, 3.2)
- 執行真實環境整合測試
- 驗證API端點功能

### TODO 3: 實作智能路徑觸發機制
**描述**: 建立智能CI流程，能根據變更的檔案路徑自動決定執行範圍
**Input**: 無（新建workflow）
**Output**: smart-ci.yml
**檔案**: `.github/workflows/smart-ci.yml`
**智能邏輯**:
- 使用 dorny/paths-filter action 檢測檔案變更
- 根據變更路徑決定執行 Core CI 或 Demo CI
- 支援文檔變更時跳過測試（docs-only模式）
- 提供變更摘要和執行決策資訊

### TODO 4: 建立版本矩陣測試Workflow
**描述**: 建立完整的版本矩陣測試，用於發布前的全面驗證
**Input**: 無（新建workflow）
**Output**: matrix-ci.yml
**檔案**: `.github/workflows/matrix-ci.yml`
**矩陣設計**:
- Java版本: [11, 17, 21]
- Spring Boot版本: [2.7.18, 3.0.13, 3.2.0]
- 測試Core在各Java版本的編譯
- 測試Demo在各版本組合的運行
- 生成相容性測試報告

### TODO 5: 建立發布前完整驗證Workflow
**描述**: 建立發布前的完整驗證流程，確保所有功能正常
**Input**: 無（新建workflow）
**Output**: release-validation.yml
**檔案**: `.github/workflows/release-validation.yml`
**驗證流程**:
- 完整編譯所有模組
- 執行完整測試套件
- 驗證Library打包結果
- 檢查版本相容性
- 生成測試報告
- 僅在手動觸發或tag推送時執行

## 驗證標準
1. ✅ Core CI workflow正確觸發和執行
2. ✅ Demo CI workflow正確觸發和執行  
3. ✅ 智能路徑觸發邏輯正確運作
4. ✅ 版本矩陣測試完整覆蓋所有組合
5. ✅ Release validation workflow正常運作
6. ✅ 所有workflow執行時間在合理範圍內
7. ✅ CI流程提供清楚的測試結果反饋
8. ✅ 整體CI架構滿足Pilot專案需求

## 注意事項
- 此為Pilot專案，CI設計著重於核心功能驗證，避免過度複雜化
- 版本矩陣測試不要過於頻繁執行，考慮GitHub Actions資源限制
- 智能觸發要確保不遺漏重要的測試情境
- CI執行時間要平衡測試完整性和開發效率
- 所有workflow要提供清楚的執行日誌和錯誤資訊
- 保持CI架構的簡潔性，便於後續擴展和維護