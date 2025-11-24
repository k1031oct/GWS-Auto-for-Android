package com.gws.auto.mobile.android.domain.engine.modules;

import com.gws.auto.mobile.android.domain.service.SheetsApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
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
public final class SheetsSetValueModule_Factory implements Factory<SheetsSetValueModule> {
  private final Provider<SheetsApiService> sheetsApiServiceProvider;

  private SheetsSetValueModule_Factory(Provider<SheetsApiService> sheetsApiServiceProvider) {
    this.sheetsApiServiceProvider = sheetsApiServiceProvider;
  }

  @Override
  public SheetsSetValueModule get() {
    return newInstance(sheetsApiServiceProvider.get());
  }

  public static SheetsSetValueModule_Factory create(
      Provider<SheetsApiService> sheetsApiServiceProvider) {
    return new SheetsSetValueModule_Factory(sheetsApiServiceProvider);
  }

  public static SheetsSetValueModule newInstance(SheetsApiService sheetsApiService) {
    return new SheetsSetValueModule(sheetsApiService);
  }
}
