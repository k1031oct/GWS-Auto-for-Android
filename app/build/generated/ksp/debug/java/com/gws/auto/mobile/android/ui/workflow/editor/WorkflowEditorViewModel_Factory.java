package com.gws.auto.mobile.android.ui.workflow.editor;

import com.gws.auto.mobile.android.data.repository.TagRepository;
import com.gws.auto.mobile.android.data.repository.WorkflowRepository;
import com.gws.auto.mobile.android.domain.engine.WorkflowEngine;
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
public final class WorkflowEditorViewModel_Factory implements Factory<WorkflowEditorViewModel> {
  private final Provider<WorkflowRepository> workflowRepositoryProvider;

  private final Provider<TagRepository> tagRepositoryProvider;

  private final Provider<WorkflowEngine> workflowEngineProvider;

  private WorkflowEditorViewModel_Factory(Provider<WorkflowRepository> workflowRepositoryProvider,
      Provider<TagRepository> tagRepositoryProvider,
      Provider<WorkflowEngine> workflowEngineProvider) {
    this.workflowRepositoryProvider = workflowRepositoryProvider;
    this.tagRepositoryProvider = tagRepositoryProvider;
    this.workflowEngineProvider = workflowEngineProvider;
  }

  @Override
  public WorkflowEditorViewModel get() {
    return newInstance(workflowRepositoryProvider.get(), tagRepositoryProvider.get(), workflowEngineProvider.get());
  }

  public static WorkflowEditorViewModel_Factory create(
      Provider<WorkflowRepository> workflowRepositoryProvider,
      Provider<TagRepository> tagRepositoryProvider,
      Provider<WorkflowEngine> workflowEngineProvider) {
    return new WorkflowEditorViewModel_Factory(workflowRepositoryProvider, tagRepositoryProvider, workflowEngineProvider);
  }

  public static WorkflowEditorViewModel newInstance(WorkflowRepository workflowRepository,
      TagRepository tagRepository, WorkflowEngine workflowEngine) {
    return new WorkflowEditorViewModel(workflowRepository, tagRepository, workflowEngine);
  }
}
