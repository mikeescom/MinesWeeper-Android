package com.msmikeescom.minesweeper.utilities

import android.content.Context
import android.widget.Toast

object ToastUtil {
    private fun createCustomToast(
        context: Context,
        toastMessage: String,
        toastLength: Int
    ): Toast? {

        val toast = Toast.makeText(
            context, toastMessage,
            toastLength
        )

        return toast
    }

    fun showResetLinkSentToast(context: Context) {
        createCustomToast(
            context,
            "Reset password email was sent. Please check your email and try again.",
            Toast.LENGTH_LONG
        )?.show()
    }
}