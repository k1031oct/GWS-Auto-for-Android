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
| ├─ `credential.selectedAccount` | `account` | アカウントの設定 | Unit | アカウント有効性 |

### 1.3 初回起動セットアップ (First-Time Setup)

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **MainViewModel.checkFirstRun** | None | 初回起動かどうかの確認 | `Boolean` | |
| └─ **SettingsRepository.isFirstRun** | None | DataStoreからフラグ取得 | `Flow<Boolean>` | |
|   └─ `dataStore.data.map { it[IS_FIRST_RUN_KEY] ?: true }` | `preferences` | フラグ読み込み | Boolean | デフォルトはtrue |
| **SetupWizardViewModel.saveInitialSettings** | `country: String, language: String` | 初期設定の保存 | Unit | |
| ├─ **SettingsRepository.saveCountry** | `country` | 国コードの保存 | Unit | |
| │ └─ `dataStore.edit { it[COUNTRY_KEY] = country }` | `country` | DataStore書き込み | Unit | |
| ├─ **SettingsRepository.saveLanguage** | `language` | 言語コードの保存 | Unit | |
| │ └─ `dataStore.edit { it[LANGUAGE_KEY] = language }` | `language` | DataStore書き込み | Unit | |
| └─ **SettingsRepository.setFirstRunCompleted** | None | 初回起動完了フラグ設定 | Unit | |
|   └─ `dataStore.edit { it[IS_FIRST_RUN_KEY] = false }` | `false` | フラグをfalseに設定 | Unit | |

---

## 2. ワークフロー管理 (Workflow Management)

### 2.0 ナビゲーション引数の取得

詳細画面や編集画面では、`SavedStateHandle` を通じてナビゲーション引数を取得し、データの初期読み込みをトリガーする。

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **[Editor]ViewModel** | `savedStateHandle: SavedStateHandle` | ViewModelのコンストラクタでDI | Unit | |
| └─ `init { ... }` | None | 初期化ブロック | Unit | |
|   ├─ `savedStateHandle.get<String>("workflowId")` | `"workflowId"` | ナビゲーション引数を取得 | `String?` | Nullチェック |
|   └─ `loadWorkflow(workflowId)` | `workflowId` | 取得したIDでデータ読み込みを開始 | Unit | |

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

### 2.2.1 ワークフローお気に入り状態更新

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **WorkflowRepository.updateFavoriteStatus** | `workflowId: String, isFavorite: Boolean` | お気に入り状態の更新 | `Result<Unit>` | |
| └─ `Firestore.document(id).update` | `"isFavorite", isFavorite` | isFavoriteフィールドを更新 | `Task<Void>` | 書き込み権限 |

### 2.5 ワークフロー並び替え (Workflow Reordering)

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **WorkflowViewModel.reorderWorkflows** | `fromId, toId` | ワークフローの並び替え | Unit | |
| ├─ `allWorkflows` | None | 全ワークフロー取得 | List<Workflow> | |
| ├─ `rootWorkflows` | None | ルート要素の抽出・ソート | MutableList | `order` ASC, `id` ASC |
| ├─ `fromIndex, toIndex` | `fromId, toId` | インデックス特定 | Int | 範囲外チェック |
| ├─ `removeAt(fromIndex)` | `fromIndex` | 移動元削除 | Workflow | |
| ├─ `calculateInsertionIndex` | `fromIndex, toIndex` | 挿入位置計算 | Int | **Insert Above Logic** |
| │  ├─ `Moving Down (from < to)` | | `toIndex - 1` | | 削除によるズレ補正 |
| │  └─ `Moving Up (from > to)` | | `toIndex` | | |
| ├─ `add(insertionIndex, item)` | `index, item` | リストへ挿入 | Unit | |
| ├─ `updateOrders` | `list` | `order`フィールド再採番 | List<Workflow> | 0-based index |
| └─ **WorkflowRepository.updateWorkflowOrders** | `workflows` | DB更新 | Unit | |

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

