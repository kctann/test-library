# Test Library End-to-End 配置手冊

## 概述

本手冊提供Test Library的完整配置指南，包括Actuator監控、SLI數據收集、AOP監控，以及Prometheus + Docker整合的完整配置。

## 1. 快速開始配置

### 1.1 Maven依賴配置

在你的Spring Boot應用程式中添加依賴：

```xml
<dependency>
    <groupId>com.jamestann</groupId>
    <artifactId>test-library-core</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### 1.2 基本配置 (application.yml)

```yaml
# 基本Library配置
test:
  library:
    enabled: true
    library-name: your-app-name
    performance-monitoring-enabled: true
    logging-standardization-enabled: true
    actuator:
      custom-endpoints-enabled: true
      endpoint-path-prefix: test-library

# Test Library Actuator完整配置
test-library:
  actuator:
    # 總開關
    enabled: true
    
    # Health Check配置
    health:
      enabled: true
    
    # Info Contributor配置  
    info:
      enabled: true
      
    # SLI Metrics配置
    metrics:
      enabled: true
      include-percentiles: true
      percentiles: [0.5, 0.75, 0.95, 0.99]
    
    # AOP監控配置
    aop:
      enabled: true
      
    # 監控配置
    monitoring:
      enabled: true
      # HTTP攔截器 (可選，建議使用AOP)
      http-interceptor:
        enabled: false
      
      # 包級別監控配置
      include-packages:
        - "com.yourcompany.service.**"
        - "com.yourcompany.controller.**"
      exclude-packages:
        - "com.yourcompany.internal.**"
        - "com.yourcompany.test.**"
        
      # Spring stereotype監控
      monitor-rest-controllers: true
      monitor-controllers: true  
      monitor-services: true
      monitor-repositories: true
      
      # 監控選項
      include-parameters: false
      include-return-type: false

# Spring Boot Actuator配置
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
      base-path: /actuator
  endpoint:
    health:
      show-details: always
    metrics:
      enabled: true
    prometheus:
      enabled: true
  metrics:
    export:
      prometheus:
        enabled: true
  info:
    env:
      enabled: true
```

## 2. 監控功能詳細配置

### 2.1 Health Check配置

Library提供客戶端應用程式的健康檢查：

```yaml
test-library:
  actuator:
    health:
      enabled: true
```

訪問: `GET /actuator/health`

示例響應：
```json
{
  "status": "UP",
  "components": {
    "library": {
      "status": "UP",
      "details": {
        "applicationName": "your-app",
        "libraryEnabled": true,
        "monitoringEnabled": true,
        "sliCollection": {
          "enabled": true,
          "percentiles": true,
          "packageMonitoring": true
        }
      }
    }
  }
}
```

### 2.2 Info Contributor配置

提供應用程式和Library資訊：

```yaml
test-library:
  actuator:
    info:
      enabled: true
```

訪問: `GET /actuator/info`

### 2.3 SLI Metrics配置 (Golden Signals)

收集Latency、Traffic、Errors、Saturation四大指標：

```yaml
test-library:
  actuator:
    metrics:
      enabled: true
      include-percentiles: true
      percentiles: [0.5, 0.75, 0.95, 0.99]
```

**生成的Metrics：**
- `application.http.request.duration` - HTTP請求延遲 (Latency)
- `application.http.request.total` - HTTP請求總數 (Traffic)  
- `application.http.request.errors` - HTTP錯誤總數 (Errors)
- `application.http.request.active` - 當前活躍請求數 (Saturation)
- `application.method.*` - 自定義方法監控

### 2.4 AOP監控配置

#### 包級別監控
```yaml
test-library:
  actuator:
    monitoring:
      include-packages:
        - "com.yourcompany.service.**"
        - "com.yourcompany.controller.**"
      exclude-packages:
        - "com.yourcompany.internal.**"
      monitor-rest-controllers: true
      monitor-services: true
```

#### 註解級別監控
使用`@IncludeMonitoring`註解進行精確控制：

```java
@RestController
public class UserController {
    
    @GetMapping("/users/{id}")
    @IncludeMonitoring(
        name = "user.get",
        description = "Get user by ID",
        tags = {"operation=read", "entity=user"}
    )
    public User getUser(@PathVariable Long id) {
        return userService.findById(id);
    }
    
