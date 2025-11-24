# GWS-Auto-for-Android アプリのログを表示
# このスクリプトは、logcatから本アプリのログのみをフィルタリングして表示します。

Write-Host "アプリのログを監視しています（Ctrl+C で停止）..." -ForegroundColor Cyan
Write-Host "パッケージ: com.gws.auto.mobile.android" -ForegroundColor Yellow
Write-Host ""

# ANDROID_HOME環境変数を設定してadbコマンドを実行
$env:ANDROID_HOME = "C:\Users\k1031\AppData\Local\Android\Sdk"
& "$env:ANDROID_HOME\platform-tools\adb.exe" logcat -v time | Select-String "com.gws.auto.mobile.android"