### 2.4 ワークフロータグ管理

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **WorkflowEditorViewModel.addTagToWorkflow** | `tagName: String` | ワークフローへのタグ追加 | Unit | |
| ├─ `_selectedTags.value` | `tagName` | 重複チェック | Boolean | 既存なら追加しない |
| ├─ `_selectedTags.value += tagName` | `tagName` | StateFlow更新 | Unit | |
| └─ **TagRepository.addTag** | `Tag(tagName)` | タグマスタへの保存 | Unit | |
| **WorkflowEditorViewModel.removeTagFromWorkflow** | `tagName: String` | ワークフローからのタグ削除 | Unit | |
| └─ `_selectedTags.value -= tagName` | `tagName` | StateFlow更新 | Unit | |
| **WorkflowEditorActivity.showAddTagDialog** | None | タグ追加ダイアログ表示 | Dialog | |
| ├─ `viewModel.availableTags` | None | 既存タグリスト取得 | List<Tag> | |
| └─ `filter(!selectedTags.contains)` | `availableTags` | 未選択タグのみ表示 | List<String> | |

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
| ├─ `context.resolveVariables` | `params` | パラメータ内の変数 `{{var}}` を解決 | Map<String, Any> | 変数未定義エラー |
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

### 4.1.1 Gmail API - メール受信

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **GmailReceiveModule.executeInternal** | `query, maxResults?, labelIds?` | Gmail受信処理 | `ExecutionResult` | |
| ├─ **GoogleApiAuthorizer.getCredential** | `[GmailReadOnly]` | OAuth2資格情報取得 | Credential | スコープ権限確認 |
| ├─ `Gmail.Builder` | `credential` | GmailServiceインスタンス生成 | Gmail | |
| ├─ `gmail.users().messages().list` | `userId="me"` | メッセージリスト取得 | ListMessagesResponse | |
| │  ├─ `setQ(query)` | `query` | 検索クエリ設定 | Request | Gmail検索構文 |
| │  ├─ `setMaxResults(maxResults ?: 10)` | `maxResults` | 取得件数制限 | Request | 1-500 |
| │  └─ `setLabelIds(labelIds)` | `labelIds` | ラベルフィルタ | Request | INBOX, SENT等 |
| ├─ Loop `messages` | `messageId` | 各メッセージの詳細取得 | | |
| │  ├─ `gmail.users().messages().get` | `userId="me", id=messageId` | メッセージ取得 | Message | |
| │  ├─ `parseHeaders` | `message.payload.headers` | ヘッダー解析 | Map | From, To, Subject, Date |
| │  ├─ `parseBody` | `message.payload` | 本文抽出 | String | text/plain, text/html |
| │  └─ `context.setVariable("emails", emailList)` | `emailList` | 実行コンテキストへ保存 | Unit | 次モジュールで利用可能 |
| └─ Error Handling | `Exception` | try-catch | ExecutionResult(false) | ログ記録 |

### 4.1.2 Gmail API - 添付ファイル保存

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **GmailSaveAttachmentModule.executeInternal** | `messageId, savePath, fileNamePattern?` | 添付ファイル保存 | `ExecutionResult` | |
| ├─ **GoogleApiAuthorizer.getCredential** | `[GmailReadOnly]` | OAuth2資格情報取得 | Credential | |
| ├─ `Gmail.Builder` | `credential` | GmailServiceインスタンス生成 | Gmail | |
| ├─ `gmail.users().messages().get` | `userId="me", id=messageId` | メッセージ取得 | Message | |
| ├─ `extractAttachments` | `message.payload.parts` | 添付ファイル部分抽出 | List<MessagePart> | MIMEパート解析 |
| ├─ Loop `attachments` | `part` | 各添付ファイルの処理 | | |
| │  ├─ Validation | `part.filename` | ファイル名パターンマッチ |  Boolean | 正規表現 |
| │  ├─ `gmail.users().messages().attachments().get` | `messageId, attachmentId` | 添付データ取得 | MessagePartBody | |
| │  ├─ `decodeBase64UrlSafe` | `attachmentData` | Base64デコード | ByteArray | |
| │  ├─ `File(savePath, filename)` | `path, name` | 保存先ファイル作成 | File | ディレクトリ存在確認 |
| │  └─ `FileOutputStream.write` | `decodedData` | ファイル書き込み | Unit | ストレージ容量 |
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

