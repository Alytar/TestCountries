package com.testcountriesapp.general.behavior

import com.testcountriesapp.widget.PreloaderOverlay


class PreloaderOverlayBehavior(private val preloader: PreloaderOverlay) : IProgressBehavior {

    override fun showProgress() {
        preloader.visible = true
    }

    override fun hideProgress() {
        preloader.visible = false
    }

    override fun detach() {
        preloader.visible = false
    }
}