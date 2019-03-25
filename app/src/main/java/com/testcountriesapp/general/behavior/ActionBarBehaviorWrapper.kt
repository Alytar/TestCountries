package com.testcountriesapp.general.behavior

import android.os.Build
import android.view.WindowManager
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class ActionBarBehaviorWrapper(
    private val toolbar: Toolbar,
    private val supportActionBar: ActionBar?,
    private val activity: AppCompatActivity,
    private val drawerToggle: ActionBarDrawerToggle?,
    private val holdDrawer: (Boolean) -> Unit,
    navigationClick: (Boolean) -> Unit
) : ActionBarBehavior(toolbar, supportActionBar, drawerToggle, holdDrawer, navigationClick) {

    init {
        drawerToggle?.isDrawerIndicatorEnabled = false
    }

    fun applyToolbarParams(title: String, button: Button) {
        if (Build.VERSION.SDK_INT >= 21) {
            val window = activity.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            //window.statusBarColor = toolbarBackgroundColor
        }

        when (button) {
            Button.DRAWER -> {
                showBurger()
            }
            Button.BACK -> {
                showBackButton()
            }
            Button.NONE -> {
                hideNavigationButton()
            }
        }

        updateTitle(title)
    }

    override fun showBurger() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
        drawerToggle?.syncState()
        backAction = false
        holdDrawer(false)
    }

    override fun showBackButton() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white)
        backAction = true
        holdDrawer(true)
    }

    override fun hideNavigationButton() {
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        holdDrawer(true)
    }

    enum class Color {
        BLUE, PINK, GREEN
    }

    enum class Button {
        DRAWER, BACK, NONE
    }
}