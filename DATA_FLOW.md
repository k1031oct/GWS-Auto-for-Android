# Tsunagu - Data Flow & Logic Architecture

このドキュメントは、Tsunaguアプリケーションにおけるデータフローとロジックアーキテクチャを詳細に記述します。

---

## 1. 認証 (Authentication)

### 1.1 Google Sign-In フロー

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **UI: AppSettingsFragment.signIn()** | None | GoogleSignInClientを起動 | Intent | |
| └─ `begin SignInActivityForResult` | `signInIntent` | ユーザーにアカウント選択を促す | ActivityResult | |
| **AppSettingsFragment.handleSignInResult** | `result: ActivityResult` | サインイン結果の処理 | Unit | Null安全性チェック |
| ├─ `GoogleSignIn.getSignedInAccountFromIntent` | `intent` | GoogleSignInAccountの取得 | `Task<GoogleSignInAccount>` | 例外処理 |
| └─ **MainSharedViewModel.setSignedInStatus** | `isSignedIn: Boolean` | UI状態の更新 | StateFlow | |
| **GoogleApiAuthorizer.isSignedIn** | None | サインイン状態の確認 | `Boolean` | |
| └─ `GoogleSignIn.getLastSignedInAccount` | `context` | キャッシュから取得 | `GoogleSignInAccount?` | Nullチェック |

### 1.2 Google API 認証

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **GoogleApiAuthorizer.getCredential** | `scopes: List<String>` | OAuth2資格情報の取得 | `GoogleAccountCredential?` | |
| ├─ Validation | `isSignedIn` | サインイン状態確認 | Boolean | 未サインイン時はnull |
| ├─ `GoogleAccountCredential.usingOAuth2` | `context, scopes` | 資格情報の生成 | Credential | |
| └─ `credential.selectedAccount` | `account` | アカウントの設定 | Unit | アカウント有効性 |

---

## 2. ワークフロー管理 (Workflow Management)

### 2.1 ワークフロー一覧取得

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **WorkflowRepository.getAllWorkflows** | `userId: String` | Firestoreからワークフロー取得 | `Flow<List<Workflow>>` | |
| └─ `Firestore.collection("workflows")` | `.whereEqualTo("userId", userId)` | クエリ実行 | QuerySnapshot | インデックス確認 |
| \u0026nbsp;\u0026nbsp;└─ `Document.toObject<Workflow>()` | `document` | Firestoreドキュメント→Kotlinオブジェクト変換 | Workflow | データ型の一致 |
| **WorkflowViewModel.loadWorkflows** | None | UIへのデータ提供 | `StateFlow<List<Workflow>>` | |
| └─ `repository.getAllWorkflows` | `currentUserId` | Repository呼び出し | Flow | ユーザーID存在確認 |

### 2.2 ワークフロー保存

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **WorkflowRepository.saveWorkflow** | `workflow: Workflow` | ワークフローの保存 | `Result<Unit>` | |
| ├─ Validation | `workflow` | `name.isNotBlank()` | Boolean | 必須項目チェック |
| ├─ Validation | `workflow.modules` | `modules.isNotEmpty()` | Boolean | 最低1モジュール必須 |
| ├─ `generateId` | None | 新規の場合はID生成 | String | UUID |
| ├─ `workflow.copy(updatedAt)` | `Timestamp.now()` | タイムスタンプ更新 | Workflow | |
| └─ `Firestore.document(id).set` | `workflow.toMap()` | Firestoreへ保存 | `Task<Void>` | 書き込み権限 |

### 2.3 モジュール操作 (エディタ)

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **WorkflowEditorViewModel.addModule** | `moduleType: String` | モジュールリストへ追加 | Unit | |
| ├─ `ModuleFactory.create` | `moduleType` | タイプに基づくモジュールインスタンス生成 | Module | Unknown type処理 |
| └─ `_modules.value += newModule` | `list` | StateFlow更新 | Unit | |
| **WorkflowEditorViewModel.moveModule** | `from: Int, to: Int` | ドラッグ&ドロップ | Unit | |
| ├─ Validation | `from, to` | `indices.contains(from/to)` | Boolean | IndexOutOfBounds回避 |
| └─ `MutableList.add(to, removeAt(from))` | `indices` | リスト操作 | Unit | |
| **WorkflowEditorViewModel.removeModule** | `index: Int` | モジュール削除 | Unit | |
| └─ `_modules.value = _modules.value.filterIndexed` | `predicate` | 指定インデックス除外 | List | |

