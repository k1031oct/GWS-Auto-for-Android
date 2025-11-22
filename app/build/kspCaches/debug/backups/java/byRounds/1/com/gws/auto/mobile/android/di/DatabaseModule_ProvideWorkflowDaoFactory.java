package com.gws.auto.mobile.android.di;

import com.gws.auto.mobile.android.data.local.db.AppDatabase;
import com.gws.auto.mobile.android.data.local.db.WorkflowDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideWorkflowDaoFactory implements Factory<WorkflowDao> {
  private final Provider<AppDatabase> appDatabaseProvider;

  private DatabaseModule_ProvideWorkflowDaoFactory(Provider<AppDatabase> appDatabaseProvider) {
    this.appDatabaseProvider = appDatabaseProvider;
  }

  @Override
  public WorkflowDao get() {
    return provideWorkflowDao(appDatabaseProvider.get());
  }

  public static DatabaseModule_ProvideWorkflowDaoFactory create(
      Provider<AppDatabase> appDatabaseProvider) {
    return new DatabaseModule_ProvideWorkflowDaoFactory(appDatabaseProvider);
  }

  public static WorkflowDao provideWorkflowDao(AppDatabase appDatabase) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideWorkflowDao(appDatabase));
  }
}
