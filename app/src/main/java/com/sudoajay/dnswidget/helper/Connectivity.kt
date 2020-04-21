package com.sudoajay.dnswidget.helper

import android.content.Context
import android.net.ConnectivityManager

object Connectivity {

    fun getNetworkProvider(context: Context): String? {
        val cm =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return if (activeNetwork != null) {
            // connected to the internet
            if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) {
                "Wifi"
            } else  {
                "Mobile Data"
            }
        } else {
            "No Internet"
        }

    }
}