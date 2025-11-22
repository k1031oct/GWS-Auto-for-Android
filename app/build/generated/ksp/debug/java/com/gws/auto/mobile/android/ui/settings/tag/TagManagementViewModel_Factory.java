package com.gws.auto.mobile.android.ui.settings.tag;

import com.gws.auto.mobile.android.data.repository.TagRepository;
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
public final class TagManagementViewModel_Factory implements Factory<TagManagementViewModel> {
  private final Provider<TagRepository> tagRepositoryProvider;

  private TagManagementViewModel_Factory(Provider<TagRepository> tagRepositoryProvider) {
    this.tagRepositoryProvider = tagRepositoryProvider;
  }

  @Override
  public TagManagementViewModel get() {
    return newInstance(tagRepositoryProvider.get());
  }

  public static TagManagementViewModel_Factory create(
      Provider<TagRepository> tagRepositoryProvider) {
    return new TagManagementViewModel_Factory(tagRepositoryProvider);
  }

  public static TagManagementViewModel newInstance(TagRepository tagRepository) {
    return new TagManagementViewModel(tagRepository);
  }
}
