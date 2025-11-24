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
public final class SheetsCreateNewModule_Factory implements Factory<SheetsCreateNewModule> {
  private final Provider<SheetsApiService> sheetsApiServiceProvider;

  private SheetsCreateNewModule_Factory(Provider<SheetsApiService> sheetsApiServiceProvider) {
    this.sheetsApiServiceProvider = sheetsApiServiceProvider;
  }

  @Override
  public SheetsCreateNewModule get() {
    return newInstance(sheetsApiServiceProvider.get());
  }

  public static SheetsCreateNewModule_Factory create(
      Provider<SheetsApiService> sheetsApiServiceProvider) {
    return new SheetsCreateNewModule_Factory(sheetsApiServiceProvider);
  }

  public static SheetsCreateNewModule newInstance(SheetsApiService sheetsApiService) {
    return new SheetsCreateNewModule(sheetsApiService);
  }
}
