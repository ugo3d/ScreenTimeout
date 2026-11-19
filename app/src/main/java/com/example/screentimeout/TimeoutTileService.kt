package com.example.screentimeout

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

private const val SHORT_MS = 15_000       // 15 secondi
private const val LONG_MS = 600_000       // 10 minuti

class TimeoutTileService : TileService() {

    override fun onStartListening() {
        updateTile()
    }

    override fun onClick() {
        if (!Settings.System.canWrite(this)) {
            openApp()          // serve il permesso "Modifica impostazioni di sistema"
            return
        }
        val current = Settings.System.getInt(
            contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, SHORT_MS
        )
        val next = if (current >= LONG_MS) SHORT_MS else LONG_MS
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, next)
        updateTile()
    }

    private fun updateTile() {
        val tile = qsTile ?: return
        val current = Settings.System.getInt(
            contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, SHORT_MS
        )
        val long = current >= LONG_MS
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
            val pi = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_IMMUTABLE
            )
            startActivityAndCollapse(pi)
        } else {
            startActivityAndCollapse(intent)
        }
    }
}
