package br.org.aldeiasinfantis.dashboard.ui.information

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.org.aldeiasinfantis.dashboard.R
import br.org.aldeiasinfantis.dashboard.data.helper.Utils
import br.org.aldeiasinfantis.dashboard.data.model.Information
import br.org.aldeiasinfantis.dashboard.data.model.InformationType
import br.org.aldeiasinfantis.dashboard.data.model.Singleton
import br.org.aldeiasinfantis.dashboard.ui.BaseActivity
import br.org.aldeiasinfantis.dashboard.ui.MainActivity
import com.google.firebase.database.*
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Math.abs
import kotlin.system.exitProcess

@AndroidEntryPoint
class InformationActivity : BaseActivity() {

    private val informationViewModel: InformationViewModel by viewModels()

    private var idReceived: Int = -1

    private lateinit var currentInformationType: InformationType
    private lateinit var selectorReference: DatabaseReference

    private lateinit var recyclerViewMain: RecyclerView
    private lateinit var infoGroupName: TextView
    private lateinit var infoItemCount: TextView
    private lateinit var infoProgressBar: ProgressBar
    private lateinit var infoRefreshButton: CardView
    private lateinit var infoRefreshImage: ImageView

    private lateinit var velocity: VelocityTracker

    private var alreadyCalled: Boolean = false
    private var fetchingInformation: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_informations)

        //region Assignments
        recyclerViewMain = findViewById(R.id.recycler_view_main)
        infoGroupName = findViewById(R.id.info_group_name_tv)
        infoProgressBar = findViewById(R.id.information_progress_bar)
        infoItemCount = findViewById(R.id.info_item_count)
        infoRefreshButton = findViewById(R.id.info_refresh_button)
        infoRefreshImage = findViewById(R.id.info_refresh_image)
        velocity = VelocityTracker.obtain()
        //endregion

        idReceived = intent.getIntExtra(MainActivity.intentIDTag, -1)

        recyclerViewMain.layoutManager = LinearLayoutManager(this)

        // Se ID < 0, então ele não foi encontrado na intent
        if (idReceived < 0) {
            val dialog = Dialog(this)

            fun clickListener(v: View) {
                finish()
                exitProcess(0)
            }

            // Todo: dialog de erro
            return
        }

        informationViewModel.informationDataPair.observe(this, {
            it?.let { informationPair ->
                loadUIAndRecyclerView(informationPair.first, informationPair.second)
            }
        })

        displayRequestInformation(idReceived)
    }

    private fun displayRequestInformation(idReceived: Int) {
        when (idReceived) {
            MainActivity.button1.id -> {
                infoGroupName.text = getString(R.string.acolhimento_casas_lares)

                // Referência do respectivo indicador e o tipo de informação
                selectorReference = Singleton.DB_ACOLHIMENTO_CASAS_LARES_REF
                currentInformationType = InformationType.VALUE

                informationViewModel.fetchDatabaseInformation(currentInformationType, selectorReference)
            }

            MainActivity.button2.id -> {
                infoGroupName.text = getString(R.string.fortalecimento_familiar)

                // Referência do respectivo indicador e o tipo de informação
                selectorReference = Singleton.DB_FORTALECIMENTO_FAMILIAR_REF
                currentInformationType = InformationType.VALUE

                informationViewModel.fetchDatabaseInformation(currentInformationType, selectorReference)
            }

            MainActivity.button3.id -> {
                infoGroupName.text = getString(R.string.juventudes)

                // Referência do respectivo indicador e o tipo de informação
                selectorReference = Singleton.DB_JUVENTUDES_REF
                currentInformationType = InformationType.VALUE

                informationViewModel.fetchDatabaseInformation(currentInformationType, selectorReference)
            }

            MainActivity.button4.id -> {
                val choiceIdReceived = intent.getIntExtra(MainActivity.choiceTag, -1)

                when (choiceIdReceived) {
                    // ID 1 = Mes anterior
                    1 -> {
                        infoGroupName.text = getString(R.string.indicadores_gerais_mes)

                        // Referência do respectivo indicador e o tipo de informação
                        selectorReference = Singleton.DB_INDICADORES_GERAIS_MES_REF
                        currentInformationType = InformationType.PERCENTAGE

                        informationViewModel.fetchDatabaseInformation(currentInformationType, selectorReference)
                    }

                    // ID 2 = Ano anterior
                    2 -> {
                        infoGroupName.text = getString(R.string.indicadores_gerais_ano)

                        // Referência do respectivo indicador e o tipo de informação
                        selectorReference = Singleton.DB_INDICADORES_GERAIS_ANO_REF
                        currentInformationType = InformationType.PERCENTAGE

                        informationViewModel.fetchDatabaseInformation(currentInformationType, selectorReference)
                    }
                }
            }
        }
    }

    private fun loadUIAndRecyclerView(
        informationData: MutableList<Information>,
        subInformationParent: MutableList<MutableList<Information>>,
    ) {
        fetchingInformation = false

        // Atualizando/alimentando a RecyclerView com os itens
        recyclerViewMain.adapter = InformationAdapter(
            informationData,
            subInformationParent,
            currentInformationType,
            this
        )

        // Se o refresh button estiver rodando (animado), cancelar
        infoRefreshImage.animation?.cancel()

        infoProgressBar.visibility = View.GONE

        infoItemCount.text = getString(R.string.information_item_count, informationData.size)

        infoRefreshButton.visibility = View.VISIBLE
        infoRefreshImage.visibility = View.VISIBLE

        // Animação do "refresh button"
        val rotateAnimation = RotateAnimation(0.0F, 720.0F,
                Animation.RELATIVE_TO_SELF, 0.5F,
                Animation.RELATIVE_TO_SELF, 0.5f)

        rotateAnimation.repeatCount = Animation.INFINITE
        rotateAnimation.duration = 1200

        infoRefreshButton.setOnClickListener {
            if (fetchingInformation)
                return@setOnClickListener

            fetchingInformation = true

            infoRefreshImage.startAnimation(rotateAnimation)

            // Clear adapter
            recyclerViewMain.adapter = null

            Handler(Looper.getMainLooper()).postDelayed({
                informationViewModel.fetchDatabaseInformation(
                    currentInformationType, selectorReference
                )
            }, 600)
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down)
    }

    var y1: Float = 0F; var y2: Float = 0F

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val height = Resources.getSystem().displayMetrics.heightPixels

        // MIN_DISTANCE = width * x% of Screen Width
        val MIN_DISTANCE = (height * 0.20)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> y1 = event.y

            MotionEvent.ACTION_UP -> {
                y2 = event.y

                val deltaY = y2 - y1

                if (abs(deltaY) > MIN_DISTANCE)
                    super.onBackPressed()
            }

            MotionEvent.ACTION_MOVE -> {
                velocity.addMovement(event)
                velocity.computeCurrentVelocity(1000)

                val yVelocity: Float = velocity.yVelocity

                if (yVelocity > 4000 && !alreadyCalled)
                    super.onBackPressed()
            }
        }

        return super.onTouchEvent(event)
    }

}