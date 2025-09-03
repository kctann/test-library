# 003-M1-修復Actuator設計問題-TODO

## 問題描述

當前的監控設計採用 HTTP 攔截 + 路徑清理模式，存在以下根本問題：

### 設計問題
1. **過度自動化**: 嘗試監控所有 HTTP 請求，導致不必要的複雜性
2. **錯誤的抽象層級**: 在 HTTP 層而非業務邏輯層處理監控
3. **路徑參數處理**: 使用複雜的正規表達式猜測路徑參數，容易出錯
4. **高基數指標風險**: 每個不同的路徑參數都可能產生新的指標標籤

### 與業界標準的差距
業界標準監控配置方式：
- YAML 配置指定監控的 package
- `@ExcludeMonitoring` 排除特定方法
- `@IncludeMonitoring` 明確包含特定方法
- 基於業務語意的指標命名

## 重構目標

將監控系統改為符合業界標準的配置驅動 + 註解控制模式：

1. **Package-based 配置**: 透過 YAML 指定要監控的 package
2. **註解精確控制**: 使用註解包含/排除特定方法
3. **業務語意指標**: 用戶定義有意義的指標名稱
4. **AOP 實現**: 基於 Spring AOP 而非 HTTP 攔截器

## 重構計劃

### Phase 1: 新增註解系統
- [ ] **TODO 1.1**: 創建 `@IncludeMonitoring` 註解
  - 支持自定義指標名稱
  - 支持描述信息
  - 支持標籤配置
- [ ] **TODO 1.2**: 創建 `@ExcludeMonitoring` 註解
  - 支持方法級和類級排除
  - 提供排除原因說明

### Phase 2: 重新設計配置系統
- [ ] **TODO 2.1**: 重構 `LibraryActuatorProperties`
  - 移除 `PathFilters` 配置
  - 添加 `MonitoringPackages` 配置
  - 添加預設排除 package 列表
- [ ] **TODO 2.2**: 創建 `MonitoringConfiguration` 類
  - 處理 package-based 監控邏輯
  - 提供註解掃描功能
  - 配置 AOP pointcuts

### Phase 3: 重構核心監控邏輯  
- [ ] **TODO 3.1**: 重構 `LibrarySLICollector`
  - 移除 `sanitizeEndpoint()` 方法
  - 移除 `sanitizeClassName()` 方法
  - 改為接受用戶定義的指標名稱
  - 簡化指標創建邏輯
- [ ] **TODO 3.2**: 重構 `LibraryMetricsAspect`
  - 改為基於 `@IncludeMonitoring` 註解的切面
  - 添加基於 package 配置的切面
  - 處理 `@ExcludeMonitoring` 排除邏輯
- [ ] **TODO 3.3**: 簡化 `LibraryMonitoringInterceptor`
  - 移除複雜的路徑處理邏輯
  - 改為可選的基礎 HTTP 統計收集
  - 或考慮完全移除

### Phase 4: 更新配置和測試
- [ ] **TODO 4.1**: 更新 `LibraryActuatorAutoConfiguration`
  - 移除 HTTP interceptor 註冊（或簡化）
  - 註冊新的監控配置
  - 添加條件化載入邏輯
- [ ] **TODO 4.2**: 重寫監控相關測試
  - 重寫 `LibrarySLICollectorTest` 移除路徑清理測試
  - 重寫 `LibraryMetricsAspectTest` 基於新註解
  - 添加 `MonitoringConfigurationTest`
  - 更新整合測試
- [ ] **TODO 4.3**: 創建使用範例
  - 在 demo 模組中添加使用範例
  - 展示 package 配置方式
  - 展示註解使用方式

## 配置範例

### 新的 YAML 配置
```yaml
test-library:
  actuator:
    monitoring:
      # Package-based 監控配置
      include-packages:
        - "com.example.api.controller"
        - "com.example.service"
      exclude-packages:
        - "com.example.internal"
        - "com.example.test"
      # 預設排除的 endpoints
      default-excludes:
        - "/actuator/**"
        - "/error"
```

### 註解使用範例
```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @GetMapping("/{id}")
    @IncludeMonitoring(
        name = "user.get",
        description = "Get user by ID",
        tags = {"operation=read", "resource=user"}
    )
    public User getUser(@PathVariable String id) {
        return userService.getUser(id);
    }
    
    @GetMapping("/health")
    @ExcludeMonitoring(reason = "Internal health check")
    public String health() {
        return "OK";
    }
}
```

## 預期效果

### 用戶體驗改善
1. **簡化配置**: 用戶只需在 YAML 中指定要監控的 package
2. **精確控制**: 用戶可以精確控制哪些方法需要監控
3. **有意義的指標**: 指標名稱反映業務邏輯而非技術實現

### 技術改善
1. **移除複雜性**: 不再需要路徑參數猜測和清理
2. **防止高基數**: 用戶明確定義指標名稱，避免動態生成
3. **符合業界標準**: 採用常見的監控配置模式

### 可維護性提升
1. **清晰的職責**: 配置、註解、AOP 各司其職
2. **易於測試**: 每個組件都有明確的邊界
3. **擴展性**: 容易添加新的監控特性

## 風險評估

### 破壞性變更
- 這是一個架構級的重構，會對現有使用者造成破壞性變更
- 需要提供遷移指南和向後兼容考慮

### 工作量
- 需要重寫大部分核心邏輯
- 所有相關測試需要重新設計
- 文檔需要全面更新

### 建議
- 考慮將此重構作為新的主版本發布
- 提供清楚的遷移路徑文檔
- 在 demo 中展示最佳實踐用法