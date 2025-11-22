package com.gws.auto.mobile.android.ui.search;

import com.gws.auto.mobile.android.data.repository.SearchHistoryRepository;
import com.gws.auto.mobile.android.data.repository.TagRepository;
import com.gws.auto.mobile.android.data.repository.WorkflowFolderRepository;
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
public final class SearchViewModel_Factory implements Factory<SearchViewModel> {
  private final Provider<TagRepository> tagRepositoryProvider;

  private final Provider<SearchHistoryRepository> searchHistoryRepositoryProvider;

  private final Provider<WorkflowRepository> workflowRepositoryProvider;

  private final Provider<WorkflowFolderRepository> workflowFolderRepositoryProvider;

  private SearchViewModel_Factory(Provider<TagRepository> tagRepositoryProvider,
      Provider<SearchHistoryRepository> searchHistoryRepositoryProvider,
      Provider<WorkflowRepository> workflowRepositoryProvider,
      Provider<WorkflowFolderRepository> workflowFolderRepositoryProvider) {
    this.tagRepositoryProvider = tagRepositoryProvider;
    this.searchHistoryRepositoryProvider = searchHistoryRepositoryProvider;
    this.workflowRepositoryProvider = workflowRepositoryProvider;
    this.workflowFolderRepositoryProvider = workflowFolderRepositoryProvider;
  }

  @Override
  public SearchViewModel get() {
    return newInstance(tagRepositoryProvider.get(), searchHistoryRepositoryProvider.get(), workflowRepositoryProvider.get(), workflowFolderRepositoryProvider.get());
  }

  public static SearchViewModel_Factory create(Provider<TagRepository> tagRepositoryProvider,
      Provider<SearchHistoryRepository> searchHistoryRepositoryProvider,
      Provider<WorkflowRepository> workflowRepositoryProvider,
      Provider<WorkflowFolderRepository> workflowFolderRepositoryProvider) {
    return new SearchViewModel_Factory(tagRepositoryProvider, searchHistoryRepositoryProvider, workflowRepositoryProvider, workflowFolderRepositoryProvider);
  }

  public static SearchViewModel newInstance(TagRepository tagRepository,
      SearchHistoryRepository searchHistoryRepository, WorkflowRepository workflowRepository,
      WorkflowFolderRepository workflowFolderRepository) {
    return new SearchViewModel(tagRepository, searchHistoryRepository, workflowRepository, workflowFolderRepository);
  }
}
