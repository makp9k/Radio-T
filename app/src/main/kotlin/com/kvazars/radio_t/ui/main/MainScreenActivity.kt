package com.kvazars.radio_t.ui.main

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.kvazars.radio_t.R
import com.kvazars.radio_t.ui.online.OnlineScreenFragment

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
        val viewPager = findViewById(R.id.view_pager) as ViewPager
        val menu = findViewById(R.id.bottom_navigation_menu) as BottomNavigationView

        viewPager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return when (position) {
                    0 -> OnlineScreenFragment()
                    1 -> OnlineScreenFragment()
                    2 -> OnlineScreenFragment()
                    else -> OnlineScreenFragment()
                }
            }

            override fun getCount(): Int {
                return 3
            }
        }
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                menu.selectedItemId = when (position) {
                    0 -> R.id.action_online
                    1 -> R.id.action_news
                    2 -> R.id.action_chat
                    else -> R.id.action_online
                }
            }

        })
    }

    private fun initBottomNavigation() {
        val viewPager = findViewById(R.id.view_pager) as ViewPager
        val menu = findViewById(R.id.bottom_navigation_menu) as BottomNavigationView
        menu.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_online -> viewPager.currentItem = 0
                R.id.action_news -> viewPager.currentItem = 1
                R.id.action_chat -> viewPager.currentItem = 2
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