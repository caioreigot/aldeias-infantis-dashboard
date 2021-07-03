package br.org.aldeiasinfantis.dashboard.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import br.org.aldeiasinfantis.dashboard.R

class SplashActivity : AppCompatActivity() {

    private val timeToChangeInMillis: Long = 1200L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //hideSystemUI()
        changeActivity()
    }

    fun changeActivity() {
        val intent = Intent(this, MainActivity::class.java)

        Handler(Looper.getMainLooper()).postDelayed({
            intent.change(intent)
        }, timeToChangeInMillis)
    }

    fun Intent.change(passedIntent: Intent) {
        val bundle = ActivityOptionsCompat.makeCustomAnimation(
                this@SplashActivity,
                android.R.anim.fade_in, android.R.anim.fade_out
        ).toBundle()

        startActivity(passedIntent, bundle)
        finish()
    }

    /*
    fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val controller: WindowInsetsController? = window.insetsController

            controller?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            if (supportActionBar != null)
                supportActionBar!!.hide()

            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
        }
    }
    */

}