package com.gws.auto.mobile.android.di

import dagger.MapKey
import kotlin.reflect.KClass

/**
 * A Dagger map key for identifying and injecting a specific [ModuleExecutor].
 * The value should correspond to the unique type name of the module.
 */
@MapKey
@Retention(AnnotationRetention.RUNTIME)
annotation class ModuleKey(val value: String)
