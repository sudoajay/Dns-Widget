package com.sudoajay.dnswidget.vpnClasses


import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.sudoajay.dnswidget.MainActivity
import com.sudoajay.dnswidget.R



class DnsNotification(private val context: Context) {
    private var notificationManager: NotificationManager? = null

    fun notify(title: String, text: String,builder :NotificationCompat.Builder  ) { // local variable
        // setup intent and passing value
        val intent = Intent(context, MainActivity::class.java)

        // setup according Which Type
// if There is no data match with query

        // now check for null notification manger
        if (notificationManager == null) {
            notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        // this check for android Oero In which Channel Id Come as New Feature
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            assert(notificationManager != null)
            var mChannel = notificationManager!!.getNotificationChannel(channelId)
            if (mChannel == null) {
                mChannel = NotificationChannel(channelId, title, importance)
                notificationManager!!.createNotificationChannel(mChannel)
            }
        }
        // Default ringtone
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        builder.addAction(0, context.getString(R.string.dns_pause_text),
              null)
            .addAction(0, context.getString(R.string.dns_turn_off_text),
                null)
            // Set appropriate defaults for the notification light, sound,
// and vibration.
            .setDefaults(Notification.DEFAULT_ALL) // Set required fields, including the small icon, the
// notification title, and text.
            .setContentTitle(title)
            .setContentText(text) // All fields below this line are optional.
// Use a default priority (recognized on devices running Android
// 4.1 or later)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setSound(uri) // Provide a large icon, shown with the notification in the
// notification drawer on devices running Android 3.0 or later.
// Set ticker text (preview) information for this notification.
            .setTicker(title) // Show a number. This is useful when stacking notifications of
// a single type.
            .setNumber(1)
            .setSmallIcon(R.drawable.ic_dns)
            .setColor(ContextCompat.getColor(context,R.color.notificationColor)) // If this notification relates to a past or upcoming event, you
// should set the relevant time information using the setWhen
// method below. If this call is omitted, the notification's
// timestamp will by set to the time at which it was shown.
// TODO: Call setWhen if this notification relates to a past or
// upcoming event. The sole argument to this method should be
// the notification timestamp in milliseconds.
//.setWhen(...)
// Set the pending intent to be initiated when the user touches
// the notification.
            .setContentIntent(
                PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT)) // Show an expanded list of items on devices running Android 4.1
// or later.
// Example additional actions for this notification. These will
// only show on devices running Android 4.1 or later, so you
// should ensure that the activity in this notification's
// content intent provides access to the same actions in
// another way.
// Automatically dismiss the notification when it is touched.
            .setAutoCancel(false)

        // check if there ia data with empty
// more and view button classification
        notify(builder.build())
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private fun notify(notification: Notification) {
        notificationManager!!.notify(notificationTag, notificationTagId, notification)
    }

    companion object {
        /**
         * The unique identifier for this type of notification.
         */
        private const val notificationTag = "Dns Notification Tag"
        const val notificationTagId = 10
        const val channelId: String = "DNS Notification"

    }

}