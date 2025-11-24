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
public final class DriveMoveFileModule_Factory implements Factory<DriveMoveFileModule> {
  private final Provider<DriveApiService> driveApiServiceProvider;

  private DriveMoveFileModule_Factory(Provider<DriveApiService> driveApiServiceProvider) {
    this.driveApiServiceProvider = driveApiServiceProvider;
  }

  @Override
  public DriveMoveFileModule get() {
    return newInstance(driveApiServiceProvider.get());
  }

  public static DriveMoveFileModule_Factory create(
      Provider<DriveApiService> driveApiServiceProvider) {
    return new DriveMoveFileModule_Factory(driveApiServiceProvider);
  }

  public static DriveMoveFileModule newInstance(DriveApiService driveApiService) {
    return new DriveMoveFileModule(driveApiService);
  }
}
