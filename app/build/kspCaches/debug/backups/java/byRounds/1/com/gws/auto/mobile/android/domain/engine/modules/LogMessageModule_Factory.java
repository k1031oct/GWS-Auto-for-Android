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
public final class LogMessageModule_Factory implements Factory<LogMessageModule> {
  @Override
  public LogMessageModule get() {
    return newInstance();
  }

  public static LogMessageModule_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static LogMessageModule newInstance() {
    return new LogMessageModule();
  }

  private static final class InstanceHolder {
    static final LogMessageModule_Factory INSTANCE = new LogMessageModule_Factory();
  }
}
