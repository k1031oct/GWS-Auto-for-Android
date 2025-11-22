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
public final class SheetsClearValuesModule_Factory implements Factory<SheetsClearValuesModule> {
  private final Provider<SheetsApiService> sheetsApiServiceProvider;

  private SheetsClearValuesModule_Factory(Provider<SheetsApiService> sheetsApiServiceProvider) {
    this.sheetsApiServiceProvider = sheetsApiServiceProvider;
  }

  @Override
  public SheetsClearValuesModule get() {
    return newInstance(sheetsApiServiceProvider.get());
  }

  public static SheetsClearValuesModule_Factory create(
      Provider<SheetsApiService> sheetsApiServiceProvider) {
    return new SheetsClearValuesModule_Factory(sheetsApiServiceProvider);
  }

  public static SheetsClearValuesModule newInstance(SheetsApiService sheetsApiService) {
    return new SheetsClearValuesModule(sheetsApiService);
  }
}
