package com.gws.auto.mobile.android.domain.engine.modules;

import com.gws.auto.mobile.android.domain.service.DriveApiService;
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
public final class DuplicateSpreadsheetModule_Factory implements Factory<DuplicateSpreadsheetModule> {
  private final Provider<DriveApiService> driveApiServiceProvider;

  private DuplicateSpreadsheetModule_Factory(Provider<DriveApiService> driveApiServiceProvider) {
    this.driveApiServiceProvider = driveApiServiceProvider;
  }

  @Override
  public DuplicateSpreadsheetModule get() {
    return newInstance(driveApiServiceProvider.get());
  }

  public static DuplicateSpreadsheetModule_Factory create(
      Provider<DriveApiService> driveApiServiceProvider) {
    return new DuplicateSpreadsheetModule_Factory(driveApiServiceProvider);
  }

  public static DuplicateSpreadsheetModule newInstance(DriveApiService driveApiService) {
    return new DuplicateSpreadsheetModule(driveApiService);
  }
}
