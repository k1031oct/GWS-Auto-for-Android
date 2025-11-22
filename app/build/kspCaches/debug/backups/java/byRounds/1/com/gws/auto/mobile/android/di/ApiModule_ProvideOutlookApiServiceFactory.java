package com.gws.auto.mobile.android.di;

import com.gws.auto.mobile.android.data.remote.OutlookApiService;
import com.gws.auto.mobile.android.domain.service.MicrosoftApiAuthorizer;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class ApiModule_ProvideOutlookApiServiceFactory implements Factory<OutlookApiService> {
  private final Provider<MicrosoftApiAuthorizer> authorizerProvider;

  private final Provider<OkHttpClient> httpClientProvider;

  private ApiModule_ProvideOutlookApiServiceFactory(
      Provider<MicrosoftApiAuthorizer> authorizerProvider,
      Provider<OkHttpClient> httpClientProvider) {
    this.authorizerProvider = authorizerProvider;
    this.httpClientProvider = httpClientProvider;
  }

  @Override
  public OutlookApiService get() {
    return provideOutlookApiService(authorizerProvider.get(), httpClientProvider.get());
  }

  public static ApiModule_ProvideOutlookApiServiceFactory create(
      Provider<MicrosoftApiAuthorizer> authorizerProvider,
      Provider<OkHttpClient> httpClientProvider) {
    return new ApiModule_ProvideOutlookApiServiceFactory(authorizerProvider, httpClientProvider);
  }

  public static OutlookApiService provideOutlookApiService(MicrosoftApiAuthorizer authorizer,
      OkHttpClient httpClient) {
    return Preconditions.checkNotNullFromProvides(ApiModule.INSTANCE.provideOutlookApiService(authorizer, httpClient));
  }
}
