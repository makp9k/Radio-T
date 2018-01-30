package com.kvazars.radiot.ui.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import com.kvazars.radiot.R
import com.kvazars.radiot.ui.chat.ChatScreenFragment
import com.kvazars.radiot.ui.news.NewsScreenFragment
import com.kvazars.radiot.ui.stream.StreamScreenFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.properties.Delegates

/**
 * Created by Leo on 08.04.2017.
 */
class MainScreenActivity : AppCompatActivity(), MainScreenContract.View {
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

    //endregion

    //region LIFE CYCLE ----------------------------------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            addAllFragments()
        } else {
            findAllFragments()
        }
        initBottomNavigation()

        MainScreenPresenter(this)
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

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

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

    private fun showFragment(fragment: Fragment) {
        if (fragment == currentFragment) return

        supportFragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .show(fragment)
                .hide(currentFragment)
                .commit()

        currentFragment = fragment
    }

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}