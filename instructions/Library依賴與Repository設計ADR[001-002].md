# Library依賴與Repository設計ADR[001-002]

## 決策背景 (Context)

### 觸發點
在001專案結構建立到002依賴調整的開發過程中，發現了Library架構設計的關鍵問題：
- 從單一Spring Boot應用轉換為Multi-Module Library專案時，暴露出依賴管理的設計缺陷
- 發現Library與Demo的角色定位不明確，導致測試責任混淆
- 意識到當前架構會強制用戶使用特定Spring Boot版本，違背Library設計原則

### Pilot專案特性
根據CLAUDE.md更新內容，此專案是「測試Library開發的Pilot原型」，需要考慮更廣泛的Library開發模式，避免解決方案只適用於目前的功能範圍。

### 核心議題
1. **依賴鎖定問題**：Library繼承spring-boot-starter-parent導致版本強制綁定
2. **測試責任混淆**：Integration Test應該在Core還是Demo中進行？
3. **用戶使用限制**：用戶無法在自己選擇的Spring Boot版本下使用Library
4. **Repository架構選擇**：Monorepo vs Multi-repo的利弊權衡
5. **CI管控複雜度**：如何實現智能化的CI觸發機制

## 面臨的問題 (Problem Statement)

### 技術問題
- **版本綁定**：Library強制用戶使用Spring Boot 3.2.0，限制了用戶的技術棧選擇
- **依賴衝突**：用戶專案可能與Library的Spring Boot版本產生衝突
- **API相容性**：Library使用Spring Boot 3.x特有API，無法在2.7.x環境運行

### 架構問題  
- **角色定位不清**：Demo既不是純測試工具，也不是純使用範例
- **測試層次混亂**：缺乏清晰的內部整合vs外部整合測試分工
- **CI資源浪費**：無法根據修改範圍智能觸發對應測試

## 考慮的選項 (Options Considered)

### 選項A：維持現狀 (Parent POM + 版本綁定)
**優點**：
- 開發簡單，配置統一
- 版本管理集中化

**缺點**：
- 強制用戶使用特定Spring Boot版本
- 違背Library設計的解耦原則
- 限制Library的適用範圍

### 選項B：完全分離 (Multi-repo + 獨立版本管理)
**優點**：
- Core和Demo完全獨立開發
- 各自獨立的CI/CD流程
- 權限管理更清晰

**缺點**：
- 版本同步複雜
- 開發體驗不一致
- CI依賴關係難以管控

### 選項C：Monorepo + 智能依賴管理 (最終選擇)
**優點**：
- 保持開發同步性
- 智能CI觸發優化資源使用
- 版本一致性易於控制
- 支援用戶版本選擇彈性

**缺點**：
- 初期實作複雜度較高
- 需要建立更完善的測試架構

## 決策結果 (Decision)

### Core與Demo角色重新定義

#### **Library Core新角色**：
- **職責**：提供Library核心功能，不強制用戶技術棧
- **依賴策略**：使用provided scope，讓用戶環境提供Spring Boot依賴
- **測試責任**：專注於Library內部組件整合測試
- **版本策略**：編譯到Java 11字節碼，支援2.7.x/3.0.x/3.2.x版本

#### **Demo模組新角色**：
- **職責**：真實用戶環境模擬平台 + 使用範例展示
- **依賴策略**：完全獨立，使用自己選擇的Spring Boot版本
- **測試責任**：承擔真實環境整合測試和版本相容性驗證
- **驗證功能**：多版本組合測試，確保Library在各環境正常工作

### 依賴管理架構重新設計

#### **技術實作策略**：
```xml
<!-- Core: 不綁定版本，使用BOM管理 -->
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>3.2.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<!-- Core: 依賴改為provided scope -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
    <scope>provided</scope>
</dependency>

<!-- Demo: 完全獨立的parent -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.7.18</version> <!-- 可切換測試版本 -->
</parent>
```

### Repository與CI架構選擇

#### **Monorepo + 智能CI策略**：
```yaml
# 智能CI觸發邏輯
Core修改 → Core CI (快速反饋)
Demo修改 → Demo CI (場景驗證)  
Core+Demo修改 → 完整CI (全面驗證)
發布前 → 矩陣CI (版本相容性)
```

### 多層級測試策略

#### **測試責任重新分工**：
- **Core Integration Test**：Library內部組件整合、Mock環境測試、API相容性驗證
- **Demo Integration Test**：完整Spring Boot應用整合、真實業務場景、跨版本相容性驗證

## 決策理由 (Rationale)

### 依賴設計的核心理由

#### **問題根源**：
Library繼承spring-boot-starter-parent造成版本強制綁定，這違反了Library vs Application的本質差異。

#### **解決邏輯**：
使用provided scope讓依賴版本由用戶環境決定，這是Library設計的核心原則——Library不應該控制用戶的技術棧選擇。

