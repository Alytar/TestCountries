package com.testcountriesapp.general.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import com.testcountriesapp.R
import com.testcountriesapp.architecture.BaseDialogFragment

class AlertDialogFragment : BaseDialogFragment() {

    private var title: String? = null
    private var message: String? = null
    private var positiveButtonText: String? = null
    private var negativeButtonText: String? = null
    private var ableToCancel: Boolean? = null
    private var positiveButtonListener: DialogInterface.OnClickListener? = null
    private var negativeButtonListener: DialogInterface.OnClickListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setCancelable(ableToCancel != null ?: false)
        if (!TextUtils.isEmpty(title)) {
            alertDialogBuilder.setTitle(title)
        }
        alertDialogBuilder.setMessage(message)
        alertDialogBuilder.setPositiveButton(
            if (TextUtils.isEmpty(positiveButtonText)) getString(R.string.ok) else positiveButtonText,
            positiveButtonListener
        )
        if (negativeButtonListener != null) {
            alertDialogBuilder.setNegativeButton(
                if (TextUtils.isEmpty(negativeButtonText)) getString(R.string.close) else negativeButtonText,
                negativeButtonListener
            )
        }
        val dialog = alertDialogBuilder.create()
        dialog.setCanceledOnTouchOutside(ableToCancel != null ?: false)
        dialog.setCancelable(ableToCancel != null ?: false)
        isCancelable = ableToCancel != null ?: false

        return dialog
    }

    companion object {

        fun newInstance(
            title: String? = null,
            message: String? = null,
            positiveButtonText: String? = null,
            negativeButtonText: String? = null,
            ableToCancel: Boolean? = null,
            positiveButtonListener: DialogInterface.OnClickListener? = null,
            negativeButtonListener: DialogInterface.OnClickListener? = null
        ): AlertDialogFragment {
            val fragment = AlertDialogFragment()
            fragment.title = title
            fragment.message = message
            fragment.positiveButtonText = positiveButtonText
            fragment.negativeButtonText = negativeButtonText
            fragment.ableToCancel = ableToCancel
            fragment.positiveButtonListener = positiveButtonListener
            fragment.negativeButtonListener = negativeButtonListener
            return fragment
        }
    }
}
