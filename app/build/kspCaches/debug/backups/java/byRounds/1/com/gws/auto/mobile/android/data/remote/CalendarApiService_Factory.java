package com.gws.auto.mobile.android.data.remote;

import com.gws.auto.mobile.android.domain.service.GoogleApiAuthorizer;
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
  private final Provider<GoogleApiAuthorizer> googleApiAuthorizerProvider;

  private CalendarApiService_Factory(Provider<GoogleApiAuthorizer> googleApiAuthorizerProvider) {
    this.googleApiAuthorizerProvider = googleApiAuthorizerProvider;
  }

  @Override
  public CalendarApiService get() {
    return newInstance(googleApiAuthorizerProvider.get());
  }

  public static CalendarApiService_Factory create(
      Provider<GoogleApiAuthorizer> googleApiAuthorizerProvider) {
    return new CalendarApiService_Factory(googleApiAuthorizerProvider);
  }

  public static CalendarApiService newInstance(GoogleApiAuthorizer googleApiAuthorizer) {
    return new CalendarApiService(googleApiAuthorizer);
  }
}
