---
description: アプリのlogcatログを確認
---

# アプリのlogcatログを確認

このワークフローは、GWS-Auto-for-Androidアプリのログのみをlogcatから表示します。

## 手順

### 1. アプリのログをリアルタイムで表示

// turbo
```powershell
$env:ANDROID_HOME = "C:\Users\k1031\AppData\Local\Android\Sdk"; & "$env:ANDROID_HOME\platform-tools\adb.exe" logcat -v time | Select-String "com.gws.auto.mobile.android"
```

このコマンドは、`com.gws.auto.mobile.android`パッケージに関連するログのみを表示します。

### 2. ログをクリアしてから表示（推奨）

// turbo
```powershell
$env:ANDROID_HOME = "C:\Users\k1031\AppData\Local\Android\Sdk"; & "$env:ANDROID_HOME\platform-tools\adb.exe" logcat -c; & "$env:ANDROID_HOME\platform-tools\adb.exe" logcat -v time | Select-String "com.gws.auto.mobile.android"
```

古いログをクリアしてから、新しいログのみを表示します。

## オプション

### エラーログのみを表示

```powershell
$env:ANDROID_HOME = "C:\Users\k1031\AppData\Local\Android\Sdk"; & "$env:ANDROID_HOME\platform-tools\adb.exe" logcat -v time *:E | Select-String "com.gws.auto.mobile.android"
```

### ログをファイルに保存

```powershell
$env:ANDROID_HOME = "C:\Users\k1031\AppData\Local\Android\Sdk"; & "$env:ANDROID_HOME\platform-tools\adb.exe" logcat -v time | Select-String "com.gws.auto.mobile.android" | Out-File -FilePath "app_log.txt"
```

## 注意事項

- ログは `Ctrl+C` で停止できます
- エミュレータまたは実機が接続されている必要があります
- ログが大量に流れる場合は、エラーログのみの表示を推奨します