---

## 3. ワークフロー実行エンジン (Workflow Execution Engine)

### 3.1 実行制御

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **WorkflowEngine.execute** | `workflow: Workflow` | ワークフロー全体の実行 | `Flow<ExecutionStatus>` | |
| ├─ `createExecutionContext` | None | 変数スコープ初期化 | ExecutionContext | |
| ├─ Loop `workflow.modules` | `module` | 各モジュールを順次実行 | | キャンセル確認 |
| │\u0026nbsp;\u0026nbsp;├─ **executorMap[type]?.execute** | `module, context` | DIマップからModuleExecutor取得・実行 | ExecutionResult | |
| │\u0026nbsp;\u0026nbsp;├─ `updateProgress` | `current/total` | 進捗状況のemit | Unit | |
| │\u0026nbsp;\u0026nbsp;└─ Error Handling | `result.success == false` | 失敗時の中断/継続判定 | | stopOnFailure設定 |
| └─ **saveHistory** | `executionResult` | 実行履歴をRoomに保存 | Unit | |

### 3.2 ModuleExecutor インターフェース

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **ModuleExecutor.execute** | `module: Module, context: ExecutionContext` | モジュール実行の共通インターフェース | `ExecutionResult` | |
| ├─ `parseParameters` | `module.parameters` | JSONパラメータのパース | Map<String, Any> | 型変換エラー処理 |
| ├─ `validate` | `params` | 必須パラメータの検証 | Boolean | エラーメッセージ生成 |
| ├─ **executeInternal** | `params, context` | 各モジュール固有の処理 | Result<Any> | |
| └─ `ExecutionResult` | `success, message, output` | 結果オブジェクトの生成 | ExecutionResult | |

---

## 4. Google API統合

### 4.1 Gmail API - メール送信

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **GmailSendModule.executeInternal** | `to, subject, body, attachments?` | Gmail送信処理 | `ExecutionResult` | |
| ├─ **GoogleApiAuthorizer.getCredential** | `[GmailSend]` | OAuth2資格情報取得 | Credential | スコープ権限確認 |
| ├─ `Gmail.Builder` | `credential` | GmailServiceインスタンス生成 | Gmail | |
| ├─ `createMimeMessage` | `to, subject, body` | MimeMessage作成 | MimeMessage | |
| │\u0026nbsp;\u0026nbsp;├─ `MimeMessage.setRecipients` | `to` | 宛先設定 | Unit | メールアドレス形式 |
| │\u0026nbsp;\u0026nbsp;├─ `MimeMessage.setSubject` | `subject` | 件名設定 | Unit | |
| │\u0026nbsp;\u0026nbsp;└─ `MimeMessage.setText` | `body` | 本文設定 | Unit | |
| ├─ `encodeToBase64UrlSafe` | `mimeMessage` | Base64エンコード | String | |
| ├─ `gmail.users().messages().send` | `userId="me", message` | Gmail API呼び出し | `Message` | クォータ制限 |
| └─ Error Handling | `Exception` | try-catch | ExecutionResult(false) | ログ記録 |

### 4.2 Drive API - ファイルアップロード

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **DriveUploadModule.executeInternal** | `filePath, folderId?, fileName?` | Driveアップロード | `ExecutionResult` | |
| ├─ **GoogleApiAuthorizer.getCredential** | `[DriveFullAccess]` | OAuth2資格情報取得 | Credential | |
| ├─ `Drive.Builder` | `credential` | DriveServiceインスタンス生成 | Drive | |
| ├─ `File(filePath).exists()` | `filePath` | ファイル存在確認 | Boolean | FileNotFoundException |
| ├─ `com.google.api.services.drive.model.File` | `metadata` | ファイルメタデータ設定 | File | |
| │\u0026nbsp;\u0026nbsp;├─ `setName(fileName)` | `name` | ファイル名 | Unit | |
| │\u0026nbsp;\u0026nbsp;└─ `setParents(listOf(folderId))` | `folderId` | 親フォルダID | Unit | フォルダ存在確認 |
| ├─ `FileContent` | `file` | ファイル内容 | FileContent | MIMEタイプ自動検出 |
| ├─ `drive.files().create` | `fileMetadata, mediaContent` | Drive API呼び出し | File | ストレージ容量 |
| └─ Error Handling | `Exception` | try-catch | ExecutionResult(false) | ログ記録 |