    @PostMapping("/users")  
    @IncludeMonitoring(
        name = "user.create",
        description = "Create new user",
        tags = {"operation=write", "entity=user"},
        includeParameters = true
    )
    public User createUser(@RequestBody User user) {
        return userService.save(user);
    }
}
```

#### 排除監控
使用`@ExcludeMonitoring`註解排除不需要監控的方法：

```java
@Service
public class UserService {
    
    @ExcludeMonitoring(reason = "Internal utility method")
    private void validateUser(User user) {
        // 內部工具方法，不需要監控
    }
}
```

## 3. Docker + Prometheus 整合配置

### 3.1 docker-compose.yml

```yaml
version: '3.8'
services:
  # 你的Spring Boot應用程式
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    networks:
      - monitoring

  # Prometheus
  prometheus:
    image: prom/prometheus:latest
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
      - prometheus_data:/prometheus
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.path=/prometheus'
      - '--web.console.libraries=/etc/prometheus/console_libraries'
      - '--web.console.templates=/etc/prometheus/consoles'
      - '--storage.tsdb.retention.time=200h'
      - '--web.enable-lifecycle'
    networks:
      - monitoring

  # Grafana  
  grafana:
    image: grafana/grafana:latest
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin
    volumes:
      - grafana_data:/var/lib/grafana
      - ./grafana/dashboards:/etc/grafana/provisioning/dashboards
      - ./grafana/datasources:/etc/grafana/provisioning/datasources
    networks:
      - monitoring

volumes:
  prometheus_data:
  grafana_data:

networks:
  monitoring:
    driver: bridge
```

### 3.2 prometheus.yml

```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

rule_files:
  # - "first_rules.yml"
  # - "second_rules.yml"

scrape_configs:
  - job_name: 'prometheus'
    static_configs:
      - targets: ['localhost:9090']

  # Spring Boot應用程式metrics抓取
  - job_name: 'spring-boot-app'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 5s
    static_configs:
      - targets: ['app:8080']
    scrape_configs:
      - job_name: 'test-library-metrics'
        metrics_path: '/actuator/prometheus'
        static_configs:
          - targets: ['app:8080']
        metric_relabel_configs:
          # 重新標記application metrics
          - source_labels: [__name__]
            regex: 'application_(.*)'
            target_label: 'test_library_metric'
            replacement: '${1}'
```

### 3.3 Grafana Dashboard配置

創建`grafana/dashboards/dashboard.yml`：

```yaml
apiVersion: 1

providers:
  - name: 'default'
    orgId: 1
    folder: ''
    type: file
    disableDeletion: false
    editable: true
    options:
      path: /etc/grafana/provisioning/dashboards
```

創建`grafana/datasources/datasource.yml`：

```yaml
apiVersion: 1

datasources:
  - name: Prometheus
    type: prometheus
    access: proxy
    url: http://prometheus:9090
    isDefault: true
```

### 3.4 Golden Signals Dashboard範例

創建`grafana/dashboards/golden-signals.json`：

```json
{
  "dashboard": {
    "id": null,
    "title": "Test Library - Golden Signals",
    "tags": ["test-library", "golden-signals"],
    "timezone": "browser",
    "panels": [
      {
        "title": "Request Latency (P99)",
        "type": "stat",
        "targets": [
          {
            "expr": "application_http_request_duration{quantile=\"0.99\"}",
            "refId": "A"
          }
        ]
      },
      {
        "title": "Request Rate (QPS)",
        "type": "graph",
        "targets": [
          {
            "expr": "rate(application_http_request_total[5m])",
            "refId": "A"
          }
        ]
      },
      {
        "title": "Error Rate",
        "type": "graph", 
        "targets": [
          {
            "expr": "rate(application_http_request_errors[5m])",
            "refId": "A"
          }
        ]
      },
      {
        "title": "Active Requests (Saturation)",
        "type": "graph",
        "targets": [
          {
            "expr": "application_http_request_active",
            "refId": "A"
          }
        ]
      }
    ],
    "time": {
      "from": "now-1h",
      "to": "now"
    },
    "refresh": "5s"
  }
}
```

## 4. 測試和驗證

### 4.1 啟動完整監控stack

```bash
# 1. 啟動應用程式
mvn spring-boot:run

# 2. 啟動監控stack
docker-compose up -d

