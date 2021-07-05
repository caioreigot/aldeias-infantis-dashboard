package br.org.aldeiasinfantis.dashboard.ui.signup

import android.os.Bundle
import android.text.InputType.*
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ViewFlipper
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import br.org.aldeiasinfantis.dashboard.R
import br.org.aldeiasinfantis.dashboard.data.model.MessageType
import br.org.aldeiasinfantis.dashboard.ui.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpActivity : BaseActivity() {

    private val signUpViewModel: SignUpViewModel by viewModels()

    private lateinit var nameET: EditText
    private lateinit var emailET: EditText
    private lateinit var passwordET: EditText
    private lateinit var visibilityBtn: LinearLayout
    private lateinit var signUpBtnCV: CardView
    private lateinit var signUpViewFlipper: ViewFlipper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        //region Assignments
        nameET = findViewById(R.id.name_edit_text)
        emailET = findViewById(R.id.email_edit_text)
        passwordET = findViewById(R.id.password_edit_text)
        visibilityBtn = findViewById(R.id.password_visibility_btn_ll)
        signUpBtnCV = findViewById(R.id.sign_up_btn_cv)
        signUpViewFlipper = findViewById(R.id.sign_up_view_flipper)
        //endregion Assignments

        visibilityBtn.setOnClickListener(PasswordVisibilityButtonListener(passwordET))

        signUpBtnCV.setOnClickListener {
            signUpViewModel.registerUser(
                name = nameET.text.toString().trimEnd(),
                email = emailET.text.toString(),
                password = passwordET.text.toString()
            )
        }

        signUpViewModel.errorMessage.observe(this, {
            it?.let { message ->
                createMessageDialog(
                    this,
                    MessageType.ERROR,
                    message
                ).show()
            }
        })

        signUpViewModel.signUpViewFlipper.observe(this, {
            it?.let { childToDisplay ->
                signUpViewFlipper.displayedChild = childToDisplay
            }
        })

        signUpViewModel.registrationMade.observe(this, {
            createMessageDialog(
                this,
                MessageType.SUCCESSFUL,
                getString(R.string.registration_successful_made),
                null,
                null,
                { finish() }
            ).show()
        })
    }

    class PasswordVisibilityButtonListener(var editText: EditText) : View.OnClickListener {
        override fun onClick(v: View?) {
            if (editText.inputType == TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_PASSWORD) {
                val cursorPosition = editText.selectionStart

                editText.inputType = (TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
                editText.setSelection(cursorPosition)

                ((v as LinearLayout?)?.getChildAt(0) as ImageView?)
                    ?.setImageResource(R.drawable.ic_baseline_visibility_24)
            } else { // Not visible
                val cursorPosition = editText.selectionStart

                editText.inputType = (TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_PASSWORD)
                editText.setSelection(cursorPosition)

                ((v as LinearLayout?)?.getChildAt(0) as ImageView?)
                    ?.setImageResource(R.drawable.ic_baseline_visibility_off_24)
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }
}