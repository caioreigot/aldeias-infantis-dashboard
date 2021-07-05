package br.org.aldeiasinfantis.dashboard.ui

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityOptionsCompat
import br.org.aldeiasinfantis.dashboard.R
import br.org.aldeiasinfantis.dashboard.data.model.UserSingleton
import br.org.aldeiasinfantis.dashboard.ui.information.InformationActivity

class MainActivity : BaseActivity() {

    private var buttons = arrayOfNulls<CardView>(4)

    companion object {
        const val intentIDTag = "BUTTON_ID"
        const val choiceTag = "GENERAL_INFO_CHOICE_ID"

        lateinit var button1: CardView
        lateinit var button2: CardView
        lateinit var button3: CardView
        lateinit var button4: CardView
    }

    private lateinit var itemCountTV: TextView

    private lateinit var loginLink: TextView
    private lateinit var becomeDonor: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Main)
        setContentView(R.layout.activity_main)

        Log.d("MY_DEBUG", "name: " + UserSingleton.name)
        Log.d("MY_DEBUG", "email: " + UserSingleton.email)
        Log.d("MY_DEBUG", "isAdmin: " + UserSingleton.isAdmin.toString())

        //region Assignments
        itemCountTV = findViewById<TextView>(R.id.main_item_count)
        loginLink = findViewById(R.id.loginLink)
        becomeDonor = findViewById(R.id.becomeDonor)
        button1 = findViewById(R.id.acolhimento_casas_lares)
        button2 = findViewById(R.id.fortalecimento_familiar)
        button3 = findViewById(R.id.juventudes)
        button4 = findViewById(R.id.indicadores_gerais)
        //endregion

        loginLink.movementMethod = LinkMovementMethod.getInstance()
        becomeDonor.movementMethod = LinkMovementMethod.getInstance()

        buttons[0] = button1
        buttons[1] = button2
        buttons[2] = button3

        val itemsCount = findViewById<GridLayout>(R.id.main_grid_layout).childCount
        itemCountTV.text = getString(R.string.information_item_count, itemsCount)

        for (i in buttons.indices) {
            buttons[i]?.let { it ->
                it.setOnClickListener { buttonCV ->
                    val id = buttonCV.id
                    val intent = Intent(this, InformationActivity::class.java)
                    intent.putExtra(intentIDTag, id)

                    val bundle = ActivityOptionsCompat.makeCustomAnimation(
                            this@MainActivity,
                        R.anim.slide_in_up, R.anim.slide_out_up
                    ).toBundle()

                    startActivity(intent, bundle)
                }
            }
        }

        // "Button 4" has two subtopics, it will open a Dialog to choose
        button4.setOnClickListener {
            val choiceDialog = Dialog(this)
            choiceDialog.setContentView(R.layout.dialog_general_info)

            val i = Intent(this, InformationActivity::class.java)

            choiceDialog.findViewById<Button>(R.id.mes_anterior).setOnClickListener(
                GeneralIndicatorsChoiceListener(this, i, choiceDialog, 1)
            )

            choiceDialog.findViewById<Button>(R.id.ano_anterior).setOnClickListener(
                GeneralIndicatorsChoiceListener(this, i, choiceDialog, 2)
            )

            choiceDialog.show()
        }
    }

    class GeneralIndicatorsChoiceListener(
        private val context: Context,
        private val intent: Intent,
        private val choiceDialog: Dialog,
        private val choice: Int
    ) : View.OnClickListener {
        override fun onClick(v: View?) {
            val extras = Bundle()
            extras.putInt(intentIDTag, button4.id)
            extras.putInt(choiceTag, 1)

            intent.putExtras(extras)

            val options = ActivityOptionsCompat.makeCustomAnimation(
                context,
                R.anim.slide_in_up, R.anim.slide_out_up
            ).toBundle()

            context.startActivity(intent, options)
            choiceDialog.dismiss()
        }
    }

}