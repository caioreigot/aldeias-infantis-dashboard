package br.org.aldeiasinfantis.dashboard.ui

import android.os.Bundle
import android.transition.Transition
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import br.org.aldeiasinfantis.dashboard.R


class LoginActivity : AppCompatActivity() {

    lateinit var viewGroup: ViewGroup

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