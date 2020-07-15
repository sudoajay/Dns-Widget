package com.sudoajay.dnswidget.tileClasses

import android.app.Activity
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.sudoajay.dnswidget.R
import com.sudoajay.dnswidget.activity.BaseActivity
import com.sudoajay.dnswidget.vpnClasses.AdVpnService
import com.sudoajay.dnswidget.vpnClasses.Command


class VpnTransparentClass : AppCompatActivity() {
    val TAG = "VpnTransparentClass"
    private var requestDnsCode = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vpn_transparent_class)

        // Get intent, action and MIME type
        // Get intent, action and MIME type
        val intent = intent


        if (intent.action.equals("startService")) {
            Log.i(TAG, "Attempting to connect")
            val vpnIntent = VpnService.prepare(applicationContext)
            if (vpnIntent != null) {
                Log.i(TAG, "Intent Not  Null ")
                startActivityForResult(vpnIntent, requestDnsCode)
            } else {
                Log.i(TAG, "Intent Null ")
                onActivityResult(requestDnsCode, Activity.RESULT_OK, null)
            }
        }else if(intent.action.equals("StopService")){
            stopService()
        }
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestDnsCode && resultCode == Activity.RESULT_CANCELED)
            errorVpnService()
        else if (requestCode == requestDnsCode && resultCode == Activity.RESULT_OK) {

            val startIntent = Intent(applicationContext, AdVpnService::class.java)
            startIntent.putExtra("COMMAND", Command.START.ordinal)
            applicationContext.startService(startIntent)

        }

    }

    private fun errorVpnService() {
        val builder: AlertDialog.Builder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AlertDialog.Builder(
                    applicationContext,
                    if (!BaseActivity.isDarkMode(applicationContext)) android.R.style.Theme_Material_Light_Dialog_Alert else android.R.style.Theme_Material_Dialog_Alert
                )
            } else {
                AlertDialog.Builder(applicationContext)
            }


        builder.setTitle(applicationContext.getString(R.string.error_vpn_text))
            .setMessage(applicationContext.getString(R.string.could_not_configure_vpn_service))
            .setNegativeButton("Ok") { _, _ ->

            }
            .setIcon(R.drawable.ic_error)
            .setCancelable(true)
            .show()
    }

    private fun stopService() {

        val stopIntent = Intent(applicationContext, AdVpnService::class.java)
        stopIntent.putExtra("COMMAND", Command.STOP.ordinal)
        applicationContext.startService(stopIntent)


    }
}