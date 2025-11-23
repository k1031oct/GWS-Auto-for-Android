---
description: Firebase App Distributionにデプロイ
---

# Firebase App Distributionにデプロイ

このワークフローは、アプリケーションをFirebase App Distribution（テスター向け）にデプロイするための手順です。

> [!NOTE]
> このワークフローは、Android Studioの「Upload to Firebase」実行構成から引き継がれています。

## 前提条件

以下の環境変数が設定されている必要があります：

### 必須環境変数

1. **FIREBASE_APP_ID**
   - 値: `1:46549959857:android:7f3e4d6965318171d74e90`
   - 説明: Firebase App DistributionのアプリID

2. **FIREBASE_TOKEN** (オプション)
   - 説明: Firebase認証トークン（CI/CD環境で使用）
   - 取得方法: `firebase login:ci`

### 環境変数の設定方法

#### PowerShellで一時的に設定（現在のセッションのみ）

```powershell
$env:FIREBASE_APP_ID = "1:46549959857:android:7f3e4d6965318171d74e90"
```

#### 永続的に設定（ユーザー環境変数）

```powershell
[System.Environment]::SetEnvironmentVariable("FIREBASE_APP_ID", "1:46549959857:android:7f3e4d6965318171d74e90", "User")
```

認証トークンを設定する場合（オプション）：
```powershell
[System.Environment]::SetEnvironmentVariable("FIREBASE_TOKEN", "your-token-here", "User")
```

## 手順

### 方法1: Android Studioと同じ手順（推奨）

// turbo
```powershell
$env:FIREBASE_APP_ID = "1:46549959857:android:7f3e4d6965318171d74e90"; ./gradlew assembleDebug appDistributionUploadDebug
```

このコマンドは以下を実行します：
1. `assembleDebug` - Debugビルドのアセンブル
2. `appDistributionUploadDebug` - Firebase App Distributionへのアップロードとtestersグループへの配信

### 方法2: 簡略版（環境変数が既に設定されている場合）

// turbo
```powershell
./gradlew appDistributionUploadDebug
```

`appDistributionUploadDebug`タスクは自動的に`assembleDebug`に依存しているため、ビルドも自動実行されます。

## 配信設定

- **配信グループ**: `testers`
- **アーティファクトタイプ**: APK
- **ビルドタイプ**: Debug

## 注意事項

- 初回実行時は、Firebaseへの認証が必要になる場合があります
- ビルドとアップロードには数分かかる場合があります
- アップロード後、testersグループのメンバーに通知が送信されます
- 環境変数が設定されていない場合、`build.gradle.kts`の設定により`appId must not be null`エラーが発生します

## トラブルシューティング

### FIREBASE_APP_IDが設定されていない場合

**エラーメッセージ**: `appId must not be null`

**解決方法**: 上記の「環境変数の設定方法」セクションを参照して環境変数を設定してください。

### 認証エラーが発生する場合

Firebase CLIでログインしてください：
```powershell
firebase login
```

CI/CD環境の場合は、認証トークンを取得して環境変数に設定：
```powershell
firebase login:ci
```
