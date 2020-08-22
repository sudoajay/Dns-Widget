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
    const val SERVICE_RUNNING_Speed = "com.sudoajay.dnswidget.notifications.service.running_speed"
    const val SERVICE_RUNNING_More_Option = "com.sudoajay.dnswidget.notifications.service.running_more_option"
    const val SERVICE_PAUSED = "com.sudoajay.dnswidget.notifications.service.paused"
    const val PUSH_NOTIFICATION = "com.sudoajay.dnswidget.push.notifications."


    @RequiresApi(Build.VERSION_CODES.O)
    @JvmStatic
    fun notificationOnCreate(context: Context) {
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

        val speedRunningChannel = NotificationChannel(
            SERVICE_RUNNING_Speed,
            context.getString(R.string.notifications_running),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        speedRunningChannel.setSound(null , null)
        speedRunningChannel.description = context.getString(R.string.notifications_running_desc)
        speedRunningChannel.group = GROUP_SERVICE
        speedRunningChannel.setShowBadge(false)
        notificationManager.createNotificationChannel(speedRunningChannel)

        val moreOptionRunningChannel = NotificationChannel(
            SERVICE_RUNNING_More_Option,
            context.getString(R.string.notifications_running),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        moreOptionRunningChannel.description = context.getString(R.string.notifications_running_desc)
        moreOptionRunningChannel.group = GROUP_SERVICE
        moreOptionRunningChannel.setShowBadge(false)
        notificationManager.createNotificationChannel(moreOptionRunningChannel)

        val pausedChannel = NotificationChannel(
            SERVICE_PAUSED,
            context.getString(R.string.notifications_paused),
            NotificationManager.IMPORTANCE_LOW
        )
        pausedChannel.description = context.getString(R.string.notifications_paused_desc)
        pausedChannel.group = GROUP_SERVICE
        pausedChannel.setShowBadge(false)
        notificationManager.createNotificationChannel(pausedChannel)


        val firebaseChannel = NotificationChannel(
            PUSH_NOTIFICATION,
            context.getString(R.string.firebase_channel_id),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        firebaseChannel.description = context.getString(R.string.firebase_channel_id)
        firebaseChannel.group = GROUP_SERVICE
        firebaseChannel.setShowBadge(false)
        notificationManager.createNotificationChannel(firebaseChannel)


    }
}