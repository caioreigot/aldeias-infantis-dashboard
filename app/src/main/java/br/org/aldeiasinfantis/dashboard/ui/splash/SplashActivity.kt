package br.org.aldeiasinfantis.dashboard.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import br.org.aldeiasinfantis.dashboard.R
import br.org.aldeiasinfantis.dashboard.data.helper.ErrorMessageHandler
import br.org.aldeiasinfantis.dashboard.data.helper.ResourceProvider
import br.org.aldeiasinfantis.dashboard.data.model.ErrorType
import br.org.aldeiasinfantis.dashboard.data.model.Global
import br.org.aldeiasinfantis.dashboard.data.model.UserSingleton
import br.org.aldeiasinfantis.dashboard.ui.main.MainActivity
import br.org.aldeiasinfantis.dashboard.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    companion object {
        const val ERROR_EXTRA_TAG = "splash_error_extra"
    }

    private val splashViewModel: SplashViewModel by viewModels()

    private lateinit var logoIV: ImageView
    private lateinit var progressBar: ProgressBar

    private var emailAndPassword: Pair<String, String>? = null

    private var loginAttempts: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        logoIV = findViewById(R.id.splash_screen_logo_iv)
        progressBar = findViewById(R.id.splash_progress_bar)

        emailAndPassword = splashViewModel.fetchRememberedAccount()

        emailAndPassword?.let { (email, password) ->
            splashViewModel.loginUser(email, password)
        }
            ?: run {
                // Nothing found in SharedPreferences
                Handler(Looper.getMainLooper()).postDelayed({
                    changeToLoginActivity()
                }, Global.SPLASH_SCREEN_DURATION_IN_MILLIS)
            }

        splashViewModel.loggedUserInformation.observe(this, {
            it?.let { (loggedUser, _) ->
                UserSingleton.set(loggedUser)
                changeToMainActivity()
            }
        })

        splashViewModel.hasConnectionProblem.observe(this, {
            progressBar.visibility = View.VISIBLE

            Handler(Looper.getMainLooper()).postDelayed({
                emailAndPassword?.let { (itEmail, itPassword) ->
                    if (loginAttempts >= Global.MAX_LOGIN_ATTEMPTS) {
                        changeToLoginActivity(ErrorType.LOGIN_TIME_OUT)
                        return@postDelayed
                    }

                    splashViewModel.loginUser(itEmail, itPassword)
                    loginAttempts++
                }
                    ?: changeToLoginActivity(ErrorType.UNEXPECTED_ERROR)

            }, Global.LOGIN_ATTEMPT_DELAY_IN_MILLIS)
        })
    }

    private fun changeToLoginActivity(error: ErrorType? = null) {
        val i = Intent(this, LoginActivity::class.java)

        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this, logoIV, logoIV.transitionName
        ).toBundle()

        error?.let { e ->
            val message = ErrorMessageHandler.getErrorMessage(ResourceProvider(this), e)
            i.putExtra(ERROR_EXTRA_TAG, message)
        }

        ActivityCompat.startActivity(this, i, options)
        supportFinishAfterTransition()
    }

    private fun changeToMainActivity() {
        val i = Intent(this, MainActivity::class.java)

        val options = ActivityOptionsCompat.makeCustomAnimation(
            this,
            R.anim.slide_in_left, R.anim.slide_out_left
        ).toBundle()

        ActivityCompat.startActivity(this, i, options)
        supportFinishAfterTransition()
    }
}