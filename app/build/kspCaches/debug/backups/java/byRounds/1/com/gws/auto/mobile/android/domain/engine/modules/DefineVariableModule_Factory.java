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
public final class DefineVariableModule_Factory implements Factory<DefineVariableModule> {
  @Override
  public DefineVariableModule get() {
    return newInstance();
  }

  public static DefineVariableModule_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static DefineVariableModule newInstance() {
    return new DefineVariableModule();
  }

  private static final class InstanceHolder {
    static final DefineVariableModule_Factory INSTANCE = new DefineVariableModule_Factory();
  }
}