### 4.2.1 Drive API - ファイルダウンロード

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **DriveDownloadModule.executeInternal** | `fileId, savePath` | Driveダウンロード | `ExecutionResult` | |
| ├─ **GoogleApiAuthorizer.getCredential** | `[DriveReadOnly]` | OAuth2資格情報取得 | Credential | |
| ├─ `Drive.Builder` | `credential` | DriveServiceインスタンス生成 | Drive | |
| ├─ `drive.files().get(fileId)` | `fileId` | ファイルメタデータ取得 | File | ファイル存在確認 |
| ├─ `drive.files().get(fileId).executeMediaAndDownloadTo` | `outputStream` | ファイル内容ダウンロード | Unit | |
| ├─ `File(savePath, fileName)` | `path, name` | 保存先ファイル作成 | File | ディレクトリ存在確認 |
| ├─ `FileOutputStream` | `file` | ファイル出力ストリーム | FileOutputStream | |
| └─ Error Handling | `Exception` | try-catch | ExecutionResult(false) | ログ記録 |

### 4.2.2 Drive API - フォルダ作成

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **DriveCreateFolderModule.executeInternal** | `folderName, parentFolderId?` | Driveフォルダ作成 | `ExecutionResult` | |
| ├─ **GoogleApiAuthorizer.getCredential** | `[DriveFullAccess]` | OAuth2資格情報取得 | Credential | |
| ├─ `Drive.Builder` | `credential` | DriveServiceインスタンス生成 | Drive | |
| ├─ `com.google.api.services.drive.model.File` | `metadata` | フォルダメタデータ設定 | File | |
| │  ├─ `setName(folderName)` | `name` | フォルダ名 | Unit | |
| │  ├─ `setMimeType("application/vnd.google-apps.folder")` | None | フォルダタイプ設定 | Unit | |
| │  └─ `setParents(listOf(parentFolderId))` | `parentFolderId` | 親フォルダID | Unit | オプション |
| ├─ `drive.files().create(folderMetadata)` | `metadata` | Drive API呼び出し | File | |
| ├─ `context.setVariable("folderId", result.id)` | `folderId` | フォルダIDを保存 | Unit | 次モジュールで利用可能 |
| └─ Error Handling | `Exception` | try-catch | ExecutionResult(false) | ログ記録 |

### 4.2.3 Drive API - ファイル検索

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **DriveSearchModule.executeInternal** | `query, maxResults?` | Driveファイル検索 | `ExecutionResult` | |
| ├─ **GoogleApiAuthorizer.getCredential** | `[DriveReadOnly]` | OAuth2資格情報取得 | Credential | |
| ├─ `Drive.Builder` | `credential` | DriveServiceインスタンス生成 | Drive | |
| ├─ `drive.files().list()` | None | ファイルリストリクエスト | FileList | |
| │  ├─ `setQ(query)` | `query` | 検索クエリ設定 | Request | Drive検索構文 |
| │  ├─ `setPageSize(maxResults ?: 10)` | `maxResults` | 取得件数制限 | Request | 1-1000 |
| │  ├─ `setFields("files(id, name, mimeType, createdTime)")` | None | レスポンスフィールド指定 | Request | 効率化 |
| │  └─ `setOrderBy("createdTime desc")` | None | ソート順設定 | Request | |
| ├─ `execute()` | None | API実行 | FileList | |
| ├─ `context.setVariable("searchResults", fileList)` | `fileList` | 検索結果を保存 | Unit | 次モジュールで利用可能 |
| └─ Error Handling | `Exception` | try-catch | ExecutionResult(false) | ログ記録 |

### 4.2.4 Drive API - ファイル移動

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **DriveMoveFileModule.executeInternal** | `sourceFileUrl, destinationFolderUrl` | Driveファイル移動 | `ExecutionResult` | |
| ├─ `extractFileId` | `sourceFileUrl` | ファイルID抽出 | String | Regex |
| ├─ `extractFileId` | `destinationFolderUrl` | フォルダID抽出 | String | Regex |
| ├─ **DriveApiService.moveFile** | `fileId, folderId` | Drive API呼び出し | Unit | |
| └─ Error Handling | `Exception` | try-catch | ExecutionResult(false) | ログ記録 |

### 4.2.5 Drive API - フォルダ内ファイル削除
| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **DriveDeleteFilesInFolderModule.executeInternal** | `folderId, filterType` | フォルダ内ファイル削除 | `ExecutionResult` | |
| ├─ **GoogleApiAuthorizer.getCredential** | `[DriveFullAccess]` | OAuth2資格情報取得 | Credential | |
| ├─ `Drive.Builder` | `credential` | DriveServiceインスタンス生成 | Drive | |
| ├─ `constructQuery` | `folderId, filterType` | 検索クエリ構築 | String | 親フォルダ指定 + MIMEタイプ |
| ├─ `drive.files().list()` | `q=query` | 削除対象ファイル検索 | FileList | |
| ├─ Loop `files` | `file` | 各ファイルを削除 | | |
| │  └─ `drive.files().delete(file.id)` | `fileId` | 削除実行 | Void | |
| └─ Error Handling | `Exception` | try-catch | ExecutionResult(false) | ログ記録 |