### 4.3 Sheets API - データ追記

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **SheetsAppendModule.executeInternal** | `spreadsheetId, range, values` | Sheets行追加 | `ExecutionResult` | |
| ├─ **GoogleApiAuthorizer.getCredential** | `[SheetsFullAccess]` | OAuth2資格情報取得 | Credential | |
| ├─ `Sheets.Builder` | `credential` | SheetsServiceインスタンス生成 | Sheets | |
| ├─ Validation | `values` | `values.isNotEmpty()` | Boolean | 空データ拒否 |
| ├─ `ValueRange` | `values` | データレンジ作成 | ValueRange | |
| ├─ `sheets.spreadsheets().values().append` | `spreadsheetId, range, valueRange` | Sheets API呼び出し | AppendValuesResponse | スプレッドシート権限 |
| │\u0026nbsp;\u0026nbsp;└─ `setValueInputOption("USER_ENTERED")` | None | 数式解釈 | Request | |
| └─ Error Handling | `Exception` | try-catch | ExecutionResult(false) | ログ記録 |

### 4.4 Calendar API - イベント作成

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **CalendarCreateEventModule.executeInternal** | `summary, start, end, description?` | カレンダーイベント作成 | `ExecutionResult` | |
| ├─ **CalendarApiService.createEvent** | `params` | カレンダーサービス呼び出し | Result<Event> | |
| │\u0026nbsp;\u0026nbsp;├─ **GoogleApiAuthorizer.getCredential** | `[CalendarFullAccess]` | OAuth2資格情報取得 | Credential | |
| │\u0026nbsp;\u0026nbsp;├─ `Calendar.Builder` | `credential` | CalendarServiceインスタンス生成 | Calendar | |
| │\u0026nbsp;\u0026nbsp;├─ `Event` | `metadata` | イベントオブジェクト作成 | Event | |
| │\u0026nbsp;\u0026nbsp;│\u0026nbsp;\u0026nbsp;├─ `setSummary(summary)` | `summary` | イベントタイトル | Unit | |
| │\u0026nbsp;\u0026nbsp;│\u0026nbsp;\u0026nbsp;├─ `setStart(EventDateTime)` | `start` | 開始時刻 | Unit | DateTime形式検証 |
| │\u0026nbsp;\u0026nbsp;│\u0026nbsp;\u0026nbsp;└─ `setEnd(EventDateTime)` | `end` | 終了時刻 | Unit | start < end検証 |
| │\u0026nbsp;\u0026nbsp;└─ `calendar.events().insert` | `"primary", event` | Calendar API呼び出し | Event | カレンダー権限 |
| └─ Error Handling | `Exception` | try-catch | ExecutionResult(false) | ログ記録 |

