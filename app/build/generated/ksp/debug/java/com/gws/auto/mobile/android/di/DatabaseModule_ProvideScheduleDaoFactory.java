package com.gws.auto.mobile.android.di;

import com.gws.auto.mobile.android.data.local.db.AppDatabase;
import com.gws.auto.mobile.android.data.local.db.ScheduleDao;
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
public final class DatabaseModule_ProvideScheduleDaoFactory implements Factory<ScheduleDao> {
  private final Provider<AppDatabase> appDatabaseProvider;

  private DatabaseModule_ProvideScheduleDaoFactory(Provider<AppDatabase> appDatabaseProvider) {
    this.appDatabaseProvider = appDatabaseProvider;
  }

  @Override
  public ScheduleDao get() {
    return provideScheduleDao(appDatabaseProvider.get());
  }

  public static DatabaseModule_ProvideScheduleDaoFactory create(
      Provider<AppDatabase> appDatabaseProvider) {
    return new DatabaseModule_ProvideScheduleDaoFactory(appDatabaseProvider);
  }

  public static ScheduleDao provideScheduleDao(AppDatabase appDatabase) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideScheduleDao(appDatabase));
  }
}
