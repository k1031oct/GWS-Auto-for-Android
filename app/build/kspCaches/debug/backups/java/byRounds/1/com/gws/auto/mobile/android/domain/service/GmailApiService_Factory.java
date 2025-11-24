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
public final class GmailApiService_Factory implements Factory<GmailApiService> {
  private final Provider<GoogleApiAuthorizer> authorizerProvider;

  private GmailApiService_Factory(Provider<GoogleApiAuthorizer> authorizerProvider) {
    this.authorizerProvider = authorizerProvider;
  }

  @Override
  public GmailApiService get() {
    return newInstance(authorizerProvider.get());
  }

  public static GmailApiService_Factory create(Provider<GoogleApiAuthorizer> authorizerProvider) {
    return new GmailApiService_Factory(authorizerProvider);
  }

  public static GmailApiService newInstance(GoogleApiAuthorizer authorizer) {
    return new GmailApiService(authorizer);
  }
}
