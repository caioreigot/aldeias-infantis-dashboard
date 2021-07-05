package br.org.aldeiasinfantis.dashboard.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.StateListDrawable
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import br.org.aldeiasinfantis.dashboard.R
import br.org.aldeiasinfantis.dashboard.data.model.MessageType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {

    protected fun createMessageDialog(
        context: Context,
        messageType: MessageType,
        content: String,
        positiveOnClickListener: (() -> Unit)? = null,
        negativeOnClickListener: (() -> Unit)? = null,
        callback: ((choice: Boolean) -> Unit)? = null
    ): Dialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_message)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        /*val width = (resources.displayMetrics.widthPixels * 0.70).toInt()
        dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)*/

        val image = dialog.findViewById<ImageView>(R.id.image_view)

        val titleTV = dialog.findViewById<TextView>(R.id.title_tv)
        val contentTV = dialog.findViewById<TextView>(R.id.content_tv)

        val negativeBtn = dialog.findViewById<Button>(R.id.negative_btn)
        val positiveBtn = dialog.findViewById<Button>(R.id.positive_btn)

        val dividerView = dialog.findViewById<View>(R.id.buttons_divider)

        when (messageType) {
            MessageType.SUCCESSFUL -> {
                titleTV.text = getString(R.string.message_success_header)
                image.setImageResource(R.drawable.ic_baseline_check_circle_24)

                positiveBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.successIcon))
            }

            MessageType.ERROR -> {
                titleTV.text = getString(R.string.message_error_ops_header)
                image.setImageResource(R.drawable.ic_baseline_error_24)

                positiveBtn.text = getString(R.string.message_dialog_close)
            }

            MessageType.CONFIRMATION -> {
                titleTV.text = getString(R.string.message_confirmation_header)
                image.setImageResource(R.drawable.ic_baseline_info_24)

                negativeBtn.text = getString(R.string.dialog_cancel_button)
                positiveBtn.text = getString(R.string.dialog_yes_button)

                /*positiveBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
                negativeBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)*/

                positiveBtn.layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    0.5f
                )

                positiveBtn.setBackgroundResource(R.drawable.button_message_blue_bg)
                negativeBtn.setBackgroundResource(R.drawable.button_message_blue_bg)

                negativeBtn.visibility = View.VISIBLE
                dividerView.visibility = View.VISIBLE
            }
        }

        contentTV.text = content

        positiveBtn.setOnClickListener{
            positiveOnClickListener?.invoke()
            callback?.let { it(true) }
            dialog.dismiss()
        }

        negativeBtn.setOnClickListener {
            negativeOnClickListener?.invoke()
            callback?.let { it(false) }
            dialog.dismiss()
        }

        return dialog
    }
}