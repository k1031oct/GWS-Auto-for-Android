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
public final class DriveCopyFileModule_Factory implements Factory<DriveCopyFileModule> {
  private final Provider<DriveApiService> driveApiServiceProvider;

  private DriveCopyFileModule_Factory(Provider<DriveApiService> driveApiServiceProvider) {
    this.driveApiServiceProvider = driveApiServiceProvider;
  }

  @Override
  public DriveCopyFileModule get() {
    return newInstance(driveApiServiceProvider.get());
  }

  public static DriveCopyFileModule_Factory create(
      Provider<DriveApiService> driveApiServiceProvider) {
    return new DriveCopyFileModule_Factory(driveApiServiceProvider);
  }

  public static DriveCopyFileModule newInstance(DriveApiService driveApiService) {
    return new DriveCopyFileModule(driveApiService);
  }
}
