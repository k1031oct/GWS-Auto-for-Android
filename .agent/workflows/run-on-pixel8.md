---
description: Pixel 8エミュレータでアプリを起動
---

# Pixel 8エミュレータでアプリを起動

このワークフローは、Pixel 8エミュレータでアプリケーションを起動するための手順です。

## 手順

### 1. エミュレータを起動

// turbo
```powershell
$env:ANDROID_HOME = "C:\Users\k1031\AppData\Local\Android\Sdk"; Start-Process -FilePath "$env:ANDROID_HOME\emulator\emulator.exe" -ArgumentList "-avd", "Pixel_8" -WindowStyle Normal
```

### 2. エミュレータの起動を待機

// turbo
```powershell
$env:ANDROID_HOME = "C:\Users\k1031\AppData\Local\Android\Sdk"; & "$env:ANDROID_HOME\platform-tools\adb.exe" wait-for-device
```

### 3. アプリをビルド・インストール

// turbo
```powershell
./gradlew installDebug
```

### 4. アプリを起動

// turbo
```powershell
$env:ANDROID_HOME = "C:\Users\k1031\AppData\Local\Android\Sdk"; & "$env:ANDROID_HOME\platform-tools\adb.exe" shell am start -n com.gws.auto.mobile.android/.MainActivity
```

## 注意事項

- エミュレータが既に起動している場合は、手順1と2をスキップできます。
- アプリが既にインストールされている場合でも、手順3を実行することで最新版に更新されます。
