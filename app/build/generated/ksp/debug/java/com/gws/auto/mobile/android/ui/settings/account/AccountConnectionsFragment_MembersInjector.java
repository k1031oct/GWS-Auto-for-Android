package com.gws.auto.mobile.android.ui.settings.account;

import com.google.firebase.auth.FirebaseAuth;
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
public final class AccountConnectionsFragment_MembersInjector implements MembersInjector<AccountConnectionsFragment> {
  private final Provider<FirebaseAuth> authProvider;

  private final Provider<GoogleApiAuthorizer> authorizerProvider;

  private AccountConnectionsFragment_MembersInjector(Provider<FirebaseAuth> authProvider,
      Provider<GoogleApiAuthorizer> authorizerProvider) {
    this.authProvider = authProvider;
    this.authorizerProvider = authorizerProvider;
  }

  @Override
  public void injectMembers(AccountConnectionsFragment instance) {
    injectAuth(instance, authProvider.get());
    injectAuthorizer(instance, authorizerProvider.get());
  }

  public static MembersInjector<AccountConnectionsFragment> create(
      Provider<FirebaseAuth> authProvider, Provider<GoogleApiAuthorizer> authorizerProvider) {
    return new AccountConnectionsFragment_MembersInjector(authProvider, authorizerProvider);
  }

  @InjectedFieldSignature("com.gws.auto.mobile.android.ui.settings.account.AccountConnectionsFragment.auth")
  public static void injectAuth(AccountConnectionsFragment instance, FirebaseAuth auth) {
    instance.auth = auth;
  }

  @InjectedFieldSignature("com.gws.auto.mobile.android.ui.settings.account.AccountConnectionsFragment.authorizer")
  public static void injectAuthorizer(AccountConnectionsFragment instance,
      GoogleApiAuthorizer authorizer) {
    instance.authorizer = authorizer;
  }
}
