package br.org.aldeiasinfantis.dashboard

import android.app.Activity
import android.app.Dialog
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import kotlin.system.exitProcess

class Utils {

    companion object {
        fun createConnectionErrorDialog(dialog: Dialog, activity: Activity, positiveBtnCallback: View.OnClickListener?, negativeBtnCallback: View.OnClickListener?): Dialog {
            dialog.setContentView(R.layout.connection_error_dialog)
            dialog.findViewById<TextView>(R.id.textView2).text = activity.getString(R.string.connection_error_message)

            dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
            dialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog.window?.setGravity(Gravity.CENTER)

            val btnPositive: Button = dialog.findViewById(R.id.btn_positive)
            val btnNegative: Button = dialog.findViewById(R.id.btn_negative)

            btnPositive.setOnClickListener(positiveBtnCallback)
            btnNegative.setOnClickListener(negativeBtnCallback)

            return dialog
        }

        fun createUnexpectedErrorDialog(dialog: Dialog, activity: Activity, positiveBtnCallback: View.OnClickListener?): Dialog {
            dialog.setContentView(R.layout.unexpected_error_dialog)
            dialog.findViewById<TextView>(R.id.textView2).text = activity.getString(R.string.unexpected_error_message)

            dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
            dialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog.window?.setGravity(Gravity.CENTER)

            val btnPositive: Button = dialog.findViewById(R.id.btn_positive)

            btnPositive.setOnClickListener(positiveBtnCallback)

            return dialog
        }
    }

}