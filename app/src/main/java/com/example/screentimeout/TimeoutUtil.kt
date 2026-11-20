package com.example.screentimeout

import android.content.Context
import android.provider.Settings

object TimeoutUtil {
    const val SHORT_MS = 15_000      // 15 secondi
    const val LONG_MS = 600_000      // 10 minuti

    fun current(ctx: Context): Int =
        Settings.System.getInt(ctx.contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, SHORT_MS)

    fun isLong(ctx: Context): Boolean = current(ctx) >= LONG_MS

    fun canWrite(ctx: Context): Boolean = Settings.System.canWrite(ctx)

    fun toggle(ctx: Context) {
        val next = if (isLong(ctx)) SHORT_MS else LONG_MS
        Settings.System.putInt(ctx.contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, next)
    }
}
