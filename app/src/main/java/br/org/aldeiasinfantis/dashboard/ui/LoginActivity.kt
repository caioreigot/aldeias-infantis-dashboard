package br.org.aldeiasinfantis.dashboard.ui

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityOptionsCompat
import br.org.aldeiasinfantis.dashboard.R
import br.org.aldeiasinfantis.dashboard.ui.signup.SignUpActivity


class LoginActivity : BaseActivity() {

    private lateinit var viewGroup: ViewGroup

    private lateinit var createAccountBtnCV: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        viewGroup = findViewById(R.id.login_view_group)

        viewGroup.animate().apply {
            interpolator = LinearInterpolator()
            duration = 300
            alpha(1f)
            startDelay = 600
            start()
        }

        createAccountBtnCV = findViewById(R.id.create_account_btn)

        createAccountBtnCV.setOnClickListener {
            val i = Intent(this, SignUpActivity::class.java)

            val options = ActivityOptionsCompat.makeCustomAnimation(
                this,
                R.anim.slide_in_left, R.anim.slide_out_left
            ).toBundle()

            startActivity(i, options)
        }

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

    override fun onBackPressed() {
        // To support reverse transitions when user clicks the device back button
        supportFinishAfterTransition()
    }
}