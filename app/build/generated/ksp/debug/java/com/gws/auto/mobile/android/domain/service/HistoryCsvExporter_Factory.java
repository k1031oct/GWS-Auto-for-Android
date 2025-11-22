package com.gws.auto.mobile.android.domain.service;

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
public final class HistoryCsvExporter_Factory implements Factory<HistoryCsvExporter> {
  @Override
  public HistoryCsvExporter get() {
    return newInstance();
  }

  public static HistoryCsvExporter_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static HistoryCsvExporter newInstance() {
    return new HistoryCsvExporter();
  }

  private static final class InstanceHolder {
    static final HistoryCsvExporter_Factory INSTANCE = new HistoryCsvExporter_Factory();
  }
}
