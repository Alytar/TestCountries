package com.testcountriesapp.general.behavior

import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import com.jakewharton.rxbinding2.support.v7.widget.RxToolbar
import io.reactivex.disposables.Disposable
import timber.log.Timber

open class ActionBarBehavior(
    toolbar: Toolbar,
    private val supportActionBar: ActionBar?,
    private val drawerToggle: ActionBarDrawerToggle?,
    private val holdDrawer: (Boolean) -> Unit,
    private val navigationClick: (Boolean) -> Unit
) : IActionBarBehavior {

    private var disposable: Disposable? = null
    var backAction: Boolean = false

    init {
        disposable = RxToolbar.navigationClicks(toolbar)
            .doOnError { Timber.e(it) }
            .subscribe {
                navigationClick(backAction)
            }
    }

    override fun show() {
        supportActionBar?.show()
    }

    override fun hide() {
        supportActionBar?.hide()
    }

    override fun updateTitle(title: String) {
        supportActionBar?.title = title
    }

    override fun showBurger() {
        drawerToggle?.isDrawerIndicatorEnabled = true
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        drawerToggle?.syncState()
        backAction = false
        holdDrawer(false)
    }

    override fun showBackButton() {
        drawerToggle?.isDrawerIndicatorEnabled = false
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        backAction = true
        holdDrawer(true)
    }

    override fun hideNavigationButton() {
        if (backAction) {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
        } else {
            drawerToggle?.isDrawerIndicatorEnabled = false
        }
        holdDrawer(true)
    }

    override fun isBackAction(): Boolean = backAction

    override fun detach() {
        disposable?.dispose()
    }
}