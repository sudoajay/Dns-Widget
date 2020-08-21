package com.sudoajay.dnswidget.vpnClasses

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sudoajay.dnswidget.ui.setting.SettingConfiguration


class BootComplete : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        if (SettingConfiguration.getIsStartOnBoot(context))
            AdVpnService.checkStartVpnOnBoot(context)
    }
}