# 3. 訪問各個服務
# 應用程式: http://localhost:8080
# Prometheus: http://localhost:9090  
# Grafana: http://localhost:3000 (admin/admin)
```

### 4.2 驗證監控功能

1. **檢查Health**: `GET http://localhost:8080/actuator/health`
2. **檢查Info**: `GET http://localhost:8080/actuator/info` 
3. **檢查Metrics**: `GET http://localhost:8080/actuator/metrics`
4. **檢查Prometheus**: `GET http://localhost:8080/actuator/prometheus`

### 4.3 常見問題排除

**Q: 為什麼沒有看到自定義metrics？**
A: 確認AOP配置已啟用，且方法有@IncludeMonitoring註解或符合包配置。

**Q: Prometheus抓取不到數據？**
A: 檢查網路連接和metrics端點是否暴露。

**Q: 性能影響如何？**
A: Library使用異步收集，性能影響極小。可通過配置調整收集頻率。

## 5. 進階配置

### 5.1 自定義Health Indicator

```java
@Component
@ConditionalOnProperty("test-library.actuator.health.custom.enabled")
public class CustomHealthIndicator implements HealthIndicator {
    
    @Override
    public Health health() {
        // 自定義健康檢查邏輯
        return Health.up()
            .withDetail("custom", "OK")
            .build();
    }
}
```

### 5.2 自定義Metrics

```java
@Service
public class CustomMetricsService {
    
    private final MeterRegistry meterRegistry;
    private final Counter customCounter;
    
    public CustomMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.customCounter = Counter.builder("custom.operations")
            .description("Custom operations counter")
            .register(meterRegistry);
    }
    
    @IncludeMonitoring(
        name = "custom.operation",
        description = "Custom business operation",
        tags = {"type=business"}
    )
    public void performOperation() {
        customCounter.increment();
        // 業務邏輯
    }
}
```

## 6. 快速測試指令

### 6.1 啟動完整環境

```bash
# 1. 構建項目
mvn clean package -DskipTests

# 2. 啟動監控stack (包含應用程式)
docker-compose up -d

# 3. 等待所有服務啟動完成
docker-compose ps

# 4. 檢查健康狀態
curl http://localhost:8080/actuator/health
```

### 6.2 測試各種功能

#### 基本監控測試
```bash
# AOP監控測試
curl "http://localhost:8080/api/monitoring/basic"
curl "http://localhost:8080/api/monitoring/business/user123?operation=create"
curl "http://localhost:8080/api/monitoring/fast"
curl "http://localhost:8080/api/monitoring/slow"

# 異常測試 (驗證Error tracking)
curl "http://localhost:8080/api/monitoring/error?errorType=runtime"
curl "http://localhost:8080/api/monitoring/error?errorType=business"

# 批量測試 (驗證高頻監控)
curl -X POST "http://localhost:8080/api/monitoring/batch?count=50"
```

#### 性能測試
```bash
# CPU密集型測試
curl "http://localhost:8080/api/performance/cpu-intensive?complexity=2000"

# Memory密集型測試
curl "http://localhost:8080/api/performance/memory-intensive?sizeMB=20"

# I/O密集型測試
curl "http://localhost:8080/api/performance/io-intensive?operations=200"

# 併發測試
curl "http://localhost:8080/api/performance/concurrent?threads=10"

# 混合負載測試
curl "http://localhost:8080/api/performance/mixed-load"

# 系統資源查看
curl "http://localhost:8080/api/performance/resources"
```

#### Health Check測試
```bash
# 查看整體健康狀態
curl "http://localhost:8080/api/health/status"

# 健康檢查性能測試
curl "http://localhost:8080/api/health/performance?iterations=50"

# 模擬健康狀態變更
curl -X POST "http://localhost:8080/api/health/simulate/database?healthy=false"
curl -X POST "http://localhost:8080/api/health/simulate/external?healthy=false"

# 重置健康狀態
curl -X POST "http://localhost:8080/api/health/reset"

# 查看健康統計
curl "http://localhost:8080/api/health/statistics"
```

