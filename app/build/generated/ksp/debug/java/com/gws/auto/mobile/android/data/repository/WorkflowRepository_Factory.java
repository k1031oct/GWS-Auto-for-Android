package com.gws.auto.mobile.android.data.repository;

import com.gws.auto.mobile.android.data.local.db.WorkflowDao;
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
public final class WorkflowRepository_Factory implements Factory<WorkflowRepository> {
  private final Provider<WorkflowDao> workflowDaoProvider;

  private WorkflowRepository_Factory(Provider<WorkflowDao> workflowDaoProvider) {
    this.workflowDaoProvider = workflowDaoProvider;
  }

  @Override
  public WorkflowRepository get() {
    return newInstance(workflowDaoProvider.get());
  }

  public static WorkflowRepository_Factory create(Provider<WorkflowDao> workflowDaoProvider) {
    return new WorkflowRepository_Factory(workflowDaoProvider);
  }

  public static WorkflowRepository newInstance(WorkflowDao workflowDao) {
    return new WorkflowRepository(workflowDao);
  }
}
