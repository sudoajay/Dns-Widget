package com.sudoajay.dnswidget.vpnClasses

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class BootComplete : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        AdVpnService.checkStartVpnOnBoot(context)
    }
}
