package br.org.aldeiasinfantis.dashboard.ui

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityOptionsCompat
import br.org.aldeiasinfantis.dashboard.R
import br.org.aldeiasinfantis.dashboard.ui.information.InformationActivity

class MainActivity : AppCompatActivity() {

    private var buttons = arrayOfNulls<CardView>(4)

    companion object {
        const val intentIDTag = "BUTTON_ID"
        const val choiceTag = "GENERAL_INFO_CHOICE_ID"

        lateinit var button1: CardView
        lateinit var button2: CardView
        lateinit var button3: CardView
        lateinit var button4: CardView
    }

    private lateinit var loginLink: TextView
    private lateinit var becomeDonor: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Main)

        setContentView(R.layout.activity_main)

        loginLink = findViewById(R.id.loginLink)
        becomeDonor = findViewById(R.id.becomeDonor)

        loginLink.movementMethod = LinkMovementMethod.getInstance()
        becomeDonor.movementMethod = LinkMovementMethod.getInstance()

        button1 = findViewById(R.id.acolhimento_casas_lares)
        button2 = findViewById(R.id.fortalecimento_familiar)
        button3 = findViewById(R.id.juventudes)
        button4 = findViewById(R.id.indicadores_gerais)

        buttons[0] = button1
        buttons[1] = button2
        buttons[2] = button3

        val itemsCount = findViewById<GridLayout>(R.id.main_grid_layout).childCount
        findViewById<TextView>(R.id.main_item_count).text = getString(R.string.information_item_count, itemsCount)

        for (i in buttons.indices) {
            buttons[i]?.let { it ->
                it.setOnClickListener { buttonCV ->
                    val id = buttonCV.id
                    val intent = Intent(this, InformationActivity::class.java)
                    intent.putExtra(intentIDTag, id)

                    val bundle = ActivityOptionsCompat.makeCustomAnimation(
                            this@MainActivity,
                        R.anim.slide_in_up, R.anim.slide_out_down
                    ).toBundle()

                    startActivity(intent, bundle)
                }
            }
        }

        // Botão 4 tem dois subtópicos, ele abrirá um Dialog
        button4.setOnClickListener {
            val choiceDialog = Dialog(this)
            choiceDialog.setContentView(R.layout.general_info_dialog)

            choiceDialog.findViewById<Button>(R.id.mes_anterior).setOnClickListener {
                val intent = Intent(this, InformationActivity::class.java)

                // Setando os extras
                val extras = Bundle()
                extras.putInt(intentIDTag, button4.id)
                extras.putInt(choiceTag, 1)

                // Passando os extras para a intent
                intent.putExtras(extras)

                val bundle = ActivityOptionsCompat.makeCustomAnimation(
                    this@MainActivity,
                    R.anim.slide_in_up, R.anim.slide_out_down
                ).toBundle()

                startActivity(intent, bundle)
                choiceDialog.dismiss()
            }

            choiceDialog.findViewById<Button>(R.id.ano_anterior).setOnClickListener {
                val intent = Intent(this, InformationActivity::class.java)

                // Setando os extras
                val extras = Bundle()
                extras.putInt(intentIDTag, button4.id)
                extras.putInt(choiceTag, 2)

                // Passando os extras para a intent
                intent.putExtras(extras)

                val bundle = ActivityOptionsCompat.makeCustomAnimation(
                    this@MainActivity,
                    R.anim.slide_in_up, R.anim.slide_out_down
                ).toBundle()

                startActivity(intent, bundle)
                choiceDialog.dismiss()
            }

            choiceDialog.show()
        }
    }

}