### 4.2.6 Drive API - ファイル検出
| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **DriveDetectFileModule.executeInternal** | `query, searchType` | ファイル検出 | `ExecutionResult` | |
| ├─ **GoogleApiAuthorizer.getCredential** | `[DriveReadOnly]` | OAuth2資格情報取得 | Credential | |
| ├─ `Drive.Builder` | `credential` | DriveServiceインスタンス生成 | Drive | |
| ├─ `constructQuery` | `query, searchType` | 検索クエリ構築 | String | name contains / fullText contains |
| ├─ `drive.files().list()` | `q=query, pageSize=1` | ファイル検索 | FileList | |
| ├─ `files.firstOrNull()` | None | 最初の結果を取得 | File? | |
| ├─ `context.setVariable("detectedFileId", file.id)` | `file.id` | IDを保存 | Unit | |
| ├─ `context.setVariable("detectedFileUrl", file.webViewLink)` | `file.webViewLink` | URLを保存 | Unit | |
| └─ Error Handling | `Exception` | try-catch | ExecutionResult(false) | ログ記録 |

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

### 4.3.1 Sheets API - セル更新

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **SheetsUpdateModule.executeInternal** | `spreadsheetId, range, values` | Sheetsセル更新 | `ExecutionResult` | |
| ├─ **GoogleApiAuthorizer.getCredential** | `[SheetsFullAccess]` | OAuth2資格情報取得 | Credential | |
| ├─ `Sheets.Builder` | `credential` | SheetsServiceインスタンス生成 | Sheets | |
| ├─ Validation | `values` | `values.isNotEmpty()` | Boolean | 空データ拒否 |
| ├─ `ValueRange` | `values` | データレンジ作成 | ValueRange | |
| ├─ `sheets.spreadsheets().values().update` | `spreadsheetId, range, valueRange` | Sheets API呼び出し | UpdateValuesResponse | スプレッドシート権限 |
| │  └─ `setValueInputOption("USER_ENTERED")` | None | 数式解釈 | Request | |
| └─ Error Handling | `Exception` | try-catch | ExecutionResult(false) | ログ記録 |

### 4.3.2 Sheets API - データ取得

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **SheetsGetModule.executeInternal** | `spreadsheetId, range` | Sheetsデータ取得 | `ExecutionResult` | |
| ├─ **GoogleApiAuthorizer.getCredential** | `[SheetsReadOnly]` | OAuth2資格情報取得 | Credential | |
| ├─ `Sheets.Builder` | `credential` | SheetsServiceインスタンス生成 | Sheets | |
| ├─ `sheets.spreadsheets().values().get` | `spreadsheetId, range` | Sheets API呼び出し | ValueRange | スプレッドシート権限 |
| ├─ `result.getValues()` | None | データ行のリスト取得 | List<List<Object>> | Null安全性 |
| ├─ `context.setVariable("sheetData", data)` | `data` | 実行コンテキストへ保存 | Unit | 次モジュールで利用可能 |
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

### 4.4.1 Calendar API - イベント取得

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **CalendarGetEventsModule.executeInternal** | `timeMin, timeMax, maxResults?` | カレンダーイベント取得 | `ExecutionResult` | |
| ├─ **GoogleApiAuthorizer.getCredential** | `[CalendarReadOnly]` | OAuth2資格情報取得 | Credential | |
| ├─ `Calendar.Builder` | `credential` | CalendarServiceインスタンス生成 | Calendar | |
| ├─ `calendar.events().list("primary")` | `calendarId = "primary"` | イベントリストリクエスト | Events | |
| │  ├─ `setTimeMin(DateTime(timeMin))` | `timeMin` | 開始時刻設定 | Request | |
| │  ├─ `setTimeMax(DateTime(timeMax))` | `timeMax` | 終了時刻設定 | Request | |
| │  ├─ `setMaxResults(maxResults ?: 10)` | `maxResults` | 取得件数制限 | Request | 1-2500 |
| │  ├─ `setSingleEvents(true)` | None | 繰り返しイベント展開 | Request | |
| │  └─ `setOrderBy("startTime")` | None | 開始時刻順ソート | Request | |
| ├─ `execute()` | None | API実行 | Events | クォータ制限 |
| ├─ `context.setVariable("calendarEvents", eventList)` | `eventList` | 実行コンテキストへ保存 | Unit | 次モジュールで利用可能 |
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

