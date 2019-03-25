package com.testcountriesapp.ui.main.activity

import android.os.Bundle
import androidx.annotation.CallSuper
import com.testcountriesapp.R
import com.testcountriesapp.architecture.BaseMVVMActivity
import com.testcountriesapp.general.behavior.PreloaderOverlayBehavior
import com.testcountriesapp.general.extension.falseIfNull
import com.testcountriesapp.general.extension.hideKeyboard
import com.testcountriesapp.ui.main.fragment.mainList.MainFragment
import com.testcountriesapp.util.KeyboardManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_common.*
import kotlinx.android.synthetic.main.content_common.*
import kotlinx.android.synthetic.main.toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainActivity :  BaseMVVMActivity(R.layout.activity_main, R.id.common_fragments_container) {

    val viewModel: MainViewModel by viewModel()

    enum class Screen {
        MAIN
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadingBehavior = attachBehavior(PreloaderOverlayBehavior(preloader))
        Timber.e("MainActivity onCreate")
        setupToolbarAndDrawer(toolbar, drawerLayout)
        viewModel.openScreenEvent.postValue(Screen.MAIN)

        viewModel.bindDisposable(
            KeyboardManager(this)
                .status()
                .subscribe {
                    viewModel.isKeyboardOpened.postValue(it == KeyboardManager.KeyboardStatus.OPEN)
                })
    }

    override fun attachLifecycleObserver() {
        lifecycle.addObserver(viewModel)
    }

    override fun onBindLiveData() {
        super.onBindLiveData()
        observe(viewModel.hasInternetConnectionEvent, ::showNoInternetView)
        observe(viewModel.openScreenEvent, ::onOpenScreen)
        observe(viewModel.isKeyboardOpened, ::onKeyboardStateChanged)

    }

    private fun onKeyboardStateChanged(isOpened: Boolean) {
        // клавиатура поменяла состояние
    }

    private fun onOpenScreen(screen: Screen) {
        drawerBehavior?.closeDrawer()

        when (screen) {
            Screen.MAIN -> {
                navigator.replaceFragment(MainFragment.getInstance())
            }
        }
    }

    private fun showNoInternetView(hasNetwork: Boolean) {
        no_network_view.visible = !hasNetwork
    }

    @CallSuper
    override fun onBackPressed() {
        if (!drawerBehavior?.closeDrawer().falseIfNull()) {
            if (isFragmentInStackHasBackAction()) {
                if (isBackActionInFragmentFinished()) {
                    viewModel.hideLoading()
                    hideKeyboard()
                    when {
                        supportFragmentManager.backStackEntryCount > 0 -> supportFragmentManager.popBackStack()
                        isRootFragment() -> {
                            navigator.clearBackStack(false)
                            viewModel.openScreenEvent.postValue(Screen.MAIN)
                        }
                        else -> super.onBackPressed()
                    }
                }
            } else {
                viewModel.hideLoading()
                hideKeyboard()
                when {
                    supportFragmentManager.backStackEntryCount > 0 -> supportFragmentManager.popBackStack()
                    isRootFragment() -> {
                        navigator.clearBackStack(false)
                        viewModel.openScreenEvent.postValue(Screen.MAIN)
                    }
                    else -> super.onBackPressed()
                }
            }
        }
    }

    private fun isRootFragment(): Boolean {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.common_fragments_container)
        return currentFragment != null && !(currentFragment::class.java.isAssignableFrom(MainFragment::class.java))
    }
}
