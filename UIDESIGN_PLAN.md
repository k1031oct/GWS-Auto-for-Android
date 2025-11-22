# UI Design Plan

## 共通定義
* **Navigation:** Jetpack Navigation Composeを使用
* **Theme:** Material 3 (Light/Dark対応)

## 1. ログイン画面 (LoginScreen)
* **Route:** `login`
* **構成要素:**
    * `Box`: 背景画像またはグラデーション
    * `Column`: 中央揃え
        * `Image`: アプリアイコン (120dp)
        * `Text`: "GWS Automater" (HeadlineMedium)
        * `Text`: "Your personal automation assistant" (BodyLarge)
        * `Spacer`: 高さ調整
        * `Button`: "Sign in with Google" (アイコン付き)
    * `CircularProgressIndicator`: ローディング時のみ表示
* **UiState (LoginUiState):**
    * `isLoading: Boolean = false`
    * `error: String? = null` (スナックバー用)
    * `isLoggedIn: Boolean = false`
* **User Event:**
    * `onSignInClick` -> `viewModel.signIn()`
    * `onErrorDismiss` -> `viewModel.clearError()`

## 2. ワークフロー一覧画面 (WorkflowListScreen)
* **Route:** `home`
* **構成要素:**
    * `Scaffold`: TopBar, FAB
    * `TopAppBar`: タイトル "Workflows", アカウントアイコン
    * `LazyColumn`:
        * `item`: 検索バー (Clickable -> BottomSheet)
        * `items(workflows)`: `WorkflowCard`
    * `WorkflowCard`:
        * `Row`: アイコン, タイトル, 最終実行日
        * `IconButton`: 実行(Run), メニュー(Edit/Delete)
    * `FloatingActionButton`: 新規作成 (+)
* **UiState (WorkflowListUiState):**
    * `workflows: List<WorkflowSummary> = emptyList()`
    * `isLoading: Boolean = false`
    * `searchQuery: String = ""`
* **User Event:**
    * `onWorkflowClick(id: String)` -> Navigate to `detail/{id}`
    * `onRunClick(id: String)` -> `viewModel.runWorkflow(id)`
    * `onCreateClick` -> Navigate to `editor/new`
    * `onDeleteClick(id: String)` -> Show Confirmation Dialog

## 3. ワークフロー編集画面 (WorkflowEditorScreen)
* **Route:** `editor/{workflowId}`
* **構成要素:**
    * `Scaffold`: TopBar (Back, Save)
    * `Column`:
        * `OutlinedTextField`: ワークフロー名
        * `OutlinedTextField`: 説明 (Optional)
        * `ReorderableLazyColumn`: モジュールリスト (Drag & Drop対応)
            * `ModuleItem`: アイコン, モジュール名, 設定概要, 削除ボタン
        * `Button`: "Add Module" (Bottom Sheet Trigger)
    * `ModalBottomSheet` (ModulePalette):
        * `LazyVerticalGrid`: カテゴリ別モジュールアイコン
* **UiState (WorkflowEditorUiState):**
    * `workflowId: String?`
    * `title: String = ""`
    * `description: String = ""`
    * `modules: List<ModuleConfig> = emptyList()`
    * `isPaletteOpen: Boolean = false`
    * `isSaving: Boolean = false`
    * `validationErrors: List<String> = emptyList()`
* **User Event:**
    * `onTitleChange(String)` -> `viewModel.updateTitle()`
    * `onModuleMove(from: Int, to: Int)` -> `viewModel.moveModule()`
    * `onModuleDelete(index: Int)` -> `viewModel.removeModule()`
    * `onAddModuleClick` -> `viewModel.openPalette()`
    * `onModuleSelect(ModuleType)` -> `viewModel.addModule()`
    * `onSaveClick` -> `viewModel.saveWorkflow()`

## 4. 実行履歴画面 (HistoryScreen)
* **Route:** `history`
* **構成要素:**
    * `LazyColumn`:
        * `StickyHeader`: 日付 (例: "Today", "Yesterday")
        * `items(logs)`: `HistoryItem`
    * `HistoryItem`:
        * `Row`: ステータスアイコン (Success/Fail), ワークフロー名, 実行時刻
        * `Text`: エラーメッセージ (失敗時のみ)
* **UiState (HistoryUiState):**
    * `logs: Map<LocalDate, List<ExecutionLog>>`
    * `filter: ExecutionStatus? = null` (All/Success/Failure)
* **User Event:**
    * `onFilterSelect(ExecutionStatus?)` -> `viewModel.setFilter()`
    * `onLogClick(logId)` -> Navigate to `log_detail/{logId}`
