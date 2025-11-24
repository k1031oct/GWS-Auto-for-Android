package com.gws.auto.mobile.android.ui.settings;

import com.google.firebase.auth.FirebaseAuth;
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
public final class MainSettingsFragment_MembersInjector implements MembersInjector<MainSettingsFragment> {
  private final Provider<FirebaseAuth> authProvider;

  private MainSettingsFragment_MembersInjector(Provider<FirebaseAuth> authProvider) {
    this.authProvider = authProvider;
  }

  @Override
  public void injectMembers(MainSettingsFragment instance) {
    injectAuth(instance, authProvider.get());
  }

  public static MembersInjector<MainSettingsFragment> create(Provider<FirebaseAuth> authProvider) {
    return new MainSettingsFragment_MembersInjector(authProvider);
  }

  @InjectedFieldSignature("com.gws.auto.mobile.android.ui.settings.MainSettingsFragment.auth")
  public static void injectAuth(MainSettingsFragment instance, FirebaseAuth auth) {
    instance.auth = auth;
  }
}
