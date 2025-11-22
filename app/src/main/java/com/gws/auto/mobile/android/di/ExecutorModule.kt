package com.gws.auto.mobile.android.di

import com.gws.auto.mobile.android.domain.engine.ModuleExecutor
import com.gws.auto.mobile.android.domain.engine.modules.ChatPostModule
import com.gws.auto.mobile.android.domain.engine.modules.OutlookSendEmailModule
import com.gws.auto.mobile.android.domain.engine.modules.SlackPostModule
import com.gws.auto.mobile.android.domain.engine.modules.ToastNotificationModule
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap

/**
 * Hilt module for providing the map of all available [ModuleExecutor]s.
 * This uses Dagger's multibinding feature to dynamically construct the map,
 * allowing for easy addition of new modules without modifying the engine.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class ExecutorModule {

    @Binds
    @IntoMap
    @ModuleKey("ChatPost")
    abstract fun bindChatPostModule(executor: ChatPostModule): ModuleExecutor

    @Binds
    @IntoMap
    @ModuleKey("OutlookSendEmail")
    abstract fun bindOutlookSendEmailModule(executor: OutlookSendEmailModule): ModuleExecutor

    @Binds
    @IntoMap
    @ModuleKey("SlackPost")
    abstract fun bindSlackPostModule(executor: SlackPostModule): ModuleExecutor

    @Binds
    @IntoMap
    @ModuleKey("ToastNotification")
    abstract fun bindToastNotificationModule(executor: ToastNotificationModule): ModuleExecutor

}
