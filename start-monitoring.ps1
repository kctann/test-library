# 啟動純監控Stack的PowerShell腳本
Write-Host "🚀 Starting Test Library Monitoring Stack..." -ForegroundColor Green
Write-Host "===========================================" -ForegroundColor Cyan

# 檢查Docker是否運行
Write-Host "📋 Checking Docker status..." -ForegroundColor Yellow
$dockerRunning = docker info 2>$null
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Error: Docker is not running. Please start Docker Desktop." -ForegroundColor Red
    Write-Host "💡 Steps to fix:" -ForegroundColor Yellow
    Write-Host "   1. Open Docker Desktop" -ForegroundColor White
    Write-Host "   2. Wait for 'Engine running' status" -ForegroundColor White
    Write-Host "   3. Try again" -ForegroundColor White
    exit 1
}
Write-Host "✅ Docker is running!" -ForegroundColor Green

# 檢查端口是否被佔用
Write-Host "📋 Checking port availability..." -ForegroundColor Yellow
$ports = @(9090, 3000, 9093)
$portsInUse = @()

foreach ($port in $ports) {
    $portCheck = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue
    if ($portCheck) {
        $portsInUse += $port
    }
}

if ($portsInUse.Count -gt 0) {
    Write-Host "⚠️  Warning: The following ports are in use: $($portsInUse -join ', ')" -ForegroundColor Yellow
    Write-Host "💡 You may need to stop other services or change ports" -ForegroundColor Yellow
    $continue = Read-Host "Continue anyway? (y/N)"
    if ($continue -ne 'y' -and $continue -ne 'Y') {
        exit 1
    }
}

# 清理可能的舊容器
Write-Host "🧹 Cleaning up any existing containers..." -ForegroundColor Yellow
docker-compose -f docker-compose-monitoring.yml down -v 2>$null

# 啟動監控Stack
Write-Host "🚀 Starting monitoring services..." -ForegroundColor Yellow
docker-compose -f docker-compose-monitoring.yml up -d

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Error: Failed to start services." -ForegroundColor Red
    Write-Host "🔍 Checking logs..." -ForegroundColor Yellow
    docker-compose -f docker-compose-monitoring.yml logs
    exit 1
}

# 等待服務啟動
Write-Host "⏳ Waiting for services to start (30 seconds)..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

# 檢查服務狀態
Write-Host "📊 Checking service status..." -ForegroundColor Yellow
docker-compose -f docker-compose-monitoring.yml ps

# 測試服務訪問
Write-Host "🔍 Testing service endpoints..." -ForegroundColor Yellow

# 測試Prometheus
try {
    $prometheusResponse = Invoke-WebRequest -Uri "http://localhost:9090" -Method Get -TimeoutSec 10
    Write-Host "✅ Prometheus: http://localhost:9090 - Status: $($prometheusResponse.StatusCode)" -ForegroundColor Green
} catch {
    Write-Host "⚠️  Prometheus: http://localhost:9090 - Not ready yet (may still be starting)" -ForegroundColor Yellow
}

# 測試Grafana
try {
    $grafanaResponse = Invoke-WebRequest -Uri "http://localhost:3000" -Method Get -TimeoutSec 10
    Write-Host "✅ Grafana: http://localhost:3000 - Status: $($grafanaResponse.StatusCode)" -ForegroundColor Green
} catch {
    Write-Host "⚠️  Grafana: http://localhost:3000 - Not ready yet (may still be starting)" -ForegroundColor Yellow
}

# 測試AlertManager
try {
    $alertmanagerResponse = Invoke-WebRequest -Uri "http://localhost:9093" -Method Get -TimeoutSec 10
    Write-Host "✅ AlertManager: http://localhost:9093 - Status: $($alertmanagerResponse.StatusCode)" -ForegroundColor Green
} catch {
    Write-Host "⚠️  AlertManager: http://localhost:9093 - Not ready yet (may still be starting)" -ForegroundColor Yellow
}

Write-Host "`n🎉 Monitoring Stack Started Successfully!" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "📊 Prometheus:   http://localhost:9090" -ForegroundColor Cyan
Write-Host "📈 Grafana:      http://localhost:3000 (admin/admin)" -ForegroundColor Cyan
Write-Host "🚨 AlertManager: http://localhost:9093" -ForegroundColor Cyan
Write-Host "`n💡 Tips:" -ForegroundColor Yellow
Write-Host "   • Services might take 1-2 minutes to fully start" -ForegroundColor White
Write-Host "   • Grafana login: admin/admin" -ForegroundColor White
Write-Host "   • If a service isn't ready, wait a bit and refresh" -ForegroundColor White
Write-Host "   • Use 'docker-compose -f docker-compose-monitoring.yml logs [service]' to check logs" -ForegroundColor White

Write-Host "`n🛑 To stop services later:" -ForegroundColor Red
Write-Host "   docker-compose -f docker-compose-monitoring.yml down" -ForegroundColor White