#### **重要性**：
這個決策確保了Library的通用性和靈活性，讓用戶可以在Java 11+Spring Boot 2.7.x環境正常使用Library，而不需要被迫升級到Spring Boot 3.2.0。

### 多層級測試的必要性

#### **Core Integration Test存在理由**：
驗證Library內部組件整合正確性，提供快速反饋，幫助開發者及時發現內部組件問題。

#### **Demo Integration Test存在理由**：
驗證Library在真實用戶環境的可用性，確保沒有隱藏的相容性問題，這是Core內部測試無法涵蓋的。

#### **兩者不可互相取代**：
- 內部整合測試無法發現環境相容性問題
- 用戶環境測試無法精確定位內部組件問題
- 兩者結合提供完整的測試覆蓋

### Monorepo選擇的策略考量

#### **同步開發需求**：
Pilot專案需要Core與Demo緊密配合驗證架構設計，分離repository會增加同步成本和複雜度。

#### **版本管理複雜度**：
Multi-repo需要額外機制確保Core更新後Demo及時同步測試，而Monorepo天然解決了這個問題。

#### **CI依賴關係**：
智能觸發可以精確控制測試範圍，比分離repository的webhook機制更可靠和高效。

#### **Pilot特性考量**：
作為原型驗證專案，Monorepo便於快速實驗和迭代，待架構穩定後再考慮是否分離。

## 影響分析 (Consequences)

### 正面影響

#### **用戶體驗大幅提升**：
- 用戶可以在任何支援版本組合（Java 11-21 × Spring Boot 2.7/3.0/3.2）下使用Library
- 不會因為Library要求而被迫升級Spring Boot版本
- Library真正做到與用戶技術棧解耦

#### **Library品質保證增強**：
- Demo成為真正的驗證平台，確保Library在各種環境正常工作
- 雙層測試架構提供更完整的功能驗證
- 版本矩陣測試確保相容性承諾的可靠性

#### **開發流程自動化**：
- CI流程更智能化，減少不必要的測試執行
- 根據修改範圍自動調整測試策略
- 提高開發效率，縮短反饋周期

#### **架構擴展性提升**：
- 為未來支援更多Spring Boot版本奠定基礎
- Demo平台可以輕易擴展測試更多版本組合
- CI架構支援漸進式功能增強

### 負面影響/風險

#### **初期實作複雜度增加**：
- 需要重構現有的POM結構
- 建立版本檢測和相容性機制
- 設計和實作智能CI workflows

#### **測試維護成本上升**：
- 需要維護多個版本組合的測試
- 版本相容性問題的診斷和修復成本
- CI執行時間可能增加（版本矩陣測試）

#### **學習曲線**：
- 團隊需要學習新的開發流程
- 理解provided scope和BOM管理的概念
- 掌握智能CI觸發機制的使用

#### **維護複雜度**：
- 需要持續關注支援版本的相容性
- 新Spring Boot版本發布時的適配工作
- CI配置和邏輯的維護成本

## 實作概要 (Implementation Summary)

### 第一階段：Library Core依賴重構
- 重構Parent POM，移除spring-boot-starter-parent繼承
- 調整Core依賴scope，建立BOM管理機制
- 新增SpringBootVersionDetector版本檢測工具
- 建立Core內部整合測試架構

### 第二階段：Demo模組獨立化
- Demo完全獨立，支援多版本測試profiles
- 建立Demo真實環境整合測試
- 重新設計Demo為驗證平台和使用範例
- 實作跨版本相容性驗證機制

### 第三階段：智能CI建置
- 建立Core專用、Demo專用CI workflows
- 實作智能路徑觸發邏輯
- 建立版本矩陣測試和發布前驗證
- 整合完整的CI自動化流程

### 驗證標準
- 用戶可以在任何支援版本組合下正常使用Library
- Demo能夠成功驗證Library在各種環境的功能
- CI流程智能觸發，提供高效的開發反饋
- 版本相容性測試涵蓋所有支援的組合

## 後續考量

### 監控和評估
- 定期評估支援版本範圍的合理性
- 監控CI執行效率和資源使用情況
- 收集用戶對版本相容性的反饋

### 持續改進
- 根據使用情況調整CI觸發策略
- 優化測試執行時間和覆蓋度平衡
- 考慮引入更多自動化工具提升開發體驗

### 擴展規劃
- 評估是否需要支援更多Spring Boot版本
- 考慮是否從Monorepo遷移到Multi-repo
- 規劃更進階的CI功能（參考[temp][002]CI optional.md）

---

**決策日期**：2025-09-01  
**決策參與者**：James (專案負責人)  
**決策狀態**：已確認，開始實作  
**後續檢視日期**：完成002功能開發後進行效果評估