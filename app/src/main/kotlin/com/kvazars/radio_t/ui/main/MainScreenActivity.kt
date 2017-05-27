package com.kvazars.radio_t.ui.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import com.kvazars.radio_t.R
import com.kvazars.radio_t.ui.stream.StreamScreenFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.properties.Delegates

/**
 * Created by Leo on 08.04.2017.
 */
class MainScreenActivity : AppCompatActivity(), MainScreenContract.View {
    //region CONSTANTS -----------------------------------------------------------------------------

    //endregion

    //region CLASS VARIABLES -----------------------------------------------------------------------

    private val fragmentStream: Fragment by lazy { StreamScreenFragment() }
    private var currentFragment: Fragment by Delegates.notNull<Fragment>()

    //endregion

    //region LIFE CYCLE ----------------------------------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        addAllFragments()
        initBottomNavigation()

        MainScreenPresenter(this)
    }

    private fun addAllFragments() {
        currentFragment = fragmentStream

        supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragmentStream)
                .commit()
    }

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    private fun initBottomNavigation() {
        bottom_navigation_menu.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_stream -> showFragment(fragmentStream)
                R.id.action_news -> {
                }
                R.id.action_chat -> {
                }
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
    }

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}