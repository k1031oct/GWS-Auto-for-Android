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
public final class MicrosoftApiAuthorizer_Factory implements Factory<MicrosoftApiAuthorizer> {
  private final Provider<Context> contextProvider;

  private MicrosoftApiAuthorizer_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public MicrosoftApiAuthorizer get() {
    return newInstance(contextProvider.get());
  }

  public static MicrosoftApiAuthorizer_Factory create(Provider<Context> contextProvider) {
    return new MicrosoftApiAuthorizer_Factory(contextProvider);
  }

  public static MicrosoftApiAuthorizer newInstance(Context context) {
    return new MicrosoftApiAuthorizer(context);
  }
}
