package com.gws.auto.mobile.android.data.repository;

import com.gws.auto.mobile.android.data.local.db.SearchHistoryDao;
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
public final class SearchHistoryRepository_Factory implements Factory<SearchHistoryRepository> {
  private final Provider<SearchHistoryDao> searchHistoryDaoProvider;

  private SearchHistoryRepository_Factory(Provider<SearchHistoryDao> searchHistoryDaoProvider) {
    this.searchHistoryDaoProvider = searchHistoryDaoProvider;
  }

  @Override
  public SearchHistoryRepository get() {
    return newInstance(searchHistoryDaoProvider.get());
  }

  public static SearchHistoryRepository_Factory create(
      Provider<SearchHistoryDao> searchHistoryDaoProvider) {
    return new SearchHistoryRepository_Factory(searchHistoryDaoProvider);
  }

  public static SearchHistoryRepository newInstance(SearchHistoryDao searchHistoryDao) {
    return new SearchHistoryRepository(searchHistoryDao);
  }
}
