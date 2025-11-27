package com.gws.auto.mobile.android

import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.lang.ref.WeakReference
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        // WorkManager initialization is now handled by Configuration.Provider
        // WorkManager.initialize(this, workManagerConfig) - Removed manual init

        FirebaseApp.initializeApp(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        createNotificationChannel()
        registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Schedule Notifications"
            val descriptionText = "Notifications for scheduled workflow executions"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("schedule_notifications", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private val activityLifecycleCallbacks = object : ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            currentActivity = WeakReference(activity)
        }

        override fun onActivityStarted(activity: Activity) {
            currentActivity = WeakReference(activity)
        }

        override fun onActivityResumed(activity: Activity) {
            currentActivity = WeakReference(activity)
        }

        override fun onActivityPaused(activity: Activity) {
            // No-op
        }

        override fun onActivityStopped(activity: Activity) {
            // No-op
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            // No-op
        }

        override fun onActivityDestroyed(activity: Activity) {
            if (currentActivity?.get() == activity) {
                currentActivity = null
            }
        }
    }

    companion object {
        var currentActivity: WeakReference<Activity>? = null
        
        val isForeground: Boolean
            get() = currentActivity?.get() != null
    }
}
