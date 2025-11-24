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
public final class GetRelativeDateModule_Factory implements Factory<GetRelativeDateModule> {
  @Override
  public GetRelativeDateModule get() {
    return newInstance();
  }

  public static GetRelativeDateModule_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static GetRelativeDateModule newInstance() {
    return new GetRelativeDateModule();
  }

  private static final class InstanceHolder {
    static final GetRelativeDateModule_Factory INSTANCE = new GetRelativeDateModule_Factory();
  }
}
