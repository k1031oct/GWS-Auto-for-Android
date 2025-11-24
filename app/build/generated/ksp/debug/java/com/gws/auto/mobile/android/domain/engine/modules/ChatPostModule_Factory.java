package com.gws.auto.mobile.android.domain.engine.modules;

import com.gws.auto.mobile.android.data.remote.ChatApiService;
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
public final class ChatPostModule_Factory implements Factory<ChatPostModule> {
  private final Provider<ChatApiService> chatApiServiceProvider;

  private ChatPostModule_Factory(Provider<ChatApiService> chatApiServiceProvider) {
    this.chatApiServiceProvider = chatApiServiceProvider;
  }

  @Override
  public ChatPostModule get() {
    return newInstance(chatApiServiceProvider.get());
  }

  public static ChatPostModule_Factory create(Provider<ChatApiService> chatApiServiceProvider) {
    return new ChatPostModule_Factory(chatApiServiceProvider);
  }

  public static ChatPostModule newInstance(ChatApiService chatApiService) {
    return new ChatPostModule(chatApiService);
  }
}
