package com.kvazars.radiot.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build


class NotificationUtils {
    enum class NotificationChannelInfo(val id: String, val channelName: String) {
        PRIMARY("default", "Primary");

        companion object {
            fun findById(id: String): NotificationChannelInfo {
                for (notificationChannelInfo in values()) {
                    if (notificationChannelInfo.id == id) {
                        return notificationChannelInfo
                    }
                }
                return PRIMARY
            }
        }
    }

    companion object {
        fun notify(context: Context, id: Int, notification: Notification) {
            val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            createNotificationChannel(notificationManager, notification)

            notificationManager.notify(id, notification)
        }

        private fun createNotificationChannel(notificationManager: NotificationManager, notification: Notification) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelId = notification.channelId
                val channelInfo = NotificationChannelInfo.findById(channelId)

                if (notificationManager.getNotificationChannel(channelInfo.id) == null) {
                    val channel = NotificationChannel(
                            channelInfo.id,
                            channelInfo.channelName,
                            NotificationManager.IMPORTANCE_DEFAULT
                    )
                    notificationManager.createNotificationChannel(channel)
                }
            }
        }
    }
}