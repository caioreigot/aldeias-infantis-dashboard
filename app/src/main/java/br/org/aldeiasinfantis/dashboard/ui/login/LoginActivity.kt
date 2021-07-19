package br.org.aldeiasinfantis.dashboard.ui.login

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ViewFlipper
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import br.org.aldeiasinfantis.dashboard.R
import br.org.aldeiasinfantis.dashboard.data.model.MessageType
import br.org.aldeiasinfantis.dashboard.data.model.UserSingleton
import br.org.aldeiasinfantis.dashboard.ui.BaseActivity
import br.org.aldeiasinfantis.dashboard.ui.MainActivity
import br.org.aldeiasinfantis.dashboard.ui.signup.SignUpActivity
import br.org.aldeiasinfantis.dashboard.ui.splash.SplashActivity

class LoginActivity : BaseActivity() {

    companion object {
        const val FADE_ANIMATION_ENABLED_EXTRA_TAG = "fade_anim_extra"
    }

    private val loginViewModel: LoginViewModel by viewModels()

    private lateinit var viewGroup: ViewGroup

    private lateinit var emailET: EditText
    private lateinit var passwordET: EditText
    private lateinit var rememberMeCB: CheckBox
    private lateinit var loginViewFlipper: ViewFlipper
    private lateinit var loginBtnCV: CardView
    private lateinit var forgotPasswordBtnLL: LinearLayout

    private lateinit var createAccountBtnCV: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //region Assignments
        viewGroup = findViewById(R.id.login_view_group)
        emailET = findViewById(R.id.email_edit_text)
        passwordET = findViewById(R.id.password_edit_text)
        rememberMeCB = findViewById(R.id.remember_me_checkbox)
        loginViewFlipper = findViewById(R.id.login_btn_view_flipper)
        loginBtnCV = findViewById(R.id.login_btn_cv)
        forgotPasswordBtnLL = findViewById(R.id.forgot_password_btn_ll)
        createAccountBtnCV = findViewById(R.id.create_account_btn)
        //endregion

        val emailReceived = intent.getStringExtra(SignUpActivity.EMAIL_EXTRA_TAG)
        val animationEnabled = intent.getBooleanExtra(FADE_ANIMATION_ENABLED_EXTRA_TAG, true)
        val errorReceived = intent.getStringExtra(SplashActivity.ERROR_EXTRA_TAG)

        if (animationEnabled)
            viewGroup.animate().apply {
                interpolator = LinearInterpolator()
                duration = 300
                alpha(1f)
                startDelay = 600
                start()
            }
        else
            viewGroup.alpha = 1f

        emailReceived?.let { email ->
            emailET.setText(email)
        }

        errorReceived?.let { message ->
            val messageDialog = createMessageDialog(
                MessageType.ERROR,
                message
            )

            messageDialog.show(supportFragmentManager, messageDialog.tag)
        }

        loginBtnCV.setOnClickListener {
            val email = emailET.text.toString()
            val password = passwordET.text.toString()

            loginViewModel.loginUser(email, password)
        }

        forgotPasswordBtnLL.setOnClickListener {
            val forgotPasswordDialog = ForgotPasswordDialog()
            forgotPasswordDialog.show(supportFragmentManager, forgotPasswordDialog.tag)
        }

        createAccountBtnCV.setOnClickListener {
            changeToSignUpActivity()
        }

        loginViewModel.loginViewFlipper.observe(this, {
            it?.let { childToDisplay ->
                loginViewFlipper.displayedChild = childToDisplay
            }
        })

        loginViewModel.errorMessage.observe(this, {
            it?.let { message ->
                val messageDialog = createMessageDialog(
                    MessageType.ERROR,
                    message
                )

                messageDialog.show(supportFragmentManager, messageDialog.tag)
            }
        })

        loginViewModel.loggedUserInformation.observe(this, {
            it?.let { (loggedUser, password) ->
                UserSingleton.set(loggedUser)

                if (rememberMeCB.isChecked)
                    loginViewModel.rememberAccount(loggedUser.email, password)

                changeToMainActivity()
            }
        })

        loginViewModel.resetPasswordMessage.observe(this, { (messageType, message) ->
            val messageDialog = createMessageDialog(
                messageType,
                message
            )

            messageDialog.show(supportFragmentManager, messageDialog.tag)
        })

        /*val sharedElementEnterTransition: Transition = window.sharedElementEnterTransition
        sharedElementEnterTransition.addListener(object : Transition.TransitionListener {
            override fun onTransitionStart(transition: Transition?) {}

            override fun onTransitionEnd(transition: Transition?) {

            }

            override fun onTransitionCancel(transition: Transition?) {}
            override fun onTransitionPause(transition: Transition?) {}
            override fun onTransitionResume(transition: Transition?) {}
        })*/
    }

    private fun changeToMainActivity() {
        startActivityWithAnimation(MainActivity::class.java)
    }

    private fun changeToSignUpActivity() {
        startActivityWithAnimation(SignUpActivity::class.java)
    }

    private fun <T> startActivityWithAnimation(targetActivity: Class<T>) {
        val options = ActivityOptionsCompat.makeCustomAnimation(
            this,
            R.anim.slide_in_left, R.anim.slide_out_left
        ).toBundle()

        val i = Intent(this, targetActivity)

        ActivityCompat.startActivity(this, i, options)
    }
}