package com.gws.auto.mobile.android.ui.workflow;

import com.gws.auto.mobile.android.domain.engine.WorkflowEngine;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;

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
public final class WorkflowFragment_MembersInjector implements MembersInjector<WorkflowFragment> {
  private final Provider<WorkflowEngine> workflowEngineProvider;

  private WorkflowFragment_MembersInjector(Provider<WorkflowEngine> workflowEngineProvider) {
    this.workflowEngineProvider = workflowEngineProvider;
  }

  @Override
  public void injectMembers(WorkflowFragment instance) {
    injectWorkflowEngine(instance, workflowEngineProvider.get());
  }

  public static MembersInjector<WorkflowFragment> create(
      Provider<WorkflowEngine> workflowEngineProvider) {
    return new WorkflowFragment_MembersInjector(workflowEngineProvider);
  }

  @InjectedFieldSignature("com.gws.auto.mobile.android.ui.workflow.WorkflowFragment.workflowEngine")
  public static void injectWorkflowEngine(WorkflowFragment instance,
      WorkflowEngine workflowEngine) {
    instance.workflowEngine = workflowEngine;
  }
}