#### Metrics測試
```bash
# Golden Signals測試
curl "http://localhost:8080/api/metrics/latency-test?delayMs=500"
curl "http://localhost:8080/api/metrics/traffic-test"
curl "http://localhost:8080/api/metrics/error-test?errorType=runtime&errorRate=0.3"
curl "http://localhost:8080/api/metrics/saturation-test?load=75"

# 自定義Metrics
curl -X POST "http://localhost:8080/api/metrics/custom?name=test_counter&type=counter&value=1"
curl -X POST "http://localhost:8080/api/metrics/custom?name=test_gauge&type=gauge&value=42"

# 批量Metrics測試
curl -X POST "http://localhost:8080/api/metrics/batch-test?operations=500"

# 查看Metrics統計
curl "http://localhost:8080/api/metrics/stats"
curl "http://localhost:8080/api/metrics/stats?metricName=demo.custom.counter"

# 列出所有Metrics
curl "http://localhost:8080/api/metrics/list"
curl "http://localhost:8080/api/metrics/list?prefix=application"
```

### 6.3 監控數據查看

#### Spring Boot Actuator端點
```bash
# 健康檢查
curl "http://localhost:8080/actuator/health"

# 應用資訊
curl "http://localhost:8080/actuator/info"

# Metrics端點
curl "http://localhost:8080/actuator/metrics"
curl "http://localhost:8080/actuator/metrics/application.http.request.duration"

# Prometheus格式metrics
curl "http://localhost:8080/actuator/prometheus"
```

#### 監控系統訪問
- **Prometheus**: http://localhost:9090
  - 查詢示例: `application_http_request_duration`
  - 查看告警: http://localhost:9090/alerts
  - 查看配置: http://localhost:9090/config

- **Grafana**: http://localhost:3000 (admin/admin)
  - Golden Signals Dashboard已預配置
  - 可視化所有監控指標

- **AlertManager**: http://localhost:9093
  - 查看當前告警
  - 告警規則管理

### 6.4 壓力測試腳本

創建`load-test.sh`腳本進行壓力測試：

```bash
#!/bin/bash
echo "Starting load test for Test Library..."

# 並行執行多種請求
for i in {1..100}; do
  {
    curl -s "http://localhost:8080/api/monitoring/basic" > /dev/null
    curl -s "http://localhost:8080/api/metrics/traffic-test" > /dev/null
    curl -s "http://localhost:8080/api/performance/fast" > /dev/null
  } &
  
  if (( i % 20 == 0 )); then
    echo "Completed $i requests..."
    sleep 1
  fi
done

wait
echo "Load test completed. Check Grafana dashboard for results."
```

### 6.5 驗證清單

完整測試後，驗證以下項目：

- [ ] 應用程式正常啟動 (`/actuator/health` 返回 UP)
- [ ] Prometheus正常收集數據 (http://localhost:9090)
- [ ] Grafana Dashboard顯示數據 (http://localhost:3000)
- [ ] AOP監控正常工作 (有`application.method.*`指標)
- [ ] Golden Signals數據收集正常
  - [ ] Latency: `application_http_request_duration`
  - [ ] Traffic: `application_http_request_total`
  - [ ] Errors: `application_http_request_errors`
  - [ ] Saturation: `application_http_request_active`
- [ ] 自定義Metrics正常創建和更新
- [ ] Health Indicators正常工作
- [ ] 告警規則正常觸發 (可測試異常情況)

## 7. 生產環境建議

### 7.1 性能調優
- 合理配置percentiles，避免過多百分位數
- 使用包級別配置減少不必要的監控
- 定期清理過期metrics

### 7.2 安全考慮  
- 限制actuator端點的網路訪問
- 使用Spring Security保護敏感端點
- 配置適當的日誌級別

### 7.3 監控告警
- 配置Prometheus AlertManager
- 設置SLI/SLO告警規則
- 建立運維監控面板

## 8. 常見問題

**Q: 為什麼Docker啟動失敗？**
A: 確保已執行`mvn clean package`構建JAR文件，並檢查端口8080、9090、3000是否被佔用。

**Q: Prometheus無法抓取數據？**
A: 檢查應用程式是否啟動，`/actuator/prometheus`端點是否可訪問。

**Q: Grafana Dashboard沒有數據？**
A: 確認Prometheus數據源配置正確，並等待數據收集（約1-2分鐘）。

**Q: AOP監控沒有生效？**
A: 確認`test-library.actuator.aop.enabled=true`且方法有@IncludeMonitoring註解或符合包配置。

---

本手冊提供了Test Library的完整End-to-End配置和測試方法。通過這些測試，你可以全面驗證Library的所有監控功能。