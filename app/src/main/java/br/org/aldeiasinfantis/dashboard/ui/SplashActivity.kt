package br.org.aldeiasinfantis.dashboard.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.ChangeBounds
import android.transition.Transition
import android.util.Log
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.drawerlayout.widget.DrawerLayout
import br.org.aldeiasinfantis.dashboard.R

class SplashActivity : AppCompatActivity() {

    private lateinit var logoIV: ImageView

    private val timeToChangeInMillis: Long = 1200L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        logoIV = findViewById(R.id.splash_screen_logo_iv)

        Handler(Looper.getMainLooper()).postDelayed({
            changeToLoginActivity()
        }, timeToChangeInMillis)
    }

    private fun changeToLoginActivity() {
        val i = Intent(this, LoginActivity::class.java)

        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this, logoIV, logoIV.transitionName
        ).toBundle()

        options?.let {
            startActivity(i, it)
            supportFinishAfterTransition()
        }
    }
}