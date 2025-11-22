package com.gws.auto.mobile.android.domain.engine.modules;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import okhttp3.OkHttpClient;

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
public final class SlackPostModule_Factory implements Factory<SlackPostModule> {
  private final Provider<OkHttpClient> httpClientProvider;

  private SlackPostModule_Factory(Provider<OkHttpClient> httpClientProvider) {
    this.httpClientProvider = httpClientProvider;
  }

  @Override
  public SlackPostModule get() {
    return newInstance(httpClientProvider.get());
  }

  public static SlackPostModule_Factory create(Provider<OkHttpClient> httpClientProvider) {
    return new SlackPostModule_Factory(httpClientProvider);
  }

  public static SlackPostModule newInstance(OkHttpClient httpClient) {
    return new SlackPostModule(httpClient);
  }
}
