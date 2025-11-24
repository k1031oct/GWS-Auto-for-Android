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
public final class CopyPasteSheetValuesModule_Factory implements Factory<CopyPasteSheetValuesModule> {
  private final Provider<SheetsApiService> sheetsApiServiceProvider;

  private CopyPasteSheetValuesModule_Factory(Provider<SheetsApiService> sheetsApiServiceProvider) {
    this.sheetsApiServiceProvider = sheetsApiServiceProvider;
  }

  @Override
  public CopyPasteSheetValuesModule get() {
    return newInstance(sheetsApiServiceProvider.get());
  }

  public static CopyPasteSheetValuesModule_Factory create(
      Provider<SheetsApiService> sheetsApiServiceProvider) {
    return new CopyPasteSheetValuesModule_Factory(sheetsApiServiceProvider);
  }

  public static CopyPasteSheetValuesModule newInstance(SheetsApiService sheetsApiService) {
    return new CopyPasteSheetValuesModule(sheetsApiService);
  }
}
