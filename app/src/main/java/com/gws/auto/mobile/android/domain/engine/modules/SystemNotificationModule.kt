package com.gws.auto.mobile.android.domain.engine.modules

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.domain.engine.ExecutionContext
import com.gws.auto.mobile.android.domain.engine.ModuleExecutor
import com.gws.auto.mobile.android.domain.model.ExecutionResult
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * A workflow module that displays a system notification in the status bar.
 */
class SystemNotificationModule @Inject constructor(
    @ApplicationContext private val context: Context
) : ModuleExecutor {

    companion object {
        private const val CHANNEL_ID = "workflow_notifications"
        private const val CHANNEL_NAME = "Workflow Notifications"
        private const val NOTIFICATION_ID = 1001 // Simple fixed ID for now, or could be random/incremental
    }

    override suspend fun execute(context: ExecutionContext): ExecutionResult {
        val message = context.resolveVariables(context.module.parameters["message"] ?: "")
        val title = context.resolveVariables(context.module.parameters["title"] ?: "Workflow Notification")

        if (message.isBlank()) {
            return ExecutionResult.Success("Notification skipped: Message is empty.")
        }

        val notificationManager = this.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(
                    this.context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                return ExecutionResult.Error("Permission POST_NOTIFICATIONS not granted. Please allow notifications in App Settings.")
            }
        }

        createNotificationChannel(notificationManager)

        val notification = NotificationCompat.Builder(this.context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Fallback icon, should ideally use app icon
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)

        return ExecutionResult.Success("Notification displayed: $title - $message")
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications triggered by workflow modules"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
}
