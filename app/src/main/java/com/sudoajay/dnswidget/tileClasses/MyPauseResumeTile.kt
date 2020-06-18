package com.sudoajay.dnswidget.tileClasses

import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.sudoajay.dnswidget.R

@RequiresApi(Build.VERSION_CODES.N)
class MyPauseResumeTile: TileService(){

    override fun onClick() {
        super.onClick()

        // Called when the user click the tile
    }

    override fun onTileRemoved() {
        super.onTileRemoved()

        // Do something when the user removes the Tile
    }

    override fun onTileAdded() {
        super.onTileAdded()

        // Do something when the user add the Tile

        val tile = qsTile
        tile.state = Tile.STATE_UNAVAILABLE


        tile.label = getString(R.string.pause_text)

        tile.icon = Icon.createWithResource(this, R.drawable.ic_pause)
        tile.updateTile()

        Toast.makeText(applicationContext, "tile added", Toast.LENGTH_SHORT).show()
    }

    override fun onStartListening() {
        super.onStartListening()

        // Called when the Tile becomes visible
    }

    override fun onStopListening() {
        super.onStopListening()

        // Called when the tile is no longer visible
    }
}