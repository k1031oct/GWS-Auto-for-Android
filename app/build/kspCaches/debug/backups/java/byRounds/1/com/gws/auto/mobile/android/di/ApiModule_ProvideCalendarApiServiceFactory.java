package com.gws.auto.mobile.android.di;

import com.gws.auto.mobile.android.data.remote.CalendarApiService;
import com.gws.auto.mobile.android.domain.service.GoogleApiAuthorizer;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class ApiModule_ProvideCalendarApiServiceFactory implements Factory<CalendarApiService> {
  private final Provider<GoogleApiAuthorizer> authorizerProvider;

  private ApiModule_ProvideCalendarApiServiceFactory(
      Provider<GoogleApiAuthorizer> authorizerProvider) {
    this.authorizerProvider = authorizerProvider;
  }

  @Override
  public CalendarApiService get() {
    return provideCalendarApiService(authorizerProvider.get());
  }

  public static ApiModule_ProvideCalendarApiServiceFactory create(
      Provider<GoogleApiAuthorizer> authorizerProvider) {
    return new ApiModule_ProvideCalendarApiServiceFactory(authorizerProvider);
  }

  public static CalendarApiService provideCalendarApiService(GoogleApiAuthorizer authorizer) {
    return Preconditions.checkNotNullFromProvides(ApiModule.INSTANCE.provideCalendarApiService(authorizer));
  }
}
