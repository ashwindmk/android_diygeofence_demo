package com.ashwin.android.sample.diygeogencedemo

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import com.ashwin.android.library.diygeofence.GeofenceListener
import java.util.*

class MyGeofenceListener : GeofenceListener {
    override fun onEnter(context: Context, id: String) {
        showNotification(context, ("$id-enter").run { hashCode() }, "Entered $id", "${Date()}")
    }

    override fun onExit(context: Context, id: String) {
        showNotification(context, ("$id-exit").run { hashCode() }, "Entered $id", "${Date()}")
    }

    private fun showNotification(context: Context, id: Int, title: String, msg: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val builder = NotificationCompat.Builder(context, MainApplication.CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(title)
            .setContentText(msg)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(id, builder.build())
        }
    }
}
