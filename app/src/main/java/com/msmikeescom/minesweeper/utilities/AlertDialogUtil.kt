package com.msmikeescom.minesweeper.utilities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.ContextThemeWrapper
import com.msmikeescom.minesweeper.R

object AlertDialogUtil {
    private fun createCustomAlertDialog(context: Context,
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

    fun showGameWillBeRestartedDialog(context: Context,
                                      positiveButtonAction: ((DialogInterface, Int) -> Unit),
                                      negativeButtonAction: ((DialogInterface, Int) -> Unit)) {
        createCustomAlertDialog(
            context = context,
            alertTitle = "Game Restart",
            alertMessage = "Do you really want to restart your game?",
            positiveButton = Pair("Yes", positiveButtonAction),
            negativeButton = Pair("Cancel", negativeButtonAction)
        )?.show()
    }

    fun showWinMessageDialog(context: Context,
                                     positiveButtonAction: ((DialogInterface, Int) -> Unit),
                                     negativeButtonAction: ((DialogInterface, Int) -> Unit)) {
        createCustomAlertDialog(
            context = context,
            alertTitle = "You Won!",
            alertMessage = "You have won!. Do you want to start a new game?",
            positiveButton = Pair("Yes", positiveButtonAction),
            negativeButton = Pair("Cancel", negativeButtonAction)
        )?.show()

    }

    fun showLoseMessageDialog(context: Context,
                                      positiveButtonAction: ((DialogInterface, Int) -> Unit),
                                      negativeButtonAction: ((DialogInterface, Int) -> Unit)) {
        createCustomAlertDialog(
            context = context,
            alertTitle = "You Lost!",
            alertMessage = "You have lost!. Do you want to start a new game?",
            positiveButton = Pair("Yes", positiveButtonAction),
            negativeButton = Pair("Cancel", negativeButtonAction)
        )?.show()
    }

    fun showEmailFieldCannotBeEmptyDialog(context: Context,
                              positiveButtonAction: ((DialogInterface, Int) -> Unit)) {
        createCustomAlertDialog(
            context = context,
            alertTitle = "Field is missing",
            alertMessage = "Email cannot be empty",
            positiveButton = Pair("Ok", positiveButtonAction)
        )?.show()
    }

    fun showEmailOrPasswordFieldCannotBeEmptyDialog(context: Context,
                                          positiveButtonAction: ((DialogInterface, Int) -> Unit)) {
        createCustomAlertDialog(
            context = context,
            alertTitle = "Field is missing",
            alertMessage = "Email or Password cannot be both empty",
            positiveButton = Pair("Ok", positiveButtonAction)
        )?.show()
    }

    fun showInvalidCredentialsDialog(context: Context,
                                        positiveButtonAction: ((DialogInterface, Int) -> Unit)) {
        createCustomAlertDialog(
            context = context,
            alertTitle = "Error",
            alertMessage = "Email or Password is incorrect. Please try again.",
            positiveButton = Pair("Ok", positiveButtonAction)
        )?.show()
    }

    fun showErrorSendingResetLinkDialog(context: Context,
                                          positiveButtonAction: ((DialogInterface, Int) -> Unit)) {
        createCustomAlertDialog(
            context = context,
            alertTitle = "Error",
            alertMessage = "Cannot send reset link. Please try again.",
            positiveButton = Pair("Ok", positiveButtonAction)
        )?.show()
    }
}