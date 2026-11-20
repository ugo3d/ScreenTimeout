package com.example.screentimeout

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class ScreenWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        ids.forEach { manager.updateAppWidget(it, buildViews(context)) }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_TOGGLE) {
            if (TimeoutUtil.canWrite(context)) TimeoutUtil.toggle(context)
            updateAll(context)
        }
    }

    companion object {
        const val ACTION_TOGGLE = "com.example.screentimeout.TOGGLE"

        fun updateAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, ScreenWidgetProvider::class.java))
            ids.forEach { manager.updateAppWidget(it, buildViews(context)) }
        }

        private fun buildViews(context: Context): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.widget_skull)
            val long = TimeoutUtil.isLong(context)
            views.setImageViewResource(
                R.id.widget_skull,
                if (long) R.drawable.ic_punisher_red else R.drawable.ic_punisher
            )
            val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            val pending = if (TimeoutUtil.canWrite(context)) {
                PendingIntent.getBroadcast(
                    context, 0,
                    Intent(context, ScreenWidgetProvider::class.java).setAction(ACTION_TOGGLE),
                    flags
                )
            } else {
                // senza permesso, il tocco apre l'app per concederlo
                PendingIntent.getActivity(
                    context, 1,
                    Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                    flags
                )
            }
            views.setOnClickPendingIntent(R.id.widget_skull, pending)
            return views
        }
    }
}
