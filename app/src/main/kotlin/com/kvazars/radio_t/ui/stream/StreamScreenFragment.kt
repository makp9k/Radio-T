package com.kvazars.radio_t.ui.stream

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.kvazars.radio_t.R
import com.kvazars.radio_t.RadioTApplication
import kotlinx.android.synthetic.main.fragment_stream.*
import kotlinx.android.synthetic.main.view_active_news_card.view.*
import kotlinx.android.synthetic.main.view_stream_controls.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Leo on 12.04.2017.
 */
class StreamScreenFragment : Fragment(), StreamScreenContract.View {
    //region CONSTANTS -----------------------------------------------------------------------------

    //endregion

    //region CLASS VARIABLES -----------------------------------------------------------------------

    private val dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG)
    private val timeFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT)

    //endregion

    //region LIFE CYCLE ----------------------------------------------------------------------------

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_stream, container, false)
    }

    private lateinit var presenter: StreamScreenPresenter

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = StreamScreenPresenter(this, RadioTApplication.getAppComponent(context).getNewsInteractor())

        btn_toggle_playback.setOnClickListener { presenter.onPlaybackToggleClick() }
        btn_info.setOnClickListener { presenter.onInfoClick() }
        btn_settings.setOnClickListener { presenter.onSettingsClick() }
        active_news.setOnClickListener { presenter.onActiveNewsClick() }
    }

    override fun showReconnectSnackbar() {
        val v = view
        if (v != null) {
            Snackbar.make(coord, "Internet connection error", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Reconnect", { presenter.onReconnectClick() })
                    .show()
            Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show()
        }
    }

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    override fun setPlaybackState(state: StreamScreenContract.View.PlaybackState) {
        when (state) {
            StreamScreenContract.View.PlaybackState.BUFFERING -> btn_toggle_playback.setImageResource(R.drawable.ic_preferences)
            StreamScreenContract.View.PlaybackState.PAUSED -> btn_toggle_playback.setImageResource(R.drawable.ic_pause)
            StreamScreenContract.View.PlaybackState.PLAYING -> btn_toggle_playback.setImageResource(R.drawable.ic_info)
        }
    }

    override fun setActiveNews(news: StreamScreenContract.View.NewsViewModel) {
        active_news.header.text = news.title
        active_news.sub_header.text = formatNewsDateTime(news.timestamp)
        active_news.details.text = news.details
    }

    private fun formatNewsDateTime(timestamp: Long): String {
        val date = Date(timestamp)
        return String.format(Locale.getDefault(), "%s - %s", dateFormat.format(date), timeFormat.format(date))
    }

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}