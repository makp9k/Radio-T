package com.kvazars.radiot.services

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
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
            .newsInteractor()
            .activeNews
            .retryWhen { appComponent.reconnectTrigger() }
            .subscribe(
                { updateActiveNewsNotification(it) },
                { it.printStackTrace() }
            )
            .addTo(disposableBag)

        startForeground(NOTIFICATION_ID, createNotification(Optional.empty))
    }

    override fun onDestroy() {
        disposableBag.clear()
        notificationManager.cancel(NOTIFICATION_ID)
        super.onDestroy()
    }

    private fun updateActiveNewsNotification(news: Optional<NewsItem>) {
        val notification = createNotification(news)
        NotificationUtils.notify(this, NOTIFICATION_ID, notification)
    }

    private fun createNotification(news: Optional<NewsItem>): Notification {
        val builder = NotificationCompat.Builder(this, NotificationUtils.NotificationChannelInfo.PRIMARY.id)
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
            builder.setContentTitle(getString(R.string.stream_no_active_news))
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