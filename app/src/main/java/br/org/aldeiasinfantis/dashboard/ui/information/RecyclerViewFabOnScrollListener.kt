package br.org.aldeiasinfantis.dashboard.ui.information

import android.content.Context
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import br.org.aldeiasinfantis.dashboard.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class RecyclerViewFabOnScrollListener(
    context: Context,
    private val fab: FloatingActionButton
) : RecyclerView.OnScrollListener() {

    private var itsInside = true
    private var animating = false

    private val fabExit: Animation = AnimationUtils.loadAnimation(
        context, R.anim.fab_exit)

    private val fabEnter: Animation = AnimationUtils.loadAnimation(
        context, R.anim.fab_enter)

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        // Scrolling up
        if (dy < 0 && !itsInside && !animating) {
            fabEnter.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationEnd(animation: Animation?) {
                    itsInside = true
                    animating = false

                    fab.isEnabled = true
                    fab.isClickable = true
                }

                override fun onAnimationStart(animation: Animation?) {
                    fab.isEnabled = true
                    animating = true
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })

            fab.startAnimation(fabEnter)
        }

        if (!recyclerView.canScrollVertically(1) &&
            recyclerView.canScrollVertically(-1) &&
            itsInside &&
            !animating
        ) {
            fabExit.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationEnd(animation: Animation?) {
                    fab.isEnabled = false
                    itsInside = false
                    animating = false
                }

                override fun onAnimationStart(animation: Animation?) {
                    fab.isClickable = false
                    animating = true
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })

            fab.startAnimation(fabExit)
        }
    }
}