package com.kvazars.radio_t.ui.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.kvazars.radio_t.R
import com.kvazars.radio_t.ui.stream.StreamScreenFragment
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by Leo on 08.04.2017.
 */
class MainScreenActivity : AppCompatActivity(), MainScreenContract.View {
    //region CONSTANTS -----------------------------------------------------------------------------

    //endregion

    //region CLASS VARIABLES -----------------------------------------------------------------------

    //endregion

    //region LIFE CYCLE ----------------------------------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        initViewPager()
        initBottomNavigation()

        MainScreenPresenter(this)
    }

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    private fun initViewPager() {
        view_pager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return when (position) {
                    0 -> StreamScreenFragment()
                    1 -> StreamScreenFragment()
                    2 -> StreamScreenFragment()
                    else -> StreamScreenFragment()
                }
            }

            override fun getCount(): Int {
                return 1
            }
        }
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                bottom_navigation_menu.selectedItemId = when (position) {
                    0 -> R.id.action_online
                    1 -> R.id.action_news
                    2 -> R.id.action_chat
                    else -> R.id.action_online
                }
            }

        })
    }

    private fun initBottomNavigation() {
        bottom_navigation_menu.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_online -> view_pager.currentItem = 0
                R.id.action_news -> view_pager.currentItem = 1
                R.id.action_chat -> view_pager.currentItem = 2
                else -> {
                }
            }
            true
        }
    }

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}