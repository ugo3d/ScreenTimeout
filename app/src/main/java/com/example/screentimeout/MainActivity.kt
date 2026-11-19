package com.example.screentimeout

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

private const val SHORT_MS = 15_000
private const val LONG_MS = 600_000

class MainActivity : AppCompatActivity() {

    private lateinit var status: TextView
    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        status = TextView(this).apply { textSize = 20f }
        button = Button(this)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 96, 48, 48)
            addView(status)
            addView(button)
        }
        setContentView(root)

        button.setOnClickListener {
            if (!Settings.System.canWrite(this)) {
                startActivity(
                    Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                        .setData(Uri.parse("package:$packageName"))
                )
            } else {
                val cur = Settings.System.getInt(
                    contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, SHORT_MS
                )
                Settings.System.putInt(
                    contentResolver, Settings.System.SCREEN_OFF_TIMEOUT,
                    if (cur >= LONG_MS) SHORT_MS else LONG_MS
                )
            }
            refresh()
        }
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun refresh() {
        if (!Settings.System.canWrite(this)) {
            status.text = "Permesso mancante"
            button.text = "Concedi permesso"
            return
        }
        val cur = Settings.System.getInt(
            contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, SHORT_MS
        )
        status.text = if (cur >= LONG_MS) "Spegnimento: 10 minuti" else "Spegnimento: 15 secondi"
        button.text = "Cambia"
    }
}
