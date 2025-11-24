package com.gws.auto.mobile.android.domain.engine

import javax.inject.Inject
import javax.inject.Provider

class ModuleExecutorProvider @Inject constructor(
    private val executors: Map<String, @JvmSuppressWildcards Provider<ModuleExecutor>>
) {
    fun get(type: String): ModuleExecutor? {
        return executors[type]?.get()
    }

    fun getAvailableTypes(): Set<String> {
        return executors.keys
    }
}
