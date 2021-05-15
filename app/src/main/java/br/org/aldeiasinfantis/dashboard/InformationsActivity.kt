package br.org.aldeiasinfantis.dashboard

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.org.aldeiasinfantis.dashboard.model.Information
import br.org.aldeiasinfantis.dashboard.model.InformationType
import com.google.firebase.database.*
import java.lang.Math.abs
import kotlin.system.exitProcess

class InformationsActivity : AppCompatActivity() {

    private var idReceived: Int = -1

    private var fetchDB: FetchDatabaseInformations? = null
    private lateinit var mDashboardReference: DatabaseReference
    private lateinit var mSelectorReference: DatabaseReference
    private lateinit var mDatabase: FirebaseDatabase

    private lateinit var recyclerViewMain: RecyclerView
    private lateinit var infoGroupName: TextView
    private lateinit var infoItemCount: TextView
    private lateinit var infoProgressBar: ProgressBar
    private lateinit var infoRefreshButton: CardView
    private lateinit var infoRefreshImage: ImageView

    private lateinit var velocity: VelocityTracker

    private var alreadyCalled: Boolean = false
    private var fetchingInformations: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_informations)

        recyclerViewMain = findViewById(R.id.recycler_view_main)
        infoProgressBar = findViewById(R.id.information_progress_bar)
        infoItemCount = findViewById(R.id.info_item_count)
        infoRefreshButton = findViewById(R.id.info_refresh_button)
        infoRefreshImage = findViewById(R.id.info_refresh_image)
        fetchDB = FetchDatabaseInformations()
        velocity = VelocityTracker.obtain()

        idReceived = intent.getIntExtra(MainActivity.intentIDTag, -1)

        // Se o ID for menor que 0, ele não foi encontrado no intent
        if (idReceived < 0) {
            val dialog = Dialog(this)

            fun clickListener(view: View) {
                finish()
                exitProcess(0)
            }

            Utils.createUnexpectedErrorDialog(dialog, this, ::clickListener).show()
            return
        }

        // Pegando a referência do dashboard no Firebase
        mDatabase = FirebaseDatabase.getInstance()
        mDashboardReference = mDatabase.reference.child("dashboard")

        displayRequestInformation(idReceived)
    }

    fun displayRequestInformation(idReceived: Int) {
        when (idReceived) {
            MainActivity.button1.id -> {
                infoGroupName = findViewById(R.id.info_group_name)
                infoGroupName.text = "ACOLHIMENTO EM CASAS LARES"
                val currentTnformationType = InformationType.VALUE

                // Referência do respectivo indicador
                mSelectorReference = mDashboardReference.child("acolhimento em casas lares")

                fetchDB?.fetchDatabaseInformations(
                        this,
                        ::loadUIAndRecyclerView,
                        currentTnformationType,
                        mSelectorReference,
                        recyclerViewMain
                )
            }

            MainActivity.button2.id -> {
                infoGroupName = findViewById(R.id.info_group_name)
                infoGroupName.text = "FORTALECIMENTO FAMILIAR"
                val currentTnformationType = InformationType.VALUE

                // Referência do respectivo indicador
                mSelectorReference = mDashboardReference.child("fortalecimento familiar")

                fetchDB?.fetchDatabaseInformations(
                        this,
                        ::loadUIAndRecyclerView,
                        currentTnformationType,
                        mSelectorReference,
                        recyclerViewMain
                )
            }

            MainActivity.button3.id -> {
                infoGroupName = findViewById(R.id.info_group_name)
                infoGroupName.text = "JUVENTUDE"
                val currentTnformationType = InformationType.VALUE

                // Referência do respectivo indicador
                mSelectorReference = mDashboardReference.child("juventudes")

                fetchDB?.fetchDatabaseInformations(
                        this,
                        ::loadUIAndRecyclerView,
                        currentTnformationType,
                        mSelectorReference,
                        recyclerViewMain
                )
            }

            MainActivity.button4.id -> {
                val choiceIdReceived = intent.getIntExtra(MainActivity.choiceTag, -1)

                when (choiceIdReceived) {
                    // ID 1 = Mes anterior
                    1 -> {
                        infoGroupName = findViewById(R.id.info_group_name)
                        infoGroupName.text = "INDICADORES GERAIS"
                        val currentTnformationType = InformationType.PERCENTAGE

                        // Referência do respectivo indicador
                        mSelectorReference = mDashboardReference.child("indicadores gerais")

                        fetchDB?.fetchDatabaseInformations(
                            this,
                            ::loadUIAndRecyclerView,
                            currentTnformationType,
                            mSelectorReference,
                            recyclerViewMain
                        )
                    }

                    // ID 2 = Ano anterior
                    2 -> {

                    }
                }
            }
        }
    }

    fun loadUIAndRecyclerView(
            informationData: MutableList<Information>,
            subInformationsParent: MutableList<MutableList<Information>>,
            informationType: InformationType,
            mReference: DatabaseReference
    ) {
        fetchingInformations = false

        // Atualizando/alimentando a recyclerView
        recyclerViewMain.adapter = InformationAdapter(informationData, subInformationsParent, informationType, this)
        recyclerViewMain.layoutManager = LinearLayoutManager(this)

        // Se o refresh button estiver rodando (animado), cancelar
        infoRefreshImage.animation?.cancel()

        infoProgressBar.visibility = View.GONE

        infoItemCount.text = getString(R.string.information_item_count, informationData.size)

        infoRefreshButton.visibility = View.VISIBLE
        infoRefreshImage.visibility = View.VISIBLE

        // Animation do refresh button
        val rotateAnimation = RotateAnimation(0.0F, 720.0F,
                Animation.RELATIVE_TO_SELF, 0.5F,
                Animation.RELATIVE_TO_SELF, 0.5f)

        rotateAnimation.repeatCount = Animation.INFINITE
        rotateAnimation.duration = 1200

        infoRefreshButton.setOnClickListener {
            if (!fetchingInformations) {
                fetchingInformations = true

                infoRefreshImage.startAnimation(rotateAnimation)

                recyclerViewMain.adapter = null
                Handler(Looper.getMainLooper()).postDelayed({
                    fetchDB?.fetchDatabaseInformations(
                            this,
                            ::loadUIAndRecyclerView,
                            informationType,
                            mReference,
                            recyclerViewMain
                    )}, 600)
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up)
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

                var yVelocity: Float = velocity.yVelocity

                if (yVelocity > 4000 && !alreadyCalled)
                    super.onBackPressed()
            }
        }

        return super.onTouchEvent(event)
    }

}