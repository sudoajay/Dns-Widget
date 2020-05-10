package com.sudoajay.dnswidget.vpnClasses

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sudoajay.dnswidget.R
import com.sudoajay.dnswidget.helper.Connectivity

class NetworkChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val getNetworkProvider = Connectivity.getNetworkProvider(context!!)

        when(getNetworkProvider){

        }

       if (getNetworkProvider.equals(context.getString(R.string.no_internet_text))){

       }else{

       }
    }
}