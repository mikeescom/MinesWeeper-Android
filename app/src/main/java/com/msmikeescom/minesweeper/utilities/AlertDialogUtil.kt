package com.msmikeescom.minesweeper.utilities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.ContextThemeWrapper
import com.msmikeescom.minesweeper.R

object AlertDialogUtil {
    fun createCustomAlertDialog(context: Context,
                                alertTitle: String,
                                alertMessage: String,
                                positiveButton: Pair<String, ((DialogInterface, Int) -> Unit)>,
                                negativeButton: Pair<String, ((DialogInterface, Int) -> Unit)>? = null,
                                neutralButton: Pair<String, ((DialogInterface, Int) -> Unit)>? = null,
                                cancelOnTouchOutside: Boolean = false,
                                dismissListener: ((DialogInterface) -> Unit)? = null,
    ): AlertDialog? {

        if (isContextNotAvailable(context)) {
            return null
        }

        val alertDialogBuilder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AlertDialogTheme))
        alertDialogBuilder.setTitle(alertTitle)
        alertDialogBuilder.setMessage(alertMessage)
        alertDialogBuilder.setPositiveButton(positiveButton.first, positiveButton.second)
        if (negativeButton != null) {
            alertDialogBuilder.setNegativeButton(negativeButton.first, negativeButton.second)
        }
        if (neutralButton != null) {
            alertDialogBuilder.setNeutralButton(neutralButton.first, neutralButton.second)
        }

        val dialog = alertDialogBuilder.create()
        dialog.setOnDismissListener(dismissListener)
        dialog.setCanceledOnTouchOutside(cancelOnTouchOutside)
        return dialog
    }

    private fun isContextNotAvailable(context: Context): Boolean {
        if (context is Activity) {
            return context.isFinishing || context.isDestroyed
        }

        return true
    }
}