## 6. ユーティリティモジュール (Utility Modules)

### 6.1 条件分岐 (If/Else)

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **IfElseModule.executeInternal** | `condition, trueModuleId?, falseModuleId?` | 条件分岐評価 | `ExecutionResult` | |
| ├─ `evaluateCondition` | `condition` | 式の評価 (JavaScript/Kotlin Script) | Boolean | 構文エラー処理 |
| ├─ Conditional | `result == true` | 分岐先決定 | String? (ModuleID) | |
| ├─ `context.setNextModuleId` | `moduleId` | 次に実行するモジュールを指定 | Unit | フロー制御 |
| └─ `ExecutionResult` | `success=true` | 実行結果返却 | ExecutionResult | |

### 6.2 待機 (Delay)

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **DelayModule.executeInternal** | `duration, unit` | 指定時間の待機 | `ExecutionResult` | |
| ├─ `calculateMillis` | `duration, unit` | ミリ秒換算 | Long | |
| ├─ `Thread.sleep` / `delay` | `millis` | スレッド停止 (Coroutine delay) | Unit | キャンセル対応 |
| └─ `ExecutionResult` | `success=true` | 実行結果返却 | ExecutionResult | |

### 6.5 ログ出力 (Log Message)

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **LogMessageModule.executeInternal** | `message` | ログメッセージの出力 | `ExecutionResult` | |
| ├─ `context.resolveVariables` | `message` | 変数展開 | String | |
| └─ `ExecutionResult` | `success=true, output=message` | 結果返却 (UIログに表示) | ExecutionResult | |

### 6.3 繰り返し (Loop/For Each)

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **ForEachModule.executeInternal** | `items, variableName` | リストの繰り返し処理 | `ExecutionResult` | |
| ├─ `context.getVariable(items)` | `items` | 対象リストの取得 | List<Any> | 型チェック |
| ├─ `Loop Control` | `index, item` | イテレーション管理 | | |
| │  ├─ `context.setVariable(variableName, item)` | `item` | 現在の要素を変数に設定 | Unit | |
| │  └─ `executeChildModules` | `childModules` | 内部モジュールの実行 | ExecutionResult | 再帰的実行 |
| └─ `ExecutionResult` | `success=true` | 実行結果返却 | ExecutionResult | |

### 6.4 データ操作 (Data Manipulation)

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **DataTransformModule.executeInternal** | `operation, input, outputVariable` | データの変換・加工 | `ExecutionResult` | |
| ├─ `performOperation` | `operation` | 操作の実行 | Any | |
| │  ├─ `JSON Parse` | `input` | 文字列→JSONオブジェクト | JSONObject | |
| │  ├─ `Regex Extract` | `input, pattern` | 正規表現抽出 | String | |
| │  └─ `Math Calc` | `expression` | 数式計算 | Number | |
| ├─ `context.setVariable(outputVariable, result)` | `result` | 結果の保存 | Unit | |
| └─ `ExecutionResult` | `success=true` | 実行結果返却 | ExecutionResult | |

### 6.5 変数定義 (Define Variable)

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **DefineVariableModule.executeInternal** | `variableName, value` | 変数定義 | `ExecutionResult` | |
| ├─ `context.resolveVariables` | `value` | 変数展開 | String | |
| ├─ `context.setVariable(variableName, value)` | `value` | 変数保存 | Unit | |
| └─ `ExecutionResult` | `success=true` | 実行結果返却 | ExecutionResult | |

### 6.6 相対日付取得 (Get Relative Date)

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **GetRelativeDateModule.executeInternal** | `baseDate, offsetValue, offsetUnit, outputVariableName` | 日付計算 | `ExecutionResult` | |
| ├─ `LocalDate.parse` / `LocalDate.now` | `baseDate` | 基準日決定 | LocalDate | ISO_LOCAL_DATE |
| ├─ `ChronoUnit` | `offsetUnit` | 単位変換 | ChronoUnit | DAYS/WEEKS/MONTHS/YEARS |
| ├─ `baseDate.plus` | `offsetValue, unit` | 日付加算 | LocalDate | |
| ├─ `context.setVariable` | `formattedDate` | 結果保存 | Unit | |
| └─ `ExecutionResult` | `success=true` | 実行結果返却 | ExecutionResult | |

