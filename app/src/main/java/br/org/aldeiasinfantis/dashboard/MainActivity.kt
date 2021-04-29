package br.org.aldeiasinfantis.dashboard

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityOptionsCompat

class MainActivity : AppCompatActivity() {

    private var buttons = arrayOfNulls<CardView>(4)

    companion object {
        const val intentIDTag = "BUTTON_ID"
        lateinit var button1: CardView
        lateinit var button2: CardView
        lateinit var button3: CardView
//        lateinit var button4: CardView
    }

    lateinit var loginLink: TextView
    lateinit var becomeDonor: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Main)

        setContentView(R.layout.activity_main)

        loginLink = findViewById(R.id.loginLink)
        becomeDonor = findViewById(R.id.becomeDonor)

        loginLink.movementMethod = LinkMovementMethod.getInstance()
        becomeDonor.movementMethod = LinkMovementMethod.getInstance()

        button1 = findViewById(R.id.campo_1)
        button2 = findViewById(R.id.campo_2)
        button3 = findViewById(R.id.campo_3)
//        button4 = findViewById(R.id.campo_4)

        buttons[0] = button1
        buttons[1] = button2
        buttons[2] = button3
//        buttons[3] = button4

        val itensCount = findViewById<GridLayout>(R.id.main_grid_layout).childCount
        findViewById<TextView>(R.id.main_item_count).text = getString(R.string.information_item_count, itensCount)

        for (i in buttons.indices) {
            buttons[i]?.let { it ->
                it.setOnClickListener {
                    val id = it.id
                    val intent = Intent(this, InformationsActivity::class.java)
                    intent.putExtra(intentIDTag, id)

                    val bundle = ActivityOptionsCompat.makeCustomAnimation(
                            this@MainActivity,
                            R.anim.slide_in_up, R.anim.slide_out_down
                    ).toBundle()

                    startActivity(intent, bundle);
                }
            }
        }
    }

}