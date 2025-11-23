package com.gws.auto.mobile.android.domain.engine.modules;

import com.gws.auto.mobile.android.data.remote.SlackApiService;
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
public final class SlackPostModule_Factory implements Factory<SlackPostModule> {
  private final Provider<SlackApiService> slackApiServiceProvider;

  private SlackPostModule_Factory(Provider<SlackApiService> slackApiServiceProvider) {
    this.slackApiServiceProvider = slackApiServiceProvider;
  }

  @Override
  public SlackPostModule get() {
    return newInstance(slackApiServiceProvider.get());
  }

  public static SlackPostModule_Factory create(Provider<SlackApiService> slackApiServiceProvider) {
    return new SlackPostModule_Factory(slackApiServiceProvider);
  }

  public static SlackPostModule newInstance(SlackApiService slackApiService) {
    return new SlackPostModule(slackApiService);
  }
}
