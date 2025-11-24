package com.gws.auto.mobile.android.domain.engine;

import com.gws.auto.mobile.android.data.repository.HistoryRepository;
import com.gws.auto.mobile.android.data.repository.WorkflowRepository;
import com.gws.auto.mobile.android.domain.engine.modules.LogMessageModule;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class LocalWorkflowEngine_Factory implements Factory<LocalWorkflowEngine> {
  private final Provider<ModuleExecutorProvider> moduleExecutorProvider;

  private final Provider<HistoryRepository> historyRepositoryProvider;

  private final Provider<WorkflowRepository> workflowRepositoryProvider;

  private final Provider<LogMessageModule> logMessageModuleProvider;

  private LocalWorkflowEngine_Factory(Provider<ModuleExecutorProvider> moduleExecutorProvider,
      Provider<HistoryRepository> historyRepositoryProvider,
      Provider<WorkflowRepository> workflowRepositoryProvider,
      Provider<LogMessageModule> logMessageModuleProvider) {
    this.moduleExecutorProvider = moduleExecutorProvider;
    this.historyRepositoryProvider = historyRepositoryProvider;
    this.workflowRepositoryProvider = workflowRepositoryProvider;
    this.logMessageModuleProvider = logMessageModuleProvider;
  }

  @Override
  public LocalWorkflowEngine get() {
    return newInstance(moduleExecutorProvider.get(), historyRepositoryProvider.get(), workflowRepositoryProvider.get(), logMessageModuleProvider.get());
  }

  public static LocalWorkflowEngine_Factory create(
      Provider<ModuleExecutorProvider> moduleExecutorProvider,
      Provider<HistoryRepository> historyRepositoryProvider,
      Provider<WorkflowRepository> workflowRepositoryProvider,
      Provider<LogMessageModule> logMessageModuleProvider) {
    return new LocalWorkflowEngine_Factory(moduleExecutorProvider, historyRepositoryProvider, workflowRepositoryProvider, logMessageModuleProvider);
  }

  public static LocalWorkflowEngine newInstance(ModuleExecutorProvider moduleExecutorProvider,
      HistoryRepository historyRepository, WorkflowRepository workflowRepository,
      LogMessageModule logMessageModule) {
    return new LocalWorkflowEngine(moduleExecutorProvider, historyRepository, workflowRepository, logMessageModule);
  }
}
