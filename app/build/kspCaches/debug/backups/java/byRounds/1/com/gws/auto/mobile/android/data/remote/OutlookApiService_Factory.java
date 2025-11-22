package com.gws.auto.mobile.android.data.remote;

import com.gws.auto.mobile.android.domain.service.MicrosoftApiAuthorizer;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import okhttp3.OkHttpClient;

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
public final class OutlookApiService_Factory implements Factory<OutlookApiService> {
  private final Provider<MicrosoftApiAuthorizer> microsoftApiAuthorizerProvider;

  private final Provider<OkHttpClient> httpClientProvider;

  private OutlookApiService_Factory(Provider<MicrosoftApiAuthorizer> microsoftApiAuthorizerProvider,
      Provider<OkHttpClient> httpClientProvider) {
    this.microsoftApiAuthorizerProvider = microsoftApiAuthorizerProvider;
    this.httpClientProvider = httpClientProvider;
  }

  @Override
  public OutlookApiService get() {
    return newInstance(microsoftApiAuthorizerProvider.get(), httpClientProvider.get());
  }

  public static OutlookApiService_Factory create(
      Provider<MicrosoftApiAuthorizer> microsoftApiAuthorizerProvider,
      Provider<OkHttpClient> httpClientProvider) {
    return new OutlookApiService_Factory(microsoftApiAuthorizerProvider, httpClientProvider);
  }

  public static OutlookApiService newInstance(MicrosoftApiAuthorizer microsoftApiAuthorizer,
      OkHttpClient httpClient) {
    return new OutlookApiService(microsoftApiAuthorizer, httpClient);
  }
}