### 6.7 ログ出力 (Log Message)

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **LogMessageModule.executeInternal** | `message` | ログ出力 | `ExecutionResult` | |
| ├─ `context.resolveVariables` | `message` | 変数展開 | String | |
| └─ `ExecutionResult` | `success=true, output=message` | 実行結果返却 | ExecutionResult | |

### 6.8 Toast通知 (Show Toast)

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **ToastNotificationModule.executeInternal** | `message` | Toast表示 | `ExecutionResult` | |
| ├─ `context.resolveVariables` | `message` | 変数展開 | String | |
| ├─ `withContext(Dispatchers.Main)` | None | メインスレッド切り替え | Unit | |
| │  └─ `Toast.makeText(...).show()` | `message` | Toast表示 | Unit | |
| └─ `ExecutionResult` | `success=true` | 実行結果返却 | ExecutionResult | |

---

## 5. ローカルデータ管理

### 5.1 Room Database - 実行履歴

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **HistoryRepository.saveHistory** | `history: History` | 実行履歴の保存 | Unit | |
| └─ `HistoryDao.insertHistory` | `history` | Room Insert | Long (rowId) | |
| **HistoryRepository.getAllHistory** | `isBookmarkedOnly: Boolean` | 全履歴またはブックマーク済み履歴取得 | `Flow<List<History>>` | |
| └─ `HistoryDao.getAllHistory` | `isBookmarkedOnly` | Room Query (`WHERE isBookmarked = :isBookmarkedOnly` if true) | Flow | `.orderBy("executedAt", DESC)` |
| **HistoryRepository.updateBookmarkStatus** | `historyId: String, isBookmarked: Boolean` | ブックマーク状態の更新 | Unit | |
| └─ `HistoryDao.updateBookmark` | `historyId, isBookmarked` | Room Update | Unit | |
| **HistoryRepository.deleteHistory** | `historyId: String` | 履歴削除 | Unit | |
| └─ `HistoryDao.deleteHistory` | `history` | Room Delete | Unit | |

### 5.2 DataStore - 設定管理

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **SettingsRepository.saveTheme** | `theme: String` | テーマ設定保存 | Unit | |
| └─ `dataStore.edit { it[THEME_KEY] = theme }` | `preferences` | DataStore書き込み | Unit | |
| **SettingsRepository.getTheme** | None | テーマ設定取得 | `Flow<String>` | |
| └─ `dataStore.data.map { it[THEME_KEY] ?: "System" }` | None | DataStore読み込み | Flow | デフォルト値 |

### 5.3 検索履歴管理

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **SearchHistoryRepository.getHistory** | None | 検索履歴の取得 | `Flow<List<String>>` | |
| └─ `dataStore.data.map { it[HISTORY_KEY] ?: emptySet() }` | `preferences` | DataStore読み込み | `Flow<Set<String>>` | |
| **SearchHistoryRepository.addHistory** | `term: String` | 検索履歴の追加 | Unit | |
| ├─ `dataStore.edit` | `term` | 既存履歴と結合 | `Set<String>` | 重複は自動で排除 |
| └─ `take(20)` | `Set` | 最新20件に制限 | `Set<String>` | |
| **SearchHistoryRepository.deleteHistory** | `term: String` | 個別履歴の削除 | Unit | |
| └─ `dataStore.edit { it[HISTORY_KEY] -= term }` | `term` | 指定した用語を削除 | Unit | |
| **SearchHistoryRepository.clearAllHistory** | None | 全履歴の削除 | Unit | |
| └─ `dataStore.edit { it.remove(HISTORY_KEY) }` | None | キーごと削除 | Unit | |

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

### 6.2.1 テーマ変更時のクラッシュ対策

