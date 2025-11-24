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
public final class DriveCreateFolderModule_Factory implements Factory<DriveCreateFolderModule> {
  private final Provider<DriveApiService> driveApiServiceProvider;

  private DriveCreateFolderModule_Factory(Provider<DriveApiService> driveApiServiceProvider) {
    this.driveApiServiceProvider = driveApiServiceProvider;
  }

  @Override
  public DriveCreateFolderModule get() {
    return newInstance(driveApiServiceProvider.get());
  }

  public static DriveCreateFolderModule_Factory create(
      Provider<DriveApiService> driveApiServiceProvider) {
    return new DriveCreateFolderModule_Factory(driveApiServiceProvider);
  }

  public static DriveCreateFolderModule newInstance(DriveApiService driveApiService) {
    return new DriveCreateFolderModule(driveApiService);
  }
}
