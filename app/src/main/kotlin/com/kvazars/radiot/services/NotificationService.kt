package com.kvazars.radiot.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.AlarmManagerCompat
import android.support.v4.app.NotificationCompat
import com.kvazars.radiot.R
import com.kvazars.radiot.RadioTApplication

class NotificationService {

    companion object {
        fun setupAlarm(context: Context) {
            val streamInteractor = RadioTApplication.getAppComponent(context).streamInteractor()
            val nextAirDate = streamInteractor.getNextAirDate()

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            AlarmManagerCompat.setExactAndAllowWhileIdle(
                alarmManager,
                AlarmManager.RTC_WAKEUP,
                nextAirDate.toEpochSecond() * 1000,
                getPendingIntent(context)
            )

            println("Alarm was set to $nextAirDate")
        }

        private fun getPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getBroadcast(
                context,
                1,
                Intent(context, AlarmBroadcastReceiver::class.java),
                PendingIntent.FLAG_CANCEL_CURRENT
            )
        }
    }

    class AlarmBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            NotificationService.setupAlarm(context)

            val appPreferences = RadioTApplication.getAppComponent(context).appPreferences()
            if (!appPreferences.notificationsEnabled.get()) {
                return
            }

            val notification = NotificationCompat.Builder(context, NotificationUtils.NotificationChannelInfo.PRIMARY.id)
                .setContentTitle(context.getString(R.string.alarm_notification_title))
                .setContentText(context.getString(R.string.alarm_notification_text))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(getLaunchAppPendingIntent(context))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()

            NotificationUtils.notify(context, 1, notification)
        }

        private fun getLaunchAppPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getActivity(
                context,
                1,
                RadioTApplication.getAppLaunchIntent(context),
                PendingIntent.FLAG_CANCEL_CURRENT
            )
        }
    }

}