
package com.sudoajay.dnswidget.helper

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.sudoajay.dnswidget.R

object Connectivity {

    fun getNetworkProvider(context: Context): String? {

        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm.run {
                cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                    when {
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                            context.getString(R.string.wifi_text)
                        }
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                            context.getString(R.string.mobile_data_text)
                        }
                        hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> {
                            context.getString(R.string.vpn_text)
                        }
                        else -> context.getString(R.string.no_internet_text)
                    }
                }
            }
        } else {
            cm.run {
                @Suppress("DEPRECATION")
                if (cm.activeNetworkInfo != null) cm.activeNetworkInfo?.run {
                    when (type) {
                        ConnectivityManager.TYPE_WIFI -> {
                            context.getString(R.string.wifi_text)
                        }
                        ConnectivityManager.TYPE_MOBILE -> {
                            context.getString(R.string.mobile_data_text)
                        }
                        ConnectivityManager.TYPE_VPN -> {
                            context.getString(R.string.vpn_text)
                        }
                        else -> context.getString(R.string.no_internet_text)
                    }
                } else "Null"
            }
        }
    }

}