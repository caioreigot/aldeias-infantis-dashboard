package br.org.aldeiasinfantis.dashboard.ui

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import br.org.aldeiasinfantis.dashboard.R
import br.org.aldeiasinfantis.dashboard.data.model.MessageType

class MessageDialog(
    private val messageType: MessageType,
    private val content: String,
    private val positiveOnClickListener: (() -> Unit)? = null,
    private val negativeOnClickListener: (() -> Unit)? = null,
    private val dismissCallback: ((choice: Boolean?) -> Unit)? = null
) : DialogFragment(R.layout.dialog_message) {

    private var mUserChose: Boolean = false

    lateinit var image: ImageView

    lateinit var titleTV: TextView
    lateinit var contentTV: TextView

    lateinit var negativeBtn: Button
    lateinit var positiveBtn: Button

    lateinit var dividerView: View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        image = view.findViewById(R.id.image_view)
        titleTV = view.findViewById(R.id.title_tv)
        contentTV = view.findViewById(R.id.content_tv)

        negativeBtn = view.findViewById(R.id.negative_btn)
        positiveBtn = view.findViewById(R.id.positive_btn)

        dividerView = view.findViewById(R.id.buttons_divider)

        when (messageType) {
            MessageType.SUCCESSFUL -> {
                titleTV.text = getString(R.string.message_success_header)
                image.setImageResource(R.drawable.ic_baseline_check_circle_24)

                positiveBtn.setBackgroundResource(R.drawable.button_message_green_bg)
                positiveBtn.text = getString(R.string.message_dialog_close)
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
                    positiveBtn.layoutParams.height,
                    49.8f
                )

                positiveBtn.setBackgroundResource(R.drawable.button_message_blue_bg)
                negativeBtn.setBackgroundResource(R.drawable.button_message_blue_bg)

                negativeBtn.visibility = View.VISIBLE
                dividerView.visibility = View.VISIBLE
            }
        }

        contentTV.text = content

        positiveBtn.setOnClickListener{
            mUserChose = true
            positiveOnClickListener?.invoke()
            dismissCallback?.let { it(true) }
            dismiss()
        }

        negativeBtn.setOnClickListener {
            mUserChose = true
            negativeOnClickListener?.invoke()
            dismissCallback?.let { it(false) }
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        if (!mUserChose)
            dismissCallback?.let{ it(null) }
    }
}