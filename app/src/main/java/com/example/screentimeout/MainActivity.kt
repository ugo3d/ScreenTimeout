package com.example.screentimeout

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

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
            if (!TimeoutUtil.canWrite(this)) {
                startActivity(
                    Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                        .setData(Uri.parse("package:$packageName"))
                )
            } else {
                TimeoutUtil.toggle(this)
            }
            refresh()
        }
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun refresh() {
        if (!TimeoutUtil.canWrite(this)) {
            status.text = "Permesso mancante"
            button.text = "Concedi permesso"
        } else {
            status.text = if (TimeoutUtil.isLong(this)) "Spegnimento: 10 minuti" else "Spegnimento: 15 secondi"
            button.text = "Cambia"
        }
        ScreenWidgetProvider.updateAll(this)
    }
}
