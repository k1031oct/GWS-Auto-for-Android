package com.gws.auto.mobile.android.ui.dashboard;

import com.gws.auto.mobile.android.data.repository.HistoryRepository;
import com.gws.auto.mobile.android.data.repository.WorkflowRepository;
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
public final class DashboardViewModel_Factory implements Factory<DashboardViewModel> {
  private final Provider<HistoryRepository> historyRepositoryProvider;

  private final Provider<WorkflowRepository> workflowRepositoryProvider;

  private DashboardViewModel_Factory(Provider<HistoryRepository> historyRepositoryProvider,
      Provider<WorkflowRepository> workflowRepositoryProvider) {
    this.historyRepositoryProvider = historyRepositoryProvider;
    this.workflowRepositoryProvider = workflowRepositoryProvider;
  }

  @Override
  public DashboardViewModel get() {
    return newInstance(historyRepositoryProvider.get(), workflowRepositoryProvider.get());
  }

  public static DashboardViewModel_Factory create(
      Provider<HistoryRepository> historyRepositoryProvider,
      Provider<WorkflowRepository> workflowRepositoryProvider) {
    return new DashboardViewModel_Factory(historyRepositoryProvider, workflowRepositoryProvider);
  }

  public static DashboardViewModel newInstance(HistoryRepository historyRepository,
      WorkflowRepository workflowRepository) {
    return new DashboardViewModel(historyRepository, workflowRepository);
  }
}
