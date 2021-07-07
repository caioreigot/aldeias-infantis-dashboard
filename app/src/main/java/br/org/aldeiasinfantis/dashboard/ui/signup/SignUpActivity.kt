package br.org.aldeiasinfantis.dashboard.ui.signup

import android.content.Intent
import android.os.Bundle
import android.text.InputType.*
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityOptionsCompat
import br.org.aldeiasinfantis.dashboard.R
import br.org.aldeiasinfantis.dashboard.data.model.MessageType
import br.org.aldeiasinfantis.dashboard.ui.BaseActivity
import br.org.aldeiasinfantis.dashboard.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpActivity : BaseActivity() {

    companion object {
        const val EMAIL_EXTRA_TAG = "email_extra"
    }

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

        /*
         "passwordET" is the last EditText on the registration screen, so it has an option
         defined in the xml (android:imeOptions="actionSend") that makes a "send" button on
         the keyboard, and this is a listener for when the user clicks on it
         */
        passwordET.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    passwordET.clearFocus()

                    // Perform a click action on the sign up button
                    signUpBtnCV.callOnClick()
                    return true
                }

                return false
            }
        })

        //region Observers
        with (signUpViewModel) {
            val thisActivity = this@SignUpActivity

            errorMessage.observe(thisActivity, {
                it?.let { message ->
                    val messageDialog = createMessageDialog(
                        MessageType.ERROR,
                        message
                    )

                    messageDialog.show(supportFragmentManager, messageDialog.tag)
                }
            })

            signUpViewFlipper.observe(thisActivity, {
                it?.let { childToDisplay ->
                    thisActivity.signUpViewFlipper.displayedChild = childToDisplay
                }
            })

            registrationMade.observe(thisActivity, {
                it?.let { user ->
                    val messageDialog = createMessageDialog(
                        MessageType.SUCCESSFUL,
                        getString(R.string.registration_successful_made),
                        null,
                        null,
                        {
                            val i = Intent(thisActivity, LoginActivity::class.java)

                            val options = ActivityOptionsCompat.makeCustomAnimation(
                                thisActivity,
                                R.anim.slide_in_right, R.anim.slide_out_right
                            ).toBundle()

                            i.putExtra(EMAIL_EXTRA_TAG, user.email)
                            i.putExtra(LoginActivity.FADE_ANIMATION_ENABLED_EXTRA_TAG, false)

                            startActivity(i, options)

                            supportFinishAfterTransition()
                        }
                    )

                    messageDialog.show(supportFragmentManager, messageDialog.tag)
                }
            })
        }
        //endregion
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