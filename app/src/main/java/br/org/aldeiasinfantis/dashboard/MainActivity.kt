package br.org.aldeiasinfantis.dashboard

import android.content.Intent
import android.os.Bundle
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
        lateinit var button4: CardView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Main)

/*        try {
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getRealMetrics(displayMetrics)
            val height = displayMetrics.heightPixels
            val width = displayMetrics.widthPixels

            if (width > height) {
                val bottomPart = findViewById<ImageView>(R.id.bottom_circle)
                val bottomPartText = findViewById<TextView>(R.id.bottom_part_text)
                bottomPart.visibility = View.GONE
                bottomPartText.visibility = View.GONE
            }
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }*/

        setContentView(R.layout.activity_main)

        button1 = findViewById(R.id.campo_1)
        button2 = findViewById(R.id.campo_2)
        button3 = findViewById(R.id.campo_3)
        button4 = findViewById(R.id.campo_4)

        buttons[0] = button1
        buttons[1] = button2
        buttons[2] = button3
        buttons[3] = button4

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