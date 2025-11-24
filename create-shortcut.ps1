# デスクトップにview-app-logsのショートカットを作成
# このショートカットにキーボードショートカットを割り当てることができます

$scriptPath = Join-Path $PSScriptRoot "view-app-logs.ps1"
$desktopPath = [Environment]::GetFolderPath("Desktop")
$shortcutPath = Join-Path $desktopPath "アプリログ表示.lnk"

# WScriptShellオブジェクトを作成
$WshShell = New-Object -ComObject WScript.Shell
$Shortcut = $WshShell.CreateShortcut($shortcutPath)

# ショートカットのプロパティを設定
$Shortcut.TargetPath = "powershell.exe"
$Shortcut.Arguments = "-ExecutionPolicy Bypass -NoProfile -File `"$scriptPath`""
$Shortcut.WorkingDirectory = $PSScriptRoot
$Shortcut.IconLocation = "powershell.exe,0"
$Shortcut.Description = "GWS-Auto-for-Androidアプリのログを表示"

# ショートカットを保存
$Shortcut.Save()

Write-Host "✅ ショートカットを作成しました: $shortcutPath" -ForegroundColor Green
Write-Host ""
Write-Host "📌 ショートカットキーを割り当てる手順:" -ForegroundColor Cyan
Write-Host "  1. デスクトップの「アプリログ表示」ショートカットを右クリック" -ForegroundColor Yellow
Write-Host "  2. [プロパティ] を選択" -ForegroundColor Yellow
Write-Host "  3. [ショートカット] タブの「ショートカットキー」欄をクリック" -ForegroundColor Yellow
Write-Host "  4. 任意のキーを押す（例: Ctrl+Alt+L）" -ForegroundColor Yellow
Write-Host "  5. [OK] をクリック" -ForegroundColor Yellow
Write-Host ""
Write-Host "💡 推奨キー: Ctrl+Alt+L (L = Logs)" -ForegroundColor Magenta
