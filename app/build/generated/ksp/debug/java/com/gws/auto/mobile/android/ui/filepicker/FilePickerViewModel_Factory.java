package com.gws.auto.mobile.android.ui.filepicker;

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
public final class FilePickerViewModel_Factory implements Factory<FilePickerViewModel> {
  private final Provider<DriveApiService> driveApiServiceProvider;

  private FilePickerViewModel_Factory(Provider<DriveApiService> driveApiServiceProvider) {
    this.driveApiServiceProvider = driveApiServiceProvider;
  }

  @Override
  public FilePickerViewModel get() {
    return newInstance(driveApiServiceProvider.get());
  }

  public static FilePickerViewModel_Factory create(
      Provider<DriveApiService> driveApiServiceProvider) {
    return new FilePickerViewModel_Factory(driveApiServiceProvider);
  }

  public static FilePickerViewModel newInstance(DriveApiService driveApiService) {
    return new FilePickerViewModel(driveApiService);
  }
}