### 4.5 Chat API - メッセージ送信

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **ChatPostModule.executeInternal** | `spaceId, message` | Chatメッセージ送信 | `ExecutionResult` | |
| ├─ **ChatApiService.postMessage** | `spaceId, messageText` | Chat API呼び出し | Boolean | |
| │\u0026nbsp;\u0026nbsp;├─ **GoogleApiAuthorizer.getCredential** | `[ChatBot]` | OAuth2資格情報取得 | Credential | |
| │\u0026nbsp;\u0026nbsp;├─ `formatSpaceId` | `spaceId` | "spaces/"プレフィックス確認 | String | |
| │\u0026nbsp;\u0026nbsp;├─ `URL` | `"https://chat.googleapis.com/v1/$spaceId/messages"` | エンドポイントURL | URL | |
| │\u0026nbsp;\u0026nbsp;├─ `HttpURLConnection` | `url` | HTTP接続確立 | HttpURLConnection | |
| │\u0026nbsp;\u0026nbsp;│\u0026nbsp;\u0026nbsp;├─ `setRequestMethod("POST")` | None | POSTリクエスト | Unit | |
| │\u0026nbsp;\u0026nbsp;│\u0026nbsp;\u0026nbsp;├─ `setRequestProperty("Authorization")` | `"Bearer $token"` | OAuth2トークン設定 | Unit | トークン有効期限 |
| │\u0026nbsp;\u0026nbsp;│\u0026nbsp;\u0026nbsp;└─ `setRequestProperty("Content-Type")` | `"application/json"` | Content-Type設定 | Unit | |
| │\u0026nbsp;\u0026nbsp;├─ `JSONObject` | `{"text": messageText}` | リクエストボディ作成 | JSONObject | |
| │\u0026nbsp;\u0026nbsp;├─ `OutputStreamWriter.write` | `jsonPayload` | ペイロード送信 | Unit | |
| │\u0026nbsp;\u0026nbsp;├─ `connection.responseCode` | None | レスポンス確認 | Int | 200-299成功 |
| │\u0026nbsp;\u0026nbsp;└─ Error Stream | `errorStream` | エラー詳細取得 | String | ログ記録 |
| └─ Error Handling | `Exception` | try-catch | ExecutionResult(false) | ログ記録 |

---

## 5. ローカルデータ管理

### 5.1 Room Database - 実行履歴

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **HistoryRepository.saveHistory** | `history: History` | 実行履歴の保存 | Unit | |
| └─ `HistoryDao.insertHistory` | `history` | Room Insert | Long (rowId) | |
| **HistoryRepository.getAllHistory** | None | 全履歴取得 | `Flow<List<History>>` | |
| └─ `HistoryDao.getAllHistory` | None | Room Query | Flow | `.orderBy("executedAt", DESC)` |
| **HistoryRepository.deleteHistory** | `historyId: String` | 履歴削除 | Unit | |
| └─ `HistoryDao.deleteHistory` | `history` | Room Delete | Unit | |

### 5.2 DataStore - 設定管理

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **SettingsRepository.saveTheme** | `theme: String` | テーマ設定保存 | Unit | |
| └─ `dataStore.edit { it[THEME_KEY] = theme }` | `preferences` | DataStore書き込み | Unit | |
| **SettingsRepository.getTheme** | None | テーマ設定取得 | `Flow<String>` | |
| └─ `dataStore.data.map { it[THEME_KEY] ?: "System" }` | None | DataStore読み込み | Flow | デフォルト値 |

---

## 6. エラーハンドリング パターン

### 6.1 統一エラー処理

```kotlin
// すべてのModuleExecutorで使用
try {
    val result = performApiCall()
    ExecutionResult(success = true, message = "成功", output = result)
} catch (e: GoogleJsonResponseException) {
    Timber.e(e, "Google API Error: ${e.statusCode}")
    ExecutionResult(success = false, message = "API Error: ${e.message}")
} catch (e: IOException) {
    Timber.e(e, "Network Error")
    ExecutionResult(success = false, message = "ネットワークエラー: ${e.message}")
} catch (e: Exception) {
    Timber.e(e, "Unexpected Error")
    ExecutionResult(success = false, message = "予期しないエラー: ${e.message}")
}
```

### 6.2 リトライロジック (未実装)

将来的にWorkflowEngineにリトライ機能を追加する予定。

---

## 7. 依存関係とDI (Hilt)

### 7.1 主要なDIモジュール

| Module | Provides | Scope |
| :--- | :--- | :--- |
| **ApiModule** | Gmail, Drive, Sheets, Calendar, ChatApiService | @Singleton |
| **RepositoryModule** | WorkflowRepository, HistoryRepository, SettingsRepository | @Singleton |
| **ExecutorBindings** | ModuleExecutor (マップバインディング) | @Singleton |

### 7.2 ModuleExecutor マップバインディング

```kotlin
@Binds
@IntoMap
@ModuleKey("gmail_send")
abstract fun bindGmailSendModule(impl: GmailSendModule): ModuleExecutor
```

これにより、WorkflowEngineは以下のようにDIマップから動的に取得可能:

```kotlin
@Inject lateinit var executorMap: Map<String, @JvmSuppressWildcards Provider<ModuleExecutor>>
val executor = executorMap[module.type]?.get()
```

