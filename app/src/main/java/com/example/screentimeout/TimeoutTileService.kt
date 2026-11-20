package com.example.screentimeout

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class TimeoutTileService : TileService() {

    override fun onStartListening() {
        updateTile()
    }

    override fun onClick() {
        if (!TimeoutUtil.canWrite(this)) {
            openApp()          // serve il permesso "Modifica impostazioni di sistema"
            return
        }
        TimeoutUtil.toggle(this)
        updateTile()
        ScreenWidgetProvider.updateAll(this)
    }

    private fun updateTile() {
        val tile = qsTile ?: return
        val long = TimeoutUtil.isLong(this)
        tile.state = if (long) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.label = "Schermo"
        if (Build.VERSION.SDK_INT >= 29) {
            tile.subtitle = if (long) "10 minuti" else "15 secondi"
        }
        tile.updateTile()
    }

    @Suppress("DEPRECATION")
    private fun openApp() {
        val intent = Intent(this, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT >= 34) {
            val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            startActivityAndCollapse(pi)
        } else {
            startActivityAndCollapse(intent)
        }
    }
}
