package com.gws.auto.mobile.android.domain.service;

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
public final class CalendarApiService_Factory implements Factory<CalendarApiService> {
  private final Provider<GoogleApiAuthorizer> authorizerProvider;

  private CalendarApiService_Factory(Provider<GoogleApiAuthorizer> authorizerProvider) {
    this.authorizerProvider = authorizerProvider;
  }

  @Override
  public CalendarApiService get() {
    return newInstance(authorizerProvider.get());
  }

  public static CalendarApiService_Factory create(
      Provider<GoogleApiAuthorizer> authorizerProvider) {
    return new CalendarApiService_Factory(authorizerProvider);
  }

  public static CalendarApiService newInstance(GoogleApiAuthorizer authorizer) {
    return new CalendarApiService(authorizer);
  }
}