---

## 6. UI テーマとハイライトカラー (Theme & Highlight Color)

### 6.1 ハイライトカラー適用フロー

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **SettingsRepository.saveHighlightColor** | `highlightColor: String` | ハイライトカラー設定保存 | Unit | |
| └─ `dataStore.edit { it[HIGHLIGHT_COLOR_KEY] = highlightColor }` | `preferences` | DataStore書き込み | Unit | |
| **SettingsRepository.highlightColor** | None | ハイライトカラー設定取得 | `Flow<String>` | |
| └─ `dataStore.data.map { it[HIGHLIGHT_COLOR_KEY] ?: "default" }` | None | DataStore読み込み | Flow | デフォルト値 |
| **ThemeViewModel.highlightColor** | None | UI状態管理 | `StateFlow<String>` | |
| └─ `repository.highlightColor.stateIn` | `scope` | Flow→StateFlow変換 | StateFlow | |

### 6.2 テーマ適用ロジック

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **GWSAutoForAndroidTheme** | `theme: String, highlightColor: String` | Material Theme設定 | @Composable | |
| ├─ `isDarkTheme` | `theme` | "Light"/"Dark"/"System"判定 | Boolean | システム設定考慮 |
| ├─ `baseColorScheme` | `isDarkTheme` | DarkColorScheme/LightColorScheme選択 | ColorScheme | |
| └─ **highlightColorカラースキーム適用** | `highlightColor` | primaryカラーのオーバーライド | ColorScheme | |
| &nbsp;&nbsp;&nbsp;├─ `"forest"` | None | ForestPrimaryDark/Light適用 | ColorScheme.copy() | |
| &nbsp;&nbsp;&nbsp;├─ `"ocean"` | None | OceanPrimaryDark/Light適用 | ColorScheme.copy() | |
| &nbsp;&nbsp;&nbsp;├─ `"sakura"` | None | SakuraPrimaryDark/Light適用 | ColorScheme.copy() | |
| &nbsp;&nbsp;&nbsp;└─ `"default"` | None | DefaultPrimaryDark/Light適用 | ColorScheme.copy() | Sharp Neon |

### 6.3 Compose UI での clickable 修飾子処理

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **CalendarScreen - clickable要素** | `onClick: () -> Unit` | クリック処理 | Modifier | |
| ├─ `Modifier.clickable` | `indication = null, interactionSource` | PlatformRipple互換性エラー回避 | Modifier | IndicationNodeFactory要求 |
| └─ `remember { MutableInteractionSource() }` | None | インタラクション状態管理 | MutableInteractionSource | |

**課題と解決策**:
- Material 3の`PlatformRipple`は`IndicationNodeFactory`を実装していないため、`clickable`修飾子でエラーが発生
- 解決: `indication = null`を明示的に指定してrippleエフェクトを無効化

---

## 7. Dagger Hilt による依存性注入 (Dependency Injection)

### 7.1 ModuleExecutor Map Injection

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class WorkflowModule {
    @Binds
    @IntoMap
    @StringKey("gmail_send")
    abstract fun bindGmailSendModule(executor: GmailSendModuleExecutor): ModuleExecutor
    
    // ... 19個のModuleExecutor bindings
}
```

**使用例**:
```kotlin
@Inject lateinit var executorMap: Map<String, @JvmSuppressWildcards Provider<ModuleExecutor>>
val executor = executorMap[module.type]?.get()
```

---

## まとめ

このドキュメントは、Tsunaguアプリケーションの全データフローを網羅的に記述しています。認証からワークフロー実行、各Google APIとの統合、ローカルデータ管理、エラーハンドリング、UIテーマ管理まで、開発とメンテナンスの指針として活用できます。

### 最近の更新 (2025-11-22)

- **Compose clickable互換性修正**: CalendarScreen内の全clickable要素に`indication = null`を明示的に指定
- **ハイライトカラー統一適用**: Color.ktに`DefaultPrimaryDark/Light`を追加し、Theme.ktでデフォルトカラーも統一的に扱えるように修正
- **角丸デザイン**: `RoundedCornerShape(0.dp)`で鋭角デザインを実装済み
