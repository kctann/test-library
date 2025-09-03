# 🚀 純監控Stack快速啟動指南

## 一鍵啟動監控系統

### 使用自動腳本（推薦）
```powershell
# 在PowerShell中執行
.\start-monitoring.ps1
```

### 手動啟動
```powershell
# 啟動三個監控服務
docker-compose -f docker-compose-monitoring.yml up -d

# 查看服務狀態
docker-compose -f docker-compose-monitoring.yml ps

# 查看日誌（如果有問題）
docker-compose -f docker-compose-monitoring.yml logs
```

## 🎯 訪問服務

啟動成功後，你可以訪問：

| 服務 | URL | 登入資訊 | 功能 |
|------|-----|----------|------|
| **Prometheus** | http://localhost:9090 | 無需登入 | 監控數據收集和查詢 |
| **Grafana** | http://localhost:3000 | admin/admin | 數據視覺化和Dashboard |
| **AlertManager** | http://localhost:9093 | 無需登入 | 告警管理 |

## ✅ 驗證步驟

1. **Prometheus驗證**
   - 訪問 http://localhost:9090
   - 點擊"Status" > "Targets" 查看監控目標
   - 嘗試查詢：`up` 或 `prometheus_build_info`

2. **Grafana驗證**
   - 訪問 http://localhost:3000
   - 使用 admin/admin 登入
   - 檢查左側 Configuration > Data Sources
   - Prometheus數據源應該已自動配置

3. **AlertManager驗證**
   - 訪問 http://localhost:9093
   - 查看主頁面是否正常顯示

## 🛑 停止服務

```powershell
# 停止服務（保留數據）
docker-compose -f docker-compose-monitoring.yml down

# 停止並清除所有數據
docker-compose -f docker-compose-monitoring.yml down -v
```

## 🔧 故障排除

### 服務無法訪問
1. 等待1-2分鐘讓服務完全啟動
2. 檢查Docker Desktop是否運行
3. 檢查端口是否被占用：`netstat -an | findstr ":9090\|:3000\|:9093"`

### 查看詳細日誌
```powershell
# 查看所有服務日誌
docker-compose -f docker-compose-monitoring.yml logs -f

# 查看特定服務日誌
docker-compose -f docker-compose-monitoring.yml logs prometheus
docker-compose -f docker-compose-monitoring.yml logs grafana
docker-compose -f docker-compose-monitoring.yml logs alertmanager
```

### 重新啟動特定服務
```powershell
# 重啟單個服務
docker-compose -f docker-compose-monitoring.yml restart grafana
```

## 🎊 成功標準

如果看到以下內容，說明一切正常：

- ✅ Prometheus頁面顯示正常，Status > Targets 中有prometheus和alertmanager
- ✅ Grafana可以登入，數據源連接正常
- ✅ AlertManager頁面顯示正常

現在你就有一個完整的監控系統了！🎉