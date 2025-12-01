# アーキテクチャ図解 (Architecture Diagrams)

このドキュメントは、アプリケーションの構造とデータフローを視覚的に理解するため、Mermaid記法で記述された図を提供します。

---

## 1. 全体アーキテクチャ図 (Overall Architecture)

アプリケーションの主要コンポーネント間の依存関係とデータの流れを示します。MVVMアーキテクチャをベースに、データ層がリポジトリパターンで抽象化されています。

```mermaid
graph TD
    subgraph UI Layer
        A[UI Screens (Jetpack Compose)]
    end

    subgraph ViewModel Layer
        B[ViewModels (e.g., WorkflowViewModel)]
    end

    subgraph Data Layer
        C[Repositories (e.g., WorkflowRepository)]
        subgraph Data Sources
            D[Remote: Firestore]
            E[Remote: Google APIs <br>(Gmail, Drive, etc.)]
            F[Local: Room DB <br>(History)]
            G[Local: DataStore <br>(Settings)]
        end
    end

    A -- User Events --> B
    B -- Observes UI State --> A
    B -- Calls Functions --> C
    C -- Provides Data as Flow --> B
    C -- Accesses --> D
    C -- Accesses --> E
    C -- Accesses --> F
    C -- Accesses --> G
```

---

## 2. ワークフロー実行フロー図 (Workflow Execution Flow)

ワークフローがトリガーされてから、各モジュールが実行されるまでの一連の処理の流れを示します。

```mermaid
graph TD
    A[実行開始<br>(手動 or トリガー)] --> B{WorkflowEngine.execute};
    B --> C[ExecutionContextを作成];
    C --> D{ワークフローの<br>全モジュールをループ};
    D -- 次のモジュールあり --> E[モジュール種別に基づき<br>DIマップからExecutorを取得];
    E --> F[Executor.executeを実行];
    F --> G{実行結果は成功？};
    G -- Yes --> H[ExecutionContextを更新];
    H --> D;
    G -- No --> I{stopOnFailure == true?};
    I -- Yes --> J[実行を中断し、<br>エラーを記録];
    I -- No --> H;
    D -- ループ終了 --> K[実行履歴を<br>HistoryRepositoryに保存];
    K --> L[完了];
    J --> L;
```

---

## 3. 画面遷移図 (Screen Navigation)

Jetpack Navigation Composeによる、主要な画面（Composable）間の遷移を示します。

```mermaid
graph TD
    A(LoginScreen) --> B(MainScreen);

    subgraph MainScreen (Bottom Navigation)
        B1[WorkflowListScreen]
        B2[ScheduleScreen]
        B3[HistoryScreen]
        B4[SettingsScreen]
    end

    A -- サインイン成功 --> B1;

    B1 -- FABクリック --> C{WorkflowEditorScreen <br>(新規作成)};
    B1 -- ワークフローカードクリック --> D{WorkflowEditorScreen <br>(編集)};
    B1 -- 検索バークリック --> E[SearchBottomSheet];

    B2 -- FABクリック --> F[TriggerEditorScreen];

    B4 -- 各種メニュー --> G[...詳細設定画面];
```
