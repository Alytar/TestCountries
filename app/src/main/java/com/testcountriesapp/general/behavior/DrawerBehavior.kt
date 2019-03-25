package com.testcountriesapp.general.behavior

import android.app.Activity
import android.content.res.Configuration
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.testcountriesapp.R

class DrawerBehavior(
    private val activity: Activity,
    private val drawerLayout: DrawerLayout,
    private val toolbar: Toolbar,
    private val navigationView: NavigationView,
    private val invalidateOptionsMenu: () -> Unit
) : IDrawerBehavior {

    var drawerToggle: ActionBarDrawerToggle? = null

    init {
        drawerToggle = object : ActionBarDrawerToggle(
            activity,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ) {
            override fun onDrawerClosed(view: View) {
                super.onDrawerClosed(view)
                invalidateOptionsMenu()
            }

            override fun onDrawerOpened(view: View) {
                super.onDrawerOpened(view)
                invalidateOptionsMenu()
            }
        }
        drawerLayout.addDrawerListener(this)
    }

    override fun syncState() {
        drawerToggle?.syncState()
    }

    override fun openDrawer() {
        drawerLayout.openDrawer(navigationView)
    }

    override fun closeDrawer(): Boolean {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawers()
            return true
        }
        return false
    }

    override fun holdDrawer(hold: Boolean) {
        drawerLayout.setDrawerLockMode(if (hold) DrawerLayout.LOCK_MODE_LOCKED_CLOSED else DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        drawerToggle?.onConfigurationChanged(newConfig)
    }

    override fun onDrawerStateChanged(newState: Int) {
        drawerToggle?.onDrawerStateChanged(newState)
    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
        drawerToggle?.onDrawerSlide(drawerView, slideOffset)
    }

    override fun onDrawerClosed(drawerView: View) {
        drawerToggle?.onDrawerClosed(drawerView)
    }

    override fun onDrawerOpened(drawerView: View) {
        drawerToggle?.onDrawerOpened(drawerView)
    }

    override fun detach() {
        //nop
    }
}