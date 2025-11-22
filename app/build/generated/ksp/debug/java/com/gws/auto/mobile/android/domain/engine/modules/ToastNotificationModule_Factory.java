package com.gws.auto.mobile.android.domain.engine.modules;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
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
public final class ToastNotificationModule_Factory implements Factory<ToastNotificationModule> {
  private final Provider<Context> contextProvider;

  private ToastNotificationModule_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public ToastNotificationModule get() {
    return newInstance(contextProvider.get());
  }

  public static ToastNotificationModule_Factory create(Provider<Context> contextProvider) {
    return new ToastNotificationModule_Factory(contextProvider);
  }

  public static ToastNotificationModule newInstance(Context context) {
    return new ToastNotificationModule(context);
  }
}
