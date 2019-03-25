package com.testcountriesapp.general

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.transition.Fade
import androidx.transition.Slide
import com.testcountriesapp.R
import com.testcountriesapp.architecture.BaseFragment
import com.testcountriesapp.general.enums.ActivityTransition
import com.testcountriesapp.general.enums.FragmentTransition
import timber.log.Timber
import java.lang.ref.WeakReference

class Navigator(
    private val activityWeekReference: WeakReference<FragmentActivity>,
    @IdRes private val baseFragmentLayout: Int
) {

    private val fragmentActivity
        get() = activityWeekReference.get()

    fun startActivity(action: String) {
        fragmentActivity?.startActivity(Intent(action))
    }

    fun startActivity(
        targetActivityClass: Class<out Activity>,
        clearTop: Boolean = false,
        bundle: Bundle? = null
    ) {
        startActivityInternal(
            targetActivityClass,
            bundle,
            null,
            clearTop
        )
    }

    fun startActivityForResult(
        targetActivityClass: Class<out Activity>,
        requestCode: Int,
        bundle: Bundle? = null
    ) {
        startActivityInternal(
            targetActivityClass,
            bundle,
            requestCode,
            false
        )
    }

    fun startActivityForResultInFragment(
        fragment: BaseFragment,
        targetActivityClass: Class<out Activity>,
        requestCode: Int,
        bundle: Bundle? = null
    ) {
        startActivityInternal(
            targetActivityClass,
            bundle,
            requestCode,
            false,
            fragment
        )
    }

    private fun startActivityInternal(
        targetActivityClass: Class<out Activity>,
        bundle: Bundle?,
        requestCode: Int?,
        clearTop: Boolean,
        fragment: BaseFragment? = null
    ) {
        fragmentActivity?.let {
            val intent = Intent(it, targetActivityClass)
            if (bundle != null) {
                intent.putExtra(EXTRA_BUNDLE, bundle)
            }
            if (clearTop) {
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_NEW_TASK
            }

            if (requestCode != null) {
                if (fragment != null) {
                    fragment.startActivityForResult(intent, requestCode)
                } else {
                    it.startActivityForResult(intent, requestCode)
                }
            } else {
                it.startActivity(intent)
            }
        }
    }

    fun finishActivity(affinity: Boolean = false) {
        fragmentActivity?.let {
            if (affinity) {
                it.finishAffinity()
            } else {
                it.finish()
            }
        }
    }

    fun replaceFragment(
        fragment: BaseFragment,
        containerId: Int = baseFragmentLayout,
        addToBackStack: Boolean = false,
        sharedElement: View? = null,
        transitionName: String? = null,
        transition: FragmentTransition = FragmentTransition.NONE
    ) {
        replaceFragmentInternal(
            containerId,
            fragment,
            addToBackStack,
            sharedElement,
            transitionName,
            transition
        )
    }

    private fun replaceFragmentInternal(
        @IdRes containerId: Int,
        fragment: BaseFragment,
        addToBackStack: Boolean,
        sharedElement: View? = null,
        transitionName: String? = null,
        transition: FragmentTransition
    ) {
        fragmentActivity?.let {
            val fragmentManager = it.supportFragmentManager
            val oldFragment = fragmentManager.findFragmentById(containerId)
            val transaction = fragmentManager.beginTransaction()

            if (sharedElement != null && transitionName != null) {
                transaction.addSharedElement(sharedElement, transitionName)
            }
            when (transition) {
                FragmentTransition.OPEN -> transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                FragmentTransition.CLOSE -> transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                FragmentTransition.FADE -> transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                FragmentTransition.SLIDE_LEFT -> transaction.setCustomAnimations(
                    R.anim.enter_from_left,
                    R.anim.exit_to_right,
                    R.anim.enter_from_right,
                    R.anim.exit_to_left
                )
                FragmentTransition.SLIDE_RIGHT -> transaction.setCustomAnimations(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left,
                    R.anim.enter_from_left,
                    R.anim.exit_to_right
                )
                FragmentTransition.GROW_IN -> transaction.setCustomAnimations(R.anim.grow_in, R.anim.grow_out)
                FragmentTransition.GROW_OUT -> transaction.setCustomAnimations(R.anim.grow_out, R.anim.grow_in)
                FragmentTransition.SHARED_FADE -> {
                    fragment.sharedElementEnterTransition = Fade()
                    fragment.sharedElementReturnTransition = Fade()
                }
                FragmentTransition.SHARED_SLIDE -> {
                    fragment.sharedElementEnterTransition = Slide()
                    fragment.sharedElementReturnTransition = Slide()
                }
                FragmentTransition.LONG_FADE -> transaction.setCustomAnimations(
                    R.anim.long_fade_in,
                    R.anim.long_fade_out
                )
                FragmentTransition.NONE -> {
                    /* ignore */
                }
            }

            if (oldFragment != null) {
                transaction.detach(oldFragment)
            }
            Timber.e("replaceFragmentInternal")

            transaction.replace(containerId, fragment, fragment.tag)
            if (addToBackStack) {
                transaction.addToBackStack(fragment.tag).commit()
                fragmentManager.executePendingTransactions()
            } else {
                transaction.commitNow()
            }
        }
    }

    fun clearBackStack(exceptFirst: Boolean = true, immediate: Boolean = false) {
        fragmentActivity?.also {
            if (exceptFirst) {
                if (it.supportFragmentManager.backStackEntryCount > 1) {
                    val entry = it.supportFragmentManager.getBackStackEntryAt(0)
                    if (immediate) {
                        it.supportFragmentManager.popBackStackImmediate(
                            entry.id,
                            FragmentManager.POP_BACK_STACK_INCLUSIVE
                        )
                    } else {
                        it.supportFragmentManager.popBackStack(entry.id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    }
                    it.supportFragmentManager.executePendingTransactions()
                }
            } else {
                if (immediate) {
                    it.supportFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                } else {
                    it.supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                }
            }
        }
    }

    fun closeFragment() {
        fragmentActivity?.supportFragmentManager?.popBackStack()
    }

    companion object {

        const val EXTRA_BUNDLE = "bundle"
        const val ACTIVITY_TRANSITION_ENTER = "activityTransitionEnter"
        const val ACTIVITY_TRANSITION_EXIT = "activityTransitionExit"

        fun putTransitionsToBundle(
            enterTransition: ActivityTransition,
            exitTransition: ActivityTransition
        ): Bundle {
            return putTransitionsToBundle(
                Bundle(),
                enterTransition,
                exitTransition
            )
        }

        fun putTransitionsToBundle(
            bundle: Bundle?,
            enterTransition: ActivityTransition?,
            exitTransition: ActivityTransition?
        ): Bundle {
            val resultBundle = bundle ?: Bundle()
            if (enterTransition != null) {
                resultBundle.putSerializable(ACTIVITY_TRANSITION_ENTER, enterTransition)
            }
            if (exitTransition != null) {
                resultBundle.putSerializable(ACTIVITY_TRANSITION_EXIT, exitTransition)
            }

            return resultBundle
        }
    }
}
