package com.gws.auto.mobile.android.domain.engine.modules;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class ToastNotificationModule_Factory implements Factory<ToastNotificationModule> {
  @Override
  public ToastNotificationModule get() {
    return newInstance();
  }

  public static ToastNotificationModule_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ToastNotificationModule newInstance() {
    return new ToastNotificationModule();
  }

  private static final class InstanceHolder {
    static final ToastNotificationModule_Factory INSTANCE = new ToastNotificationModule_Factory();
  }
}
