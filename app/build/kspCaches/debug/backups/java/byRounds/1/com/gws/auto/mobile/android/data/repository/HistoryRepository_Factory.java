package com.gws.auto.mobile.android.data.repository;

import com.gws.auto.mobile.android.data.local.db.HistoryDao;
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
public final class HistoryRepository_Factory implements Factory<HistoryRepository> {
  private final Provider<HistoryDao> historyDaoProvider;

  private HistoryRepository_Factory(Provider<HistoryDao> historyDaoProvider) {
    this.historyDaoProvider = historyDaoProvider;
  }

  @Override
  public HistoryRepository get() {
    return newInstance(historyDaoProvider.get());
  }

  public static HistoryRepository_Factory create(Provider<HistoryDao> historyDaoProvider) {
    return new HistoryRepository_Factory(historyDaoProvider);
  }

  public static HistoryRepository newInstance(HistoryDao historyDao) {
    return new HistoryRepository(historyDao);
  }
}
