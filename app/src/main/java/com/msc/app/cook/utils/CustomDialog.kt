package com.msc.app.cook.utils

import android.content.DialogInterface
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.msc.app.cook.R
import org.jetbrains.anko.*

private const val ACTIVITY_PADDING = 16

class CustomDialog(ui: AnkoContext<View>) {

    var dialog: DialogInterface
    lateinit var secInputText: EditText
    lateinit var minInputText: EditText
    lateinit var cancelButton: TextView
    lateinit var okButton: TextView

    init {
        with(ui) {
            dialog = alert {

                customView {
                    verticalLayout {
                        padding = dip(ACTIVITY_PADDING)

                        textView("Enter preparation Time") {
                            textSize = 24f
                            textColor = ContextCompat.getColor(context, R.color.colorPinkDark)
                        }.lparams {
                            bottomMargin = dip(ACTIVITY_PADDING)
                        }

                        linearLayout {
                            topPadding = dip(24)
                            orientation = LinearLayout.HORIZONTAL
                            horizontalGravity = Gravity.CENTER
                            minInputText = editText {
                                hint = "Min"
                                inputType = InputType.TYPE_CLASS_NUMBER
                                textSize = 16f
                            }.lparams(width = matchParent) {
                                weight = 0.5F
                            }
                            secInputText = editText {
                                hint = "Sec"
                                inputType = InputType.TYPE_CLASS_NUMBER

                                textSize = 16f
                            }.lparams(width = matchParent) {
                                weight = 0.5F
                            }
                        }

                        linearLayout {
                            topPadding = dip(24)
                            orientation = LinearLayout.HORIZONTAL
                            horizontalGravity = Gravity.END

                            cancelButton = textView("Cancel") {
                                textSize = 14f
                                textColor = ContextCompat.getColor(context, R.color.colorPinkDark)
                            }.lparams {
                                marginEnd = dip(ACTIVITY_PADDING)
                            }

                            okButton = textView("Set Time") {
                                textSize = 14f
                                textColor = ContextCompat.getColor(context, R.color.colorPinkDark)
                            }
                        }
                    }
                }
            }.show()
        }
    }
}