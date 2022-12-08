package com.wedoapps.barcodescanner.Utils

import android.graphics.Color
import android.view.View
import com.google.android.material.snackbar.Snackbar


fun View.showSnackbar(
    view: View,
    msg: String,
    length: Int,
    actionMessage: CharSequence?,
    action: (View) -> Unit
) {

    val snackbar = Snackbar.make(view, msg, length)
    if (actionMessage != null) {
        snackbar.setTextColor(Color.WHITE)
        snackbar.setAction(actionMessage) {
            action(this)
        }.show()
    } else {
        snackbar.show()
    }
}