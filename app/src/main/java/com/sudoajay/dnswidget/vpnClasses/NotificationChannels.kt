package com.sudoajay.dnswidget.vpnClasses

import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.sudoajay.dnswidget.R

/**
 * Static class containing IDs of notification channels and code to create them.
 */
object NotificationChannels {
    private const val GROUP_SERVICE = "com.sudoajay.dnswidget.notifications.service"
    private const val GROUP_UPDATE = "com.sudoajay.dnswidget.notifications.update"
    const val SERVICE_RUNNING = "com.sudoajay.dnswidget.notifications.service.running"
    const val SERVICE_PAUSED = "com.sudoajay.dnswidget.notifications.service.paused"
    const val SERVICE_NETWORK_SPEED = "com.sudoajay.dnswidget.notifications.service.network.speed"
    const val UPDATE_STATUS = "com.sudoajay.dnswidget.notifications.update.status"

    @RequiresApi(Build.VERSION_CODES.O)
    @JvmStatic
    fun onCreate(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannelGroup(
            NotificationChannelGroup(
                GROUP_SERVICE,
                context.getString(R.string.notifications_group_service)
            )
        )
        notificationManager.createNotificationChannelGroup(
            NotificationChannelGroup(
                GROUP_UPDATE,
                context.getString(R.string.notifications_group_updates)
            )
        )

        val runningChannel = NotificationChannel(
            SERVICE_RUNNING,
            context.getString(R.string.notifications_running),
            NotificationManager.IMPORTANCE_MIN
        )
        runningChannel.description = context.getString(R.string.notifications_running_desc)
        runningChannel.group = GROUP_SERVICE
        runningChannel.setShowBadge(false)
        notificationManager.createNotificationChannel(runningChannel)

        val pausedChannel = NotificationChannel(
            SERVICE_PAUSED,
            context.getString(R.string.notifications_paused),
            NotificationManager.IMPORTANCE_LOW
        )
        pausedChannel.description = context.getString(R.string.notifications_paused_desc)
        pausedChannel.group = GROUP_SERVICE
        pausedChannel.setShowBadge(false)
        notificationManager.createNotificationChannel(pausedChannel)

        val updateChannel = NotificationChannel(
            UPDATE_STATUS,
            context.getString(R.string.notifications_update),
            NotificationManager.IMPORTANCE_LOW
        )
        updateChannel.description = context.getString(R.string.notifications_update_desc)
        updateChannel.group = GROUP_UPDATE
        updateChannel.setShowBadge(false)
        notificationManager.createNotificationChannel(updateChannel)

        val networkSPeed = NotificationChannel(SERVICE_NETWORK_SPEED ,  context.getString(R.string.network_speed_text),NotificationManager.IMPORTANCE_MIN)


        networkSPeed.description = context.getString(R.string.notifications_speed_test_desc)
        networkSPeed.group = GROUP_UPDATE
        networkSPeed.setShowBadge(false)
        notificationManager.createNotificationChannel(networkSPeed)

    }
}