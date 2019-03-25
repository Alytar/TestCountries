package com.testcountriesapp.general.behavior

import android.content.res.Configuration
import androidx.drawerlayout.widget.DrawerLayout

interface IDrawerBehavior : IBehavior, DrawerLayout.DrawerListener {

    fun syncState()

    fun openDrawer()

    fun closeDrawer(): Boolean

    fun holdDrawer(hold: Boolean)

    fun onConfigurationChanged(newConfig: Configuration)
}