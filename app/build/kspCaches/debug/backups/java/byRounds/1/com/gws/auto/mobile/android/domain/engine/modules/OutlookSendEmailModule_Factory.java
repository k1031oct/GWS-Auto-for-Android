package com.gws.auto.mobile.android.domain.engine.modules;

import com.gws.auto.mobile.android.data.remote.OutlookApiService;
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
public final class OutlookSendEmailModule_Factory implements Factory<OutlookSendEmailModule> {
  private final Provider<OutlookApiService> outlookApiServiceProvider;

  private OutlookSendEmailModule_Factory(Provider<OutlookApiService> outlookApiServiceProvider) {
    this.outlookApiServiceProvider = outlookApiServiceProvider;
  }

  @Override
  public OutlookSendEmailModule get() {
    return newInstance(outlookApiServiceProvider.get());
  }

  public static OutlookSendEmailModule_Factory create(
      Provider<OutlookApiService> outlookApiServiceProvider) {
    return new OutlookSendEmailModule_Factory(outlookApiServiceProvider);
  }

  public static OutlookSendEmailModule newInstance(OutlookApiService outlookApiService) {
    return new OutlookSendEmailModule(outlookApiService);
  }
}
