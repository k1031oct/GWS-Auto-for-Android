---
description: Pixel 8 (実機) でアプリを更新・起動
---

# Pixel 8 (実機) でアプリを更新・起動

## 手順

### 1. アプリをビルド・インストール・起動
実機が接続されている状態で実行してください。

// turbo
```powershell
$env:ANDROID_HOME = "C:\Users\k1031\AppData\Local\Android\Sdk"; 
./gradlew installDebug; 
& "$env:ANDROID_HOME\platform-tools\adb.exe" shell am start -n com.gws.auto.mobile.android/.MainActivity
```
