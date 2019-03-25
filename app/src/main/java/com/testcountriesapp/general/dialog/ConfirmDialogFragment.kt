package com.testcountriesapp.general.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.testcountriesapp.R
import com.testcountriesapp.architecture.BaseDialogFragment

class ConfirmDialogFragment : BaseDialogFragment() {

    private var message: String? = null
    private var title: String? = null
    private var okListener: DialogInterface.OnClickListener? = null
    private var noListener: DialogInterface.OnClickListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialogBuilder = AlertDialog.Builder(context)
        if (!TextUtils.isEmpty(title)) {
            alertDialogBuilder.setTitle(title)
        }
        alertDialogBuilder.setMessage(message)
        alertDialogBuilder.setPositiveButton(getString(R.string.yes), okListener)
        alertDialogBuilder.setNegativeButton(getString(R.string.no), noListener)
        return alertDialogBuilder.create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false
    }

    companion object {

        fun newInstance(message: String, okListener: DialogInterface.OnClickListener): ConfirmDialogFragment {
            return newInstance(null, message, okListener, null)
        }

        fun newInstance(
            message: String, okListener: DialogInterface.OnClickListener,
            noListener: DialogInterface.OnClickListener
        ): ConfirmDialogFragment {
            return newInstance(null, message, okListener, noListener)
        }

        fun newInstance(
            title: String?, message: String, okListener: DialogInterface.OnClickListener,
            noListener: DialogInterface.OnClickListener? = null
        ): ConfirmDialogFragment {
            val fragment = ConfirmDialogFragment()
            fragment.title = title
            fragment.message = message
            fragment.okListener = okListener
            fragment.noListener = noListener
            return fragment
        }
    }
}
