package com.gws.auto.mobile.android.domain.engine;

import com.gws.auto.mobile.android.data.repository.HistoryRepository;
import com.gws.auto.mobile.android.data.repository.WorkflowRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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

  private LocalWorkflowEngine_Factory(Provider<ModuleExecutorProvider> moduleExecutorProvider,
      Provider<HistoryRepository> historyRepositoryProvider,
      Provider<WorkflowRepository> workflowRepositoryProvider) {
    this.moduleExecutorProvider = moduleExecutorProvider;
    this.historyRepositoryProvider = historyRepositoryProvider;
    this.workflowRepositoryProvider = workflowRepositoryProvider;
  }

  @Override
  public LocalWorkflowEngine get() {
    return newInstance(moduleExecutorProvider.get(), historyRepositoryProvider.get(), workflowRepositoryProvider.get());
  }

  public static LocalWorkflowEngine_Factory create(
      Provider<ModuleExecutorProvider> moduleExecutorProvider,
      Provider<HistoryRepository> historyRepositoryProvider,
      Provider<WorkflowRepository> workflowRepositoryProvider) {
    return new LocalWorkflowEngine_Factory(moduleExecutorProvider, historyRepositoryProvider, workflowRepositoryProvider);
  }

  public static LocalWorkflowEngine newInstance(ModuleExecutorProvider moduleExecutorProvider,
      HistoryRepository historyRepository, WorkflowRepository workflowRepository) {
    return new LocalWorkflowEngine(moduleExecutorProvider, historyRepository, workflowRepository);
  }
}
