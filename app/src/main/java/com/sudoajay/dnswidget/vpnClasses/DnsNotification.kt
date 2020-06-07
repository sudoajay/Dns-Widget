package com.sudoajay.dnswidget.vpnClasses


import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.sudoajay.dnswidget.R


class DnsNotification(private val context: Context) {
    private var notificationManager: NotificationManager? = null

        fun notify(title: String, builder: NotificationCompat.Builder) { // local variable

            // now check for null notification manger
            if (notificationManager == null) {
                notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }

            // Default ringtone
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            builder.addAction(
            R.drawable.ic_pause, context.getString(R.string.notification_action_pause),
            PendingIntent.getService(
                context, AdVpnService.REQUEST_CODE_PAUSE, Intent(context, AdVpnService::class.java)
                    .putExtra("COMMAND", Command.PAUSE.ordinal), 0
            )
        )
            .addAction(
                R.drawable.ic_turn_off, context.getString(R.string.notification_action_turn_off),
                PendingIntent.getService(
                    context,
                    AdVpnService.REQUEST_CODE_PAUSE,
                    Intent(context, AdVpnService::class.java)
                        .putExtra("COMMAND", Command.PAUSE.ordinal),
                    0
                )
            )

            // Set appropriate defaults for the notification light, sound,
    // and vibration.
                .setDefaults(Notification.DEFAULT_ALL) // Set required fields, including the small icon, the
                .setContentTitle(title)
                .setContentText("") // All fields below this line are optional.
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setSound(uri) // Provide a large icon, shown with the notification in the
                .setSmallIcon(R.drawable.ic_dns)
                .color = ContextCompat.getColor(
                context,
                R.color.fabColor_DnsSpeedTest
            ) // If this notification relates to a past or upcoming event, you

            // check if there ia data with empty
// more and view button classification
            val notification: Notification = builder.build()
            notification.flags =
                notification.flags or (Notification.FLAG_NO_CLEAR or Notification.FLAG_ONGOING_EVENT)

            notify(notification)
        }


    private fun notify(notification: Notification) {
        notificationManager!!.notify(AdVpnService.NOTIFICATION_ID_STATE, notification)
    }



}