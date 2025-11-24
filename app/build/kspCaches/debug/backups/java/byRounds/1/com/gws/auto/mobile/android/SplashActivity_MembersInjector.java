package com.gws.auto.mobile.android;

import com.gws.auto.mobile.android.data.repository.SettingsRepository;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;

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
public final class SplashActivity_MembersInjector implements MembersInjector<SplashActivity> {
  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private SplashActivity_MembersInjector(Provider<SettingsRepository> settingsRepositoryProvider) {
    this.settingsRepositoryProvider = settingsRepositoryProvider;
  }

  @Override
  public void injectMembers(SplashActivity instance) {
    injectSettingsRepository(instance, settingsRepositoryProvider.get());
  }

  public static MembersInjector<SplashActivity> create(
      Provider<SettingsRepository> settingsRepositoryProvider) {
    return new SplashActivity_MembersInjector(settingsRepositoryProvider);
  }

  @InjectedFieldSignature("com.gws.auto.mobile.android.SplashActivity.settingsRepository")
  public static void injectSettingsRepository(SplashActivity instance,
      SettingsRepository settingsRepository) {
    instance.settingsRepository = settingsRepository;
  }
}
