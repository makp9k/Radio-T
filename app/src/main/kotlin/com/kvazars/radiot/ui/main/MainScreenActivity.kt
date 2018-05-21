package com.kvazars.radiot.ui.main

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import com.kvazars.radiot.R
import com.kvazars.radiot.RadioTApplication
import com.kvazars.radiot.ui.base.BaseActivity
import com.kvazars.radiot.ui.chat.ChatScreenFragment
import com.kvazars.radiot.ui.news.NewsScreenFragment
import com.kvazars.radiot.ui.stream.StreamScreenFragment
import kotlinx.android.synthetic.main.screen_main.*
import kotlin.properties.Delegates

/**
 * Created by Leo on 08.04.2017.
 */
class MainScreenActivity : BaseActivity(), MainScreenContract.View {
    //region CONSTANTS -----------------------------------------------------------------------------

    companion object {
        private const val STREAM_FRAGMENT_TAG = "stream"
        private const val NEWS_FRAGMENT_TAG = "news"
        private const val CHAT_FRAGMENT_TAG = "chat"
    }

    //endregion

    //region CLASS VARIABLES -----------------------------------------------------------------------

    private lateinit var fragmentStream: Fragment
    private lateinit var fragmentNews: Fragment
    private lateinit var fragmentChat: Fragment
    private var currentFragment: Fragment by Delegates.notNull()

    private lateinit var presenter: MainScreenPresenter

    //endregion

    //region LIFE CYCLE ----------------------------------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.screen_main)

        if (savedInstanceState == null) {
            addAllFragments()
        } else {
            findAllFragments()
        }
        initBottomNavigation()

        val appComponent = RadioTApplication.getAppComponent(this)
        presenter = MainScreenPresenter(this, appComponent.getNewsInteractor())
    }

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    override fun showReconnectSnackbar() {
        Snackbar.make(fragment_container, R.string.internet_connection_error, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.reconnect, { presenter.onReconnectClick() })
            .show()
    }

    private fun initBottomNavigation() {
        bottom_navigation_menu.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_stream -> showFragment(fragmentStream)
                R.id.action_news -> {
                    showFragment(fragmentNews)
                }
                R.id.action_chat -> showFragment(fragmentChat)
                else -> {
                }
            }
            true
        }
    }

    private fun addAllFragments() {
        fragmentStream = StreamScreenFragment()
        fragmentNews = NewsScreenFragment()
        fragmentChat = ChatScreenFragment()

        currentFragment = fragmentStream

        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, fragmentStream, STREAM_FRAGMENT_TAG)
            .add(R.id.fragment_container, fragmentNews, NEWS_FRAGMENT_TAG)
            .add(R.id.fragment_container, fragmentChat, CHAT_FRAGMENT_TAG)
            .hide(fragmentNews)
            .hide(fragmentChat)
            .commit()
    }

    private fun findAllFragments() {
        fragmentStream = supportFragmentManager.findFragmentByTag(STREAM_FRAGMENT_TAG)
        fragmentNews = supportFragmentManager.findFragmentByTag(NEWS_FRAGMENT_TAG)
        fragmentChat = supportFragmentManager.findFragmentByTag(CHAT_FRAGMENT_TAG)
        if (!fragmentStream.isHidden) currentFragment = fragmentStream
        else if (!fragmentNews.isHidden) currentFragment = fragmentNews
        else if (!fragmentChat.isHidden) currentFragment = fragmentChat
    }

    private fun showFragment(fragment: Fragment) {
        if (fragment == currentFragment) return

        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .show(fragment)
            .hide(currentFragment)
            .commit()

        currentFragment = fragment
    }

    override fun onBackPressed() {
        if (bottom_navigation_menu.selectedItemId != R.id.action_stream) {
            bottom_navigation_menu.selectedItemId = R.id.action_stream
        } else {
            super.onBackPressed()
        }
    }

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}