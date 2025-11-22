package com.gws.auto.mobile.android.data.repository;

import com.gws.auto.mobile.android.data.local.db.WorkflowFolderDao;
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
public final class WorkflowFolderRepository_Factory implements Factory<WorkflowFolderRepository> {
  private final Provider<WorkflowFolderDao> workflowFolderDaoProvider;

  private WorkflowFolderRepository_Factory(Provider<WorkflowFolderDao> workflowFolderDaoProvider) {
    this.workflowFolderDaoProvider = workflowFolderDaoProvider;
  }

  @Override
  public WorkflowFolderRepository get() {
    return newInstance(workflowFolderDaoProvider.get());
  }

  public static WorkflowFolderRepository_Factory create(
      Provider<WorkflowFolderDao> workflowFolderDaoProvider) {
    return new WorkflowFolderRepository_Factory(workflowFolderDaoProvider);
  }

  public static WorkflowFolderRepository newInstance(WorkflowFolderDao workflowFolderDao) {
    return new WorkflowFolderRepository(workflowFolderDao);
  }
}
