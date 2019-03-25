package com.testcountriesapp.general.behavior

interface IActionBarBehavior : IBehavior {

    fun show()

    fun hide()

    fun updateTitle(title: String)

    fun showBurger()

    fun showBackButton()

    fun hideNavigationButton()

    fun isBackAction(): Boolean
}