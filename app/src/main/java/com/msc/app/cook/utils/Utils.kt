package com.msc.app.cook.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.widget.Toast
import com.msc.app.cook.R
import com.tapadoo.alerter.Alerter
import es.dmoral.toasty.Toasty

object Utils {


    fun alertDialog(title: String, message: String, context: Context) {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setMessage(message)
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }


        val alert = dialogBuilder.create()
        alert.setTitle(title)
        alert.show()

    }

    fun toastError(title: String, context: Context) {
        Toasty.error(context, title, Toast.LENGTH_LONG, true).show()
    }

    fun toastSuccess(title: String, context: Context) {
        Toasty.success(context, title, Toast.LENGTH_LONG, true).show()
    }

    fun alerterDialog(title: String, message: String, activity: Activity) {
        Alerter.create(activity)
            .setTitle(title)
            .setText(message)
            .setBackgroundColorRes(R.color.colorPinkDark) // or setBackgroundColorInt(Color.CYAN)
            .show()
    }


}