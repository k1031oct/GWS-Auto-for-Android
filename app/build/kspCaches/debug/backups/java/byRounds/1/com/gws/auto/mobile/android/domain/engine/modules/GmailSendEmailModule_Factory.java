package com.gws.auto.mobile.android.domain.engine.modules;

import com.gws.auto.mobile.android.domain.service.GmailApiService;
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
public final class GmailSendEmailModule_Factory implements Factory<GmailSendEmailModule> {
  private final Provider<GmailApiService> gmailApiServiceProvider;

  private GmailSendEmailModule_Factory(Provider<GmailApiService> gmailApiServiceProvider) {
    this.gmailApiServiceProvider = gmailApiServiceProvider;
  }

  @Override
  public GmailSendEmailModule get() {
    return newInstance(gmailApiServiceProvider.get());
  }

  public static GmailSendEmailModule_Factory create(
      Provider<GmailApiService> gmailApiServiceProvider) {
    return new GmailSendEmailModule_Factory(gmailApiServiceProvider);
  }

  public static GmailSendEmailModule newInstance(GmailApiService gmailApiService) {
    return new GmailSendEmailModule(gmailApiService);
  }
}
