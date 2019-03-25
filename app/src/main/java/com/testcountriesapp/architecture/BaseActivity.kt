package com.testcountriesapp.architecture

import android.content.Context
import android.content.DialogInterface
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.tbruyelle.rxpermissions2.RxPermissions
import com.testcountriesapp.R
import com.testcountriesapp.general.Navigator
import com.testcountriesapp.general.behavior.ActionBarBehaviorWrapper
import com.testcountriesapp.general.behavior.DrawerBehavior
import com.testcountriesapp.general.behavior.IBehavior
import com.testcountriesapp.general.behavior.IProgressBehavior
import com.testcountriesapp.general.dialog.AlertDialogFragment
import com.testcountriesapp.general.enums.ActivityTransition
import com.testcountriesapp.general.extension.falseIfNull
import com.testcountriesapp.general.interfaces.IHasBackAction
import com.testcountriesapp.util.LocalizedContextProvider
import timber.log.Timber
import java.lang.ref.WeakReference

abstract class BaseActivity(
    @LayoutRes private val layoutResourceId: Int,
    @IdRes private val baseFragmentLayout: Int
) : AppCompatActivity() {

    private val behaviors = mutableListOf<IBehavior?>()
    private val transitionEnter: ActivityTransition by lazy {
        if (bundledExtra?.containsKey(Navigator.ACTIVITY_TRANSITION_ENTER).falseIfNull()) {
            bundledExtra?.getSerializable(Navigator.ACTIVITY_TRANSITION_ENTER) as ActivityTransition
        } else {
            ActivityTransition.NONE
        }
    }
    private val transitionExit: ActivityTransition by lazy {
        if (bundledExtra?.containsKey(Navigator.ACTIVITY_TRANSITION_EXIT).falseIfNull()) {
            bundledExtra?.getSerializable(Navigator.ACTIVITY_TRANSITION_EXIT) as ActivityTransition
        } else {
            ActivityTransition.NONE
        }
    }
    protected var rootView: View? = null
    private var errorDialog: AlertDialogFragment? = null
    protected var drawerBehavior: DrawerBehavior? = null
    protected open lateinit var loadingBehavior: IProgressBehavior
    protected val rxPermissions: RxPermissions? by lazy { RxPermissions(this) }
    protected val bundledExtra: Bundle? by lazy { intent.getBundleExtra(Navigator.EXTRA_BUNDLE) }
    lateinit var navigator: Navigator
    var actionBarBehavior: ActionBarBehaviorWrapper? = null

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(true)
        super.onCreate(savedInstanceState)
        if (layoutResourceId != 0) {
            setContentView(layoutResourceId)
        }
        rootView = findViewById(android.R.id.content)
        navigator = Navigator(WeakReference(this), baseFragmentLayout)
    }

    override fun attachBaseContext(newBase: Context?) {
        var localizedContext = newBase
        newBase?.let {
            localizedContext = LocalizedContextProvider.getLocalizedContext(it)
        }
        super.attachBaseContext(localizedContext)
    }

    private fun overridePendingTransition(enter: Boolean) {
        if (enter) {
            when (transitionEnter) {
                ActivityTransition.FADE -> overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                ActivityTransition.SLIDE_LEFT -> overridePendingTransition(
                    R.anim.enter_from_left,
                    R.anim.exit_to_right
                )
                ActivityTransition.SLIDE_RIGHT -> overridePendingTransition(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left
                )
                ActivityTransition.GROW_IN -> overridePendingTransition(R.anim.grow_in, R.anim.grow_out)
                ActivityTransition.GROW_OUT -> overridePendingTransition(R.anim.grow_out, R.anim.grow_in)
                ActivityTransition.NONE -> {
                    /* ignore */
                }
            }
        } else {
            when (transitionExit) {
                ActivityTransition.FADE -> overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                ActivityTransition.SLIDE_LEFT -> overridePendingTransition(
                    R.anim.enter_from_left,
                    R.anim.exit_to_right
                )
                ActivityTransition.SLIDE_RIGHT -> overridePendingTransition(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left
                )
                ActivityTransition.GROW_IN -> overridePendingTransition(R.anim.grow_out, R.anim.grow_in)
                ActivityTransition.GROW_OUT -> overridePendingTransition(R.anim.grow_in, R.anim.grow_out)
                ActivityTransition.NONE -> {
                    /* ignore */
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarBehavior?.backAction == true) {
            when (item.itemId) {
                android.R.id.home -> {
                    onBackPressed()
                    return true
                }
            }
        }
        return drawerBehavior?.drawerToggle?.onOptionsItemSelected(item) ?: super.onOptionsItemSelected(item)
    }

    protected fun setupToolbarAndDrawer(
        toolbar: Toolbar?,
        drawerLayout: DrawerLayout? = null,
        navigationView: NavigationView? = null
    ) {
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            if (drawerLayout != null && navigationView != null) {
                drawerBehavior = attachBehavior(
                    DrawerBehavior(
                        this,
                        drawerLayout,
                        toolbar,
                        navigationView,
                        ::invalidateOptionsMenu
                    )
                )
            }
            actionBarBehavior = attachBehavior(
                ActionBarBehaviorWrapper(
                    toolbar, supportActionBar, this, drawerBehavior?.drawerToggle,
                    { drawerBehavior?.holdDrawer(it) },

                    {
                        Timber.e("Back " + it)
                        if (it) onBackPressed() else drawerBehavior?.openDrawer() })
            )
        }
    }

    fun setInProgress(inProgress: Boolean) {
        if (::loadingBehavior.isInitialized) {
            loadingBehavior.setInProgress(inProgress)
        }
    }

    fun showErrorDialog(errorData: Triple<String? /* message */, (() -> DialogInterface.OnClickListener?)? /* positive listener */, (() -> DialogInterface.OnClickListener?)? /* negative listener */>) {
        errorDialog?.dismissAllowingStateLoss()
        errorDialog = AlertDialogFragment.newInstance(
            title = getString(R.string.error_occurred),
            message = errorData.first ?: getString(R.string.error_occurred),
            positiveButtonText = if (errorData.second == null) getString(R.string.close) else getString(R.string.retry_button_text),
            negativeButtonText = if (errorData.second == null) null else getString(R.string.close), // if positive listener == null positive button will be Close, second button will be hidden
            positiveButtonListener = errorData.second?.invoke(),
            negativeButtonListener = errorData.third?.invoke()
        ).also {
            it.show(supportFragmentManager)
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        drawerBehavior?.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        drawerBehavior?.onConfigurationChanged(newConfig)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(false)
    }

    protected fun <T : IBehavior> attachBehavior(behavior: T) = behavior.also {
        behaviors.add(it)
    }

    @CallSuper
    override fun onDestroy() {
        behaviors.forEach { it?.detach() }
        behaviors.clear()
        super.onDestroy()
    }

    protected fun isFragmentInStackHasBackAction(): Boolean {
        val fragment = supportFragmentManager.findFragmentById(baseFragmentLayout)
        return fragment != null && fragment is IHasBackAction
    }

    protected fun isBackActionInFragmentFinished(): Boolean {
        val fragment = supportFragmentManager.findFragmentById(baseFragmentLayout)
        return (fragment as IHasBackAction).onBackPressed()
    }
}
