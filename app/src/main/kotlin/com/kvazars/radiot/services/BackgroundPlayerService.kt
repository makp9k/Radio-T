package com.kvazars.radiot.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import com.kvazars.radiot.R
import com.kvazars.radiot.RadioTApplication
import com.kvazars.radiot.domain.news.models.NewsItem
import com.kvazars.radiot.domain.util.Optional
import com.kvazars.radiot.domain.util.addTo
import com.kvazars.radiot.ui.main.MainScreenActivity
import io.reactivex.disposables.CompositeDisposable


class BackgroundPlayerService : Service() {

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "default"

        fun createLaunchIntent(context: Context): Intent {
            return Intent(context, BackgroundPlayerService::class.java)
        }
    }

    private val disposableBag = CompositeDisposable()
    private lateinit var notificationManager: NotificationManager

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val appComponent = RadioTApplication.getAppComponent(this)
        appComponent
            .getNewsInteractor()
            .activeNews
            .subscribe(
                { updateActiveNewsNotification(it) },
                { it.printStackTrace() }
            )
            .addTo(disposableBag)
    }

    override fun onDestroy() {
        disposableBag.clear()
        notificationManager.cancel(NOTIFICATION_ID)
        super.onDestroy()
    }

    private fun updateActiveNewsNotification(news: Optional<NewsItem>) {
        val notification = createNotification(news)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            createChannel()
        }
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(CHANNEL_ID, "Stream playback", importance)

        channel.setShowBadge(false)
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(news: Optional<NewsItem>): Notification {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_audiotrack)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(false)
            .setOngoing(true)
            .setContentIntent(createContentIntent())

        val newsItem = news.value
        if (newsItem != null) {
            builder.setContentTitle(newsItem.title)
            builder.setContentText(newsItem.snippet)
            builder.setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(newsItem.snippet)
            )
        } else {
            builder.setContentTitle("No active news")
        }

        return builder.build()
    }

    private fun createContentIntent(): PendingIntent {
        val intent = Intent(this, MainScreenActivity::class.java)

        intent.action = Intent.ACTION_MAIN
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        return PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

}