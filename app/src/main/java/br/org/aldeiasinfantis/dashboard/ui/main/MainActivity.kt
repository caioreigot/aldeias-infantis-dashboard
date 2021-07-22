package br.org.aldeiasinfantis.dashboard.ui.main

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import br.org.aldeiasinfantis.dashboard.R
import br.org.aldeiasinfantis.dashboard.data.local.Preferences
import br.org.aldeiasinfantis.dashboard.data.model.MessageType
import br.org.aldeiasinfantis.dashboard.data.model.Singleton
import br.org.aldeiasinfantis.dashboard.data.model.UserSingleton
import br.org.aldeiasinfantis.dashboard.ui.BaseActivity
import br.org.aldeiasinfantis.dashboard.ui.information.InformationActivity
import br.org.aldeiasinfantis.dashboard.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import java.lang.IllegalArgumentException

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    companion object {
        const val INTENT_ID_TAG = "BUTTON_ID"
        const val CHOICE_TAG = "GENERAL_INFO_CHOICE_ID"

        lateinit var button1: CardView
        lateinit var button2: CardView
        lateinit var button3: CardView
        lateinit var button4: CardView
    }

    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var mLoadingDialog: Dialog
    private var mGeneralIndicatorsButtonClicked: Boolean = false
    private var mShowGeneralIndicatorsLoadingDialog: Boolean = true

    private var mComparativeMonth: String? = null
    private var mComparativeYear: String? = null

    private var mIntent: Intent? = null

    private var buttons: MutableList<CardView> = mutableListOf()

    private lateinit var logoutBtn: ImageButton
    private lateinit var itemCountTV: TextView
    private lateinit var loginLink: TextView
    private lateinit var becomeDonor: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Main)
        setContentView(R.layout.activity_main)

        //region Assignments
        mLoadingDialog = Dialog(this)

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
                if (mIntent != null)
                    return@setOnClickListener

                mIntent = Intent(this, InformationActivity::class.java)

                mIntent?.let { i ->
                    val id = buttonCV.id

                    i.putExtra(INTENT_ID_TAG, id)

                    val bundle = ActivityOptionsCompat.makeCustomAnimation(
                        this@MainActivity,
                        R.anim.slide_in_up, R.anim.slide_out_up
                    ).toBundle()

                    ActivityCompat.startActivity(this, i, bundle)
                } ?: throw IllegalArgumentException(
                    "Unexpected error (mIntent changed in another thread?)"
                )
            }
        }

        // "Button 4" has two subtopics, it will open a Dialog to choose
        button4.setOnClickListener {
            mGeneralIndicatorsButtonClicked = true

            if (mShowGeneralIndicatorsLoadingDialog && !mLoadingDialog.isShowing) {
                mLoadingDialog.apply {
                    setContentView(R.layout.dialog_loading)
                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    show()
                }

                return@setOnClickListener
            }

            showGeneralIndicatorsChoiceDialog()
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

                try {
                    ActivityCompat.startActivity(this, intent, options)
                } catch (e: Throwable) {
                    startActivity(intent)
                }

                supportFinishAfterTransition()
            }
        }

        with (mainViewModel) {
            getGeneralIndicatorsComparative(Singleton.DB_INDICADORES_GERAIS_MES_REF)
            getGeneralIndicatorsComparative(Singleton.DB_INDICADORES_GERAIS_ANO_REF)

            comparativeMonth.observe(this@MainActivity, {
                it?.let { comparative ->
                    mComparativeMonth = comparative

                    if (!TextUtils.isEmpty(mComparativeYear)) {
                        mShowGeneralIndicatorsLoadingDialog = false

                        if (mGeneralIndicatorsButtonClicked)
                            showGeneralIndicatorsChoiceDialog()
                    }
                }
            })

            comparativeYear.observe(this@MainActivity, {
                it?.let { comparative ->
                    mComparativeYear = comparative

                    if (!TextUtils.isEmpty(mComparativeMonth)) {
                        mShowGeneralIndicatorsLoadingDialog = false

                        if (mGeneralIndicatorsButtonClicked)
                            showGeneralIndicatorsChoiceDialog()
                    }
                }
            })
        }
    }

    private fun showGeneralIndicatorsChoiceDialog() {
        if (!TextUtils.isEmpty(mComparativeMonth) && !TextUtils.isEmpty(mComparativeYear)) {
            val choiceDialog = Dialog(this).apply {
                setContentView(R.layout.dialog_general_indicators_choice)
            }

            choiceDialog.apply {
                val lastMonthButton: Button = findViewById(R.id.mes_anterior_btn)
                val lastYearButton: Button = findViewById(R.id.ano_anterior_btn)

                lastMonthButton.apply {
                    text = mComparativeMonth
                    setOnClickListener(GeneralIndicatorsChoiceListener(choiceDialog, 1))
                }

                lastYearButton.apply {
                    text = mComparativeYear
                    setOnClickListener(GeneralIndicatorsChoiceListener(choiceDialog, 2))
                }

                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }.show()
        } else {
            println("[DEBUG] Chamando recursivamente...")

            // Call this function recursively
            Handler(Looper.getMainLooper()).postDelayed(
                { showGeneralIndicatorsChoiceDialog() }, 1000
            )
        }
    }

    inner class GeneralIndicatorsChoiceListener(
        private val choiceDialog: Dialog,
        private val choice: Int
    ) : View.OnClickListener {

        override fun onClick(v: View?) {
            if (mIntent != null)
                return

            mIntent = Intent(this@MainActivity, InformationActivity::class.java)

            mIntent?.let { i ->
                val extras = Bundle().apply {
                    putInt(INTENT_ID_TAG, button4.id)
                    putInt(CHOICE_TAG, choice)
                }

                i.putExtras(extras)

                val options = ActivityOptionsCompat.makeCustomAnimation(
                    this@MainActivity,
                    R.anim.slide_in_up, R.anim.slide_out_up
                ).toBundle()

                startActivity(i, options)
                choiceDialog.dismiss()
            } ?: throw IllegalArgumentException(
                "Unexpected error (mIntent changed in another thread?)"
            )
        }
    }

    override fun onStop() {
        super.onStop()
        mIntent = null
    }
}