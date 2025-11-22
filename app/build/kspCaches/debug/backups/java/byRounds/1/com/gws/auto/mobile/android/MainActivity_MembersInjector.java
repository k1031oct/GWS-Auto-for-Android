package com.gws.auto.mobile.android;

import com.gws.auto.mobile.android.data.repository.HistoryRepository;
import com.gws.auto.mobile.android.data.repository.SettingsRepository;
import com.gws.auto.mobile.android.domain.service.GoogleApiAuthorizer;
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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<HistoryRepository> historyRepositoryProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private final Provider<GoogleApiAuthorizer> googleApiAuthorizerProvider;

  private MainActivity_MembersInjector(Provider<HistoryRepository> historyRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<GoogleApiAuthorizer> googleApiAuthorizerProvider) {
    this.historyRepositoryProvider = historyRepositoryProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.googleApiAuthorizerProvider = googleApiAuthorizerProvider;
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectHistoryRepository(instance, historyRepositoryProvider.get());
    injectSettingsRepository(instance, settingsRepositoryProvider.get());
    injectGoogleApiAuthorizer(instance, googleApiAuthorizerProvider.get());
  }

  public static MembersInjector<MainActivity> create(
      Provider<HistoryRepository> historyRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<GoogleApiAuthorizer> googleApiAuthorizerProvider) {
    return new MainActivity_MembersInjector(historyRepositoryProvider, settingsRepositoryProvider, googleApiAuthorizerProvider);
  }

  @InjectedFieldSignature("com.gws.auto.mobile.android.MainActivity.historyRepository")
  public static void injectHistoryRepository(MainActivity instance,
      HistoryRepository historyRepository) {
    instance.historyRepository = historyRepository;
  }

  @InjectedFieldSignature("com.gws.auto.mobile.android.MainActivity.settingsRepository")
  public static void injectSettingsRepository(MainActivity instance,
      SettingsRepository settingsRepository) {
    instance.settingsRepository = settingsRepository;
  }

  @InjectedFieldSignature("com.gws.auto.mobile.android.MainActivity.googleApiAuthorizer")
  public static void injectGoogleApiAuthorizer(MainActivity instance,
      GoogleApiAuthorizer googleApiAuthorizer) {
    instance.googleApiAuthorizer = googleApiAuthorizer;
  }
}
