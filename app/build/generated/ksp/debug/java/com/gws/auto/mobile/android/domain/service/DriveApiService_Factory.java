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
public final class DriveApiService_Factory implements Factory<DriveApiService> {
  private final Provider<GoogleApiAuthorizer> authorizerProvider;

  private DriveApiService_Factory(Provider<GoogleApiAuthorizer> authorizerProvider) {
    this.authorizerProvider = authorizerProvider;
  }

  @Override
  public DriveApiService get() {
    return newInstance(authorizerProvider.get());
  }

  public static DriveApiService_Factory create(Provider<GoogleApiAuthorizer> authorizerProvider) {
    return new DriveApiService_Factory(authorizerProvider);
  }

  public static DriveApiService newInstance(GoogleApiAuthorizer authorizer) {
    return new DriveApiService(authorizer);
  }
}
