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

        // Configure a new interface from our VpnService instance. This must be done
// from inside a VpnService.
        val builders = Builder()

// Create a local TUN interface using predetermined addresses. In your app,
// you typically use values returned from the VPN gateway during handshaking.
        val localTunnel = builders
            .addAddress("192.0.2.1", 24)
            .addRoute("0.0.0.0", 0)
            .addDnsServer("8.8.8.8")
            .establish()
    }




}