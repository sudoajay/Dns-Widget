package com.sudoajay.dnswidget.tileClasses

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_MULTIPLE_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.drawable.Icon
import android.os.Build
import android.os.IBinder
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import androidx.annotation.RequiresApi
import com.sudoajay.dnswidget.R
import com.sudoajay.dnswidget.helper.CustomToast
import com.sudoajay.dnswidget.vpnClasses.AdVpnService
import com.sudoajay.dnswidget.vpnClasses.Command


@RequiresApi(Build.VERSION_CODES.N)
class MyStartStopTile: TileService(){


    override fun onClick() {


        // Called when the user click the tile
        super.onClick()
        val tile = qsTile

        val isActive =
            tile.state == Tile.STATE_ACTIVE
        if (isActive) {
            tile.state = Tile.STATE_INACTIVE
            tile.label = getString(R.string.start_text)
            tile.icon = Icon.createWithResource(
                this,
                R.drawable.ic_start
            )

            val service = Intent(this, AdVpnService::class.java)
            service.putExtra("COMMAND", Command.STOP.ordinal)
            startService(service)


        } else {

            tile.state = Tile.STATE_ACTIVE
            tile.icon = Icon.createWithResource(
                this, R.drawable.ic_stop
            )
            tile.label = getString(R.string.stop_text)
            CustomToast.toastIt(applicationContext, "Is Active")

            val startIntent = Intent(this, VpnTransparentClass::class.java)
            startIntent.action = "startService"
            startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(FLAG_ACTIVITY_NEW_TASK)
            startActivity(startIntent)


        }
        tile.updateTile()

        // Called when the user click the tile
    }




    override fun onTileAdded() {
        super.onTileAdded()



        CustomToast.toastIt(
            applicationContext,
            getString(R.string.start_stop_text) + " " + getString(R.string.added_text)
        )

        // Do something when the user add the Tile
    }

    override fun onTileRemoved() {
        super.onTileRemoved()

        CustomToast.toastIt(
            applicationContext,
            getString(R.string.start_stop_text) + " " + getString(R.string.removed_text)
        )

        // Do something when the user removes the Tile
    }


    override fun onStartListening() {
        super.onStartListening()
        // Called when the Tile becomes visible

        val tile = qsTile
        val value =
            getSharedPreferences("state", Context.MODE_PRIVATE).getBoolean("isDnsActive", false)

        if (value) {
            tile.state = Tile.STATE_ACTIVE
            tile.label = getString(R.string.stop_text)
            tile.icon = Icon.createWithResource(this, R.drawable.ic_stop)

        } else {
            tile.state = Tile.STATE_INACTIVE
            tile.label = getString(R.string.start_text)
            tile.icon = Icon.createWithResource(this, R.drawable.ic_start)
        }

        tile.updateTile()
    }

    override fun onStopListening() {
        super.onStopListening()

        // Called when the tile is no longer visible
    }


}