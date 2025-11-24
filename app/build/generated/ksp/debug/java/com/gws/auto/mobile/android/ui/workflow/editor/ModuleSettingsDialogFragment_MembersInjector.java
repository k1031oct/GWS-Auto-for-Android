package com.gws.auto.mobile.android.ui.workflow.editor;

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
public final class ModuleSettingsDialogFragment_MembersInjector implements MembersInjector<ModuleSettingsDialogFragment> {
  private final Provider<GoogleApiAuthorizer> googleApiAuthorizerProvider;

  private ModuleSettingsDialogFragment_MembersInjector(
      Provider<GoogleApiAuthorizer> googleApiAuthorizerProvider) {
    this.googleApiAuthorizerProvider = googleApiAuthorizerProvider;
  }

  @Override
  public void injectMembers(ModuleSettingsDialogFragment instance) {
    injectGoogleApiAuthorizer(instance, googleApiAuthorizerProvider.get());
  }

  public static MembersInjector<ModuleSettingsDialogFragment> create(
      Provider<GoogleApiAuthorizer> googleApiAuthorizerProvider) {
    return new ModuleSettingsDialogFragment_MembersInjector(googleApiAuthorizerProvider);
  }

  @InjectedFieldSignature("com.gws.auto.mobile.android.ui.workflow.editor.ModuleSettingsDialogFragment.googleApiAuthorizer")
  public static void injectGoogleApiAuthorizer(ModuleSettingsDialogFragment instance,
      GoogleApiAuthorizer googleApiAuthorizer) {
    instance.googleApiAuthorizer = googleApiAuthorizer;
  }
}
