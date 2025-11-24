package com.gws.auto.mobile.android.domain.service;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class GoogleApiAuthorizer_Factory implements Factory<GoogleApiAuthorizer> {
  private final Provider<Context> contextProvider;

  private GoogleApiAuthorizer_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public GoogleApiAuthorizer get() {
    return newInstance(contextProvider.get());
  }

  public static GoogleApiAuthorizer_Factory create(Provider<Context> contextProvider) {
    return new GoogleApiAuthorizer_Factory(contextProvider);
  }

  public static GoogleApiAuthorizer newInstance(Context context) {
    return new GoogleApiAuthorizer(context);
  }
}
