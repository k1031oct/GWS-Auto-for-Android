package com.gws.auto.mobile.android.domain.service;

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
public final class SheetsApiService_Factory implements Factory<SheetsApiService> {
  private final Provider<GoogleApiAuthorizer> authorizerProvider;

  private SheetsApiService_Factory(Provider<GoogleApiAuthorizer> authorizerProvider) {
    this.authorizerProvider = authorizerProvider;
  }

  @Override
  public SheetsApiService get() {
    return newInstance(authorizerProvider.get());
  }

  public static SheetsApiService_Factory create(Provider<GoogleApiAuthorizer> authorizerProvider) {
    return new SheetsApiService_Factory(authorizerProvider);
  }

  public static SheetsApiService newInstance(GoogleApiAuthorizer authorizer) {
    return new SheetsApiService(authorizer);
  }
}
