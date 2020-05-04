package com.sudoajay.dnswidget.vpnClasses

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.net.VpnService
import androidx.core.app.NotificationCompat
import com.sudoajay.dnswidget.R

class MyVpnService : VpnService(){



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startDns()

        return Service.START_STICKY
    }

    private fun startDns(){

        val builder = NotificationCompat.Builder(applicationContext, DnsNotification.channelId)
        DnsNotification(applicationContext).notify("Here We Goo" , "Active",builder)
        startForeground(DnsNotification.notificationTagId, builder.build())
    }


}