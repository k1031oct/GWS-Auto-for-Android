package com.gws.auto.mobile.android.domain.engine;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import java.util.Map;
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
public final class ModuleExecutorProvider_Factory implements Factory<ModuleExecutorProvider> {
  private final Provider<Map<String, javax.inject.Provider<ModuleExecutor>>> executorsProvider;

  private ModuleExecutorProvider_Factory(
      Provider<Map<String, javax.inject.Provider<ModuleExecutor>>> executorsProvider) {
    this.executorsProvider = executorsProvider;
  }

  @Override
  public ModuleExecutorProvider get() {
    return newInstance(executorsProvider.get());
  }

  public static ModuleExecutorProvider_Factory create(
      Provider<Map<String, javax.inject.Provider<ModuleExecutor>>> executorsProvider) {
    return new ModuleExecutorProvider_Factory(executorsProvider);
  }

  public static ModuleExecutorProvider newInstance(
      Map<String, javax.inject.Provider<ModuleExecutor>> executors) {
    return new ModuleExecutorProvider(executors);
  }
}
