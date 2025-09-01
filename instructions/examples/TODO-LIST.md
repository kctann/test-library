# TODO-LIST.md

## 程式中尚未完成的 TODO 項目

### Milestone 6-7: 雙模式儲存整合相關

#### Mapping 實體建立和保存
- [006][M3][多來源資料儲存策略設計]Job104DataConverter#processJobCategories depends on [006][M6-M7][雙模式儲存整合]
- [006][M3][多來源資料儲存策略設計]Job104DataConverter#processTools depends on [006][M6-M7][雙模式儲存整合] 
- [006][M3][多來源資料儲存策略設計]Job104DataConverter#processSkills depends on [006][M6-M7][雙模式儲存整合]
- [006][M3][多來源資料儲存策略設計]Job104DataConverter#processLegalBenefits depends on [006][M6-M7][雙模式儲存整合]
- [006][M3][多來源資料儲存策略設計]Job104DataConverter#processOtherBenefits depends on [006][M6-M7][雙模式儲存整合]
- [006][M3][多來源資料儲存策略設計]Job104DataConverter#processCompanyPhotos depends on [006][M6-M7][雙模式儲存整合]

#### LinkedIn 轉換器相關
- [006][M3][多來源資料儲存策略設計]JobLinkedInDataConverter#processSkillSet depends on [006][M6-M7][雙模式儲存整合]
- [006][M3][多來源資料儲存策略設計]JobLinkedInDataConverter#processJobFunctions depends on [006][M6-M7][雙模式儲存整合]
- [006][M3][多來源資料儲存策略設計]JobLinkedInDataConverter#processBenefits depends on [006][M6-M7][雙模式儲存整合]
- [006][M3][多來源資料儲存策略設計]JobLinkedInDataConverter#processCompanyImages depends on [006][M6-M7][雙模式儲存整合]

### 未來擴展計劃

#### 其他來源爬蟲策略
- [001][爬蟲系統]JobCrawlerFactory#getJobCrawlerStrategy depends on [Future][LinkedIn爬蟲實作]
- [001][爬蟲系統]JobCrawlerFactory#getJobCrawlerStrategy depends on [Future][CakeMe爬蟲實作]

#### 其他來源資料倉庫
- [001][資料存取層]JobDataRepositoryFactory#getJobDataRepository depends on [Future][LinkedIn資料倉庫實作]
- [001][資料存取層]JobDataRepositoryFactory#getJobDataRepository depends on [Future][CakeMe資料倉庫實作]

### 外部整合

#### 政府 API 整合
- [002][工作分析服務]JobAnalysisService#analyzeJobContent depends on [Future][政府API整合]

## TODO 項目分類統計

### 緊急 (Milestone 6-7 需要)
- **Mapping 實體建立**: 10 個項目
- **範圍**: 轉換器無法完整保存關聯資料

### 中期 (未來 Milestone)
- **多來源支援**: 4 個項目  
- **範圍**: LinkedIn, CakeMe 爬蟲和資料倉庫

### 長期 (外部整合)
- **政府 API**: 1 個項目
- **範圍**: 薪資行情、公司評價等外部資料

## 移除指南

當完成相關 TODO 項目時，請根據以下標記移除對應條目：
- **Mapping 相關**: 在實作 Mapping 實體保存邏輯時移除
- **爬蟲相關**: 在實作對應來源爬蟲時移除  
- **API 相關**: 在整合政府 API 時移除