package br.org.aldeiasinfantis.dashboard.ui.login

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.ViewFlipper
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import br.org.aldeiasinfantis.dashboard.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordDialog : DialogFragment(R.layout.dialog_forgot_password) {

    private val loginViewModel: LoginViewModel by activityViewModels()

    lateinit var forgotPasswordViewFlipper: ViewFlipper
    lateinit var forgotPasswordBackBtnIV: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NO_TITLE, R.style.FullScreenDialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //region Assignments
        forgotPasswordViewFlipper = dialog!!.findViewById(R.id.send_view_flipper)
        forgotPasswordBackBtnIV = dialog!!.findViewById(R.id.back_btn_iv)
        //endregion

        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )

        val emailET = view.findViewById<EditText>(R.id.email_et)
        val sendBtn = view.findViewById<CardView>(R.id.send_btn)

        sendBtn.setOnClickListener {
            loginViewModel.sendPasswordResetEmail(emailET.text.toString())
        }

        forgotPasswordBackBtnIV.setOnClickListener {
            dialog?.dismiss()
        }

        loginViewModel.forgotPasswordBtnViewFlipper.observe(this, {
            it?.let { childToDisplay ->
                forgotPasswordViewFlipper.displayedChild = childToDisplay
            }
        })
    }

}