package br.org.aldeiasinfantis.dashboard.ui

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityOptionsCompat
import br.org.aldeiasinfantis.dashboard.R
import br.org.aldeiasinfantis.dashboard.data.local.Preferences
import br.org.aldeiasinfantis.dashboard.data.model.MessageType
import br.org.aldeiasinfantis.dashboard.data.model.Singleton
import br.org.aldeiasinfantis.dashboard.data.model.UserSingleton
import br.org.aldeiasinfantis.dashboard.ui.information.InformationActivity
import br.org.aldeiasinfantis.dashboard.ui.login.LoginActivity

class MainActivity : BaseActivity() {

    companion object {
        const val intentIDTag = "BUTTON_ID"
        const val choiceTag = "GENERAL_INFO_CHOICE_ID"

        lateinit var button1: CardView
        lateinit var button2: CardView
        lateinit var button3: CardView
        lateinit var button4: CardView
    }

    private var buttons = mutableListOf<CardView>()

    private lateinit var logoutBtn: ImageButton
    private lateinit var itemCountTV: TextView
    private lateinit var loginLink: TextView
    private lateinit var becomeDonor: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Main)
        setContentView(R.layout.activity_main)

        //region Assignments
        logoutBtn = findViewById(R.id.logout_image_btn)
        itemCountTV = findViewById(R.id.main_item_count)
        loginLink = findViewById(R.id.loginLink)
        becomeDonor = findViewById(R.id.becomeDonor)
        button1 = findViewById(R.id.acolhimento_casas_lares)
        button2 = findViewById(R.id.fortalecimento_familiar)
        button3 = findViewById(R.id.juventudes)
        button4 = findViewById(R.id.indicadores_gerais)
        //endregion

        loginLink.movementMethod = LinkMovementMethod.getInstance()
        becomeDonor.movementMethod = LinkMovementMethod.getInstance()

        buttons.apply {
            add(button1)
            add(button2)
            add(button3)
        }

        val itemsCount = findViewById<GridLayout>(R.id.main_grid_layout).childCount
        itemCountTV.text = getString(R.string.information_items_count, itemsCount)

        for (i in buttons.indices) {
            buttons[i].setOnClickListener { buttonCV ->
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

        // "Button 4" has two subtopics, it will open a Dialog to choose
        button4.setOnClickListener {
            val choiceDialog = Dialog(this)
            choiceDialog.setContentView(R.layout.dialog_general_info)

            choiceDialog.apply {
                findViewById<Button>(R.id.mes_anterior).setOnClickListener(
                    GeneralIndicatorsChoiceListener(choiceDialog, 1)
                )

                findViewById<Button>(R.id.ano_anterior).setOnClickListener(
                    GeneralIndicatorsChoiceListener(choiceDialog, 2)
                )
            }.show()
        }

        logoutBtn.setOnClickListener {
            val messageDialog = createMessageDialog(
                MessageType.CONFIRMATION,
                getString(R.string.confirm_logout_message),
                { Singleton.AUTH.signOut() }
            )

            messageDialog.show(supportFragmentManager, messageDialog.tag)
        }

        // Responsible for logging out the player and taking it to the login screen
        Singleton.AUTH.addAuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser == null) {
                val intent = Intent(this, LoginActivity::class.java)
                intent.putExtra(LoginActivity.FADE_ANIMATION_ENABLED_EXTRA_TAG, false)

                Preferences(this).clearPreferences()
                UserSingleton.clear()

                val options = ActivityOptionsCompat.makeCustomAnimation(
                    this,
                    R.anim.slide_in_right, R.anim.slide_out_right
                ).toBundle()

                startActivity(intent, options)
                finish()
            }
        }
    }

    inner class GeneralIndicatorsChoiceListener(
        private val choiceDialog: Dialog,
        private val choice: Int
    ) : View.OnClickListener {
        override fun onClick(v: View?) {
            val context = this@MainActivity
            val intent = Intent(context, InformationActivity::class.java)

            val extras = Bundle().apply {
                putInt(intentIDTag, button4.id)
                putInt(choiceTag, choice)
            }

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