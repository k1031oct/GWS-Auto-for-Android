package com.gws.auto.mobile.android.data.repository;

import android.content.SharedPreferences;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class UserPreferencesRepository_Factory implements Factory<UserPreferencesRepository> {
  private final Provider<SharedPreferences> prefsProvider;

  private UserPreferencesRepository_Factory(Provider<SharedPreferences> prefsProvider) {
    this.prefsProvider = prefsProvider;
  }

  @Override
  public UserPreferencesRepository get() {
    return newInstance(prefsProvider.get());
  }

  public static UserPreferencesRepository_Factory create(
      Provider<SharedPreferences> prefsProvider) {
    return new UserPreferencesRepository_Factory(prefsProvider);
  }

  public static UserPreferencesRepository newInstance(SharedPreferences prefs) {
    return new UserPreferencesRepository(prefs);
  }
}