- **課題**: テーマや言語変更時に`AppCompatDelegate.setDefaultNightMode`や`setApplicationLocales`が呼ばれるとActivityが再生成されるが、Composeの`ExposedDropdownMenu`が開いたままだと`PopupLayout`がWindowからデタッチされた状態で更新を行おうとしてクラッシュする (`IllegalArgumentException: View not attached to window manager`)。
- **解決策**: `AppSettingsScreen`において、テーマ・言語設定の保存処理に`300ms`の遅延を入れることで、ドロップダウンメニューが閉じてからActivity再生成が行われるように制御。

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
    @StringKey("gmail_send_email")
    abstract fun bindGmailSendEmailModule(impl: GmailSendEmailModule): ModuleExecutor
    
    @Binds
    @IntoMap
    @StringKey("SHOW_TOAST")
    abstract fun bindToastNotificationModule(impl: ToastNotificationModule): ModuleExecutor

    // ... 他のModuleExecutor bindings
}
```

**使用例**:
```kotlin
@Inject lateinit var executorMap: Map<String, @JvmSuppressWildcards Provider<ModuleExecutor>>
val executor = executorMap[module.type]?.get()
```

---

## 8. トリガー管理 (Trigger Management)

### 8.1 トリガー設定の保存

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **TriggerRepository.saveTrigger** | `trigger: Trigger` | トリガー設定の保存 | `Result<Unit>` | |
| ├─ Validation | `trigger` | `workflowId.isNotBlank()` | Boolean | 必須項目チェック |
| ├─ `trigger.copy(updatedAt)` | `Timestamp.now()` | タイムスタンプ更新 | Trigger | |
| └─ `Firestore.collection("triggers").document(id).set` | `trigger.toMap()` | Firestoreへ保存 | `Task<Void>` | 書き込み権限 |

### 8.2 トリガー一覧の取得

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **TriggerRepository.getAllTriggers** | `userId: String` | Firestoreからトリガー取得 | `Flow<List<Trigger>>` | |
| └─ `Firestore.collection("triggers")` | `.whereEqualTo("userId", userId)` | クエリ実行 | QuerySnapshot | インデックス確認 |
|  └─ `Document.toObject<Trigger>()` | `document` | Firestoreドキュメント→Kotlinオブジェクト変換 | Trigger | データ型の一致 |

### 8.3 トリガーの削除

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **TriggerRepository.deleteTrigger** | `triggerId: String` | トリガーの削除 | `Result<Unit>` | |
| └─ `Firestore.collection("triggers").document(triggerId).delete()` | `triggerId` | Firestoreから削除 | `Task<Void>` | 書き込み権限 |

---

## 9. ダッシュボード (Dashboard)

### 9.1 統計データの集計

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **DashboardViewModel.loadStatistics** | None | ダッシュボード用データの読み込みと集計 | `StateFlow<DashboardStats>` | |
| ├─ **HistoryRepository.getAllHistory** | `isBookmarkedOnly = false` | 全実行履歴を取得 | `Flow<List<History>>` | |
| │ ├─ `count()` | `list` | 総実行回数 | Int | |
| │ ├─ `count { it.isSuccess }` | `list` | 成功回数 | Int | |
| │ └─ `groupBy { it.workflowName }.mapValues { it.value.size }` | `list` | ワークフロー別実行回数 | `Map<String, Int>` | |
| ├─ **WorkflowRepository.getAllWorkflows** | `userId` | 全ワークフローを取得 | `Flow<List<Workflow>>` | |
| │ └─ `count()` | `list` | 総ワークフロー数 | Int | |
| └─ `combine` | `historyFlow, workflowFlow` | 複数Flowを結合し、`DashboardStats`オブジェクトを生成 | `Flow<DashboardStats>` | |

---

## 10. UI状態管理パターン (UI State Management Pattern)

多くの画面では、以下の`ViewModel-Repository-UI`パターンを用いてUIの状態を管理する。

### 10.1 データ読み込みとUI状態への変換

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **[Screen]Composable** | `viewModel` | `viewModel.uiState.collectAsState()` | `UiState` | |
| └─ **[Name]ViewModel** | `repository` | `_uiState: MutableStateFlow<UiState>` | `StateFlow<UiState>` | |
|   ├─ `init { loadData() }` | None | 初期データ読み込み | Unit | |
|   ├─ `loadData()` | None | データ読み込み処理 | Unit | |
|   │ ├─ `_uiState.value = UiState.Loading` | None | ローディング状態を通知 | Unit | |
|   │ ├─ `repository.getData()` | `params` | Repositoryからデータ取得 | `Flow<Result<T>>` | |
|   │ │ └─ `onEach { result -> ... }` | `result` | 結果の処理 | Unit | |
|   │ │   ├─ `is Success` | `data` | `_uiState.value = UiState.Success(data)` | Unit | |
|   │ │   └─ `is Failure` | `error` | `_uiState.value = UiState.Error(error.message)` | Unit | |
|   │ └─ `.launchIn(viewModelScope)` | `scope` | Coroutine内でFlowを収集 | Job | |

### 10.2 UiState シールドクラス定義

```kotlin
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
```

---

## 11. 実行時権限の管理 (Runtime Permission Management)

### 11.1 権限要求と結果処理

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **[Screen]Composable** | `permission: String` | `rememberLauncherForActivityResult` | `ActivityResultLauncher` | |
| ├─ `onClick` | None | ボタンクリック等で処理を開始 | Unit | |
| │ ├─ `ContextCompat.checkSelfPermission` | `permission` | 権限の有無を確認 | `PERMISSION_GRANTED` or `DENIED` | |
| │ ├─ `is GRANTED` | None | 権限があれば、機能を直接実行 | Unit | |
| │ └─ `is DENIED` | None | 権限がなければ、ランチャーを起動 | Unit | |
| │   └─ `permissionLauncher.launch(permission)` | `permission` | システムの権限要求ダイアログ表示 | Unit | |
| └─ `onResult` | `isGranted: Boolean` | ユーザーの選択結果を処理 | Unit | |
|   ├─ `isGranted == true` | None | 権限が許可された場合、機能を実行 | Unit | |
|   └─ `isGranted == false` | None | 権限が拒否された場合、Toast等で通知 | Unit | |

---

## 12. ファイルロギング (File Logging)

デバッグビルドにおいて、Logcatへの出力と同時に、ログをファイルに保存する仕組み。

| 階層 (Call Stack) | 入力値 (Arguments) | 処理・検証 (Micro Logic) | 出力値 (Return) | 整合性 (Check) |
| :--- | :--- | :--- | :--- | :--- |
| **App.onCreate** | None | アプリケーション起動時 | Unit | |
| └─ `if (BuildConfig.DEBUG)` | None | デバッグビルドか判定 | Unit | |
|   └─ `Timber.plant(FileLoggingTree(context))` | `context` | カスタムTreeを植える | Unit | |
| **FileLoggingTree.log** | `priority, tag, message, t` | `Timber.d()`などが呼ばれた時 | Unit | |
| ├─ `formatLogMsg` | `priority, tag, message` | ログメッセージをフォーマット | String | `[TIME] [TAG]: message` |
| ├─ `getLogFile()` | `context` | ログファイルの参照を取得 | `File` | `context.filesDir` |
| └─ `file.appendText(log)` | `log` | ファイルに追記 | Unit | `use`で自動クローズ |

---

## まとめ

このドキュメントは、Tsunaguアプリケーションの全データフローを網羅的に記述しています。認証からワークフロー実行、各Google APIとの統合、ローカルデータ管理、エラーハンドリング、UIテーマ管理まで、開発とメンテナンスの指針として活用できます。

### 最近の更新 (2025-11-22)

- **Compose clickable互換性修正**: CalendarScreen内の全clickable要素に`indication = null`を明示的に指定
- **ハイライトカラー統一適用**: Color.ktに`DefaultPrimaryDark/Light`を追加し、Theme.ktでデフォルトカラーも統一的に扱えるように修正
- **角丸デザイン**: `RoundedCornerShape(0.dp)`で鋭角デザインを実装済み
- **UIテーマ適用の拡張**:
  - **MainActivity**: Bottom Navigationにハイライトカラー適用(白テキスト/アイコン、ハイライトインジケーター)、FAB背景色適用
  - **WorkflowEditorActivity**: ActionBar、Save Button、FAB、TextInputLayout(枠線とヒントラベル)、Cancel Button、ModuleAdapterへのハイライトカラー適用
  - **ModuleAdapter**: モジュールアイコン、トグルスイッチ、アクションボタンへのハイライトカラー適用
  - **ModuleSettingsDialogFragment**: 動的生成されるTextInputLayout、Buttonへのハイライトカラー適用(`currentHighlightColor`プロパティ経由)
  - **SearchFragment/TagAdapter**: タグ追加Chip buttonへのハイライトカラー適用
  - **SettingsActivity**: Toolbarへのハイライトカラー適用