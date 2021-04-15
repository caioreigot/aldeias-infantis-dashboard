package br.org.aldeiasinfantis.dashboard

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.org.aldeiasinfantis.dashboard.model.Information
import br.org.aldeiasinfantis.dashboard.model.information
import com.google.firebase.database.*
import java.lang.Math.abs
import kotlin.system.exitProcess

class InformationsActivity : AppCompatActivity() {

    private var idReceived: Int = -1

    private lateinit var mDashboardReference: DatabaseReference
    private lateinit var mSelectorReference: DatabaseReference
    private lateinit var mDatabase: FirebaseDatabase

    private lateinit var recyclerViewMain: RecyclerView
    private lateinit var infoGroupName: TextView
    private lateinit var infoItemCount: TextView
    private lateinit var infoProgressBar: ProgressBar
    private lateinit var infoRefreshButton: CardView
    private lateinit var infoRefreshImage: ImageView

    lateinit var velocity: VelocityTracker
    var alreadyCalled: Boolean = false
    var fetchingInformations: Boolean = false

    // Informações que serão coletadas do Firebase
    private var _header: String? = ""
    private var _content: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_informations)

        recyclerViewMain = findViewById(R.id.recycler_view_main)
        infoProgressBar = findViewById(R.id.information_progress_bar)
        infoItemCount = findViewById(R.id.info_item_count)
        infoRefreshButton = findViewById(R.id.info_refresh_button)
        infoRefreshImage = findViewById(R.id.info_refresh_image)
        velocity = VelocityTracker.obtain()

        val fetchId = intent
        idReceived = fetchId.getIntExtra(MainActivity.intentIDTag, -1)

        // Se o ID for menor que 0, ele não foi encontrado no intent
        if (idReceived < 0) {
            createMyConnectionErrorDialog(true).show()
            return
        }

        // Conexão com o Firebase
        mDatabase = FirebaseDatabase.getInstance()
        mDashboardReference = mDatabase.reference.child("dashboard")

        when (idReceived) {
            MainActivity.button1.id -> {
                infoGroupName = findViewById(R.id.info_group_name)
                infoGroupName.text = "INFORMAÇÕES: CASA"

                // Referência de "casas" do database
                mSelectorReference = mDashboardReference.child("casas")
                fetchDatabaseInformations(mSelectorReference)
            }

            MainActivity.button2.id -> {

            }

            MainActivity.button3.id -> {

            }

            MainActivity.button4.id -> {

            }
        }
    }

    fun fetchDatabaseInformations(mReference: DatabaseReference) {
        mReference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val informationData = mutableListOf<Information>()

                for (ss in snapshot.children) {
                    for (snap_shot in ss.children) {
                        _header = if (snap_shot.key == "cabecalho") snap_shot.getValue(String::class.java) else _header
                        _content = if (snap_shot.key == "conteudo") snap_shot.getValue(String::class.java) else _content
                    }

                    informationData.add(information {
                        this.header = _header!!
                        this.content = _content!!
                    })
                }

                fetchingInformations = false
                infoRefreshImage.animation?.cancel()
                loadUIAndRecyclerView(informationData, mReference)
            }

            override fun onCancelled(error: DatabaseError) {
                createMyConnectionErrorDialog(false).show()
            }
        })
    }

    fun loadUIAndRecyclerView(informationData: MutableList<Information>, mReference: DatabaseReference) {
        recyclerViewMain.adapter = InformationAdapter(informationData)
        recyclerViewMain.layoutManager = LinearLayoutManager(this)

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
                    fetchDatabaseInformations(mReference)
                }, 600)
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up)
    }

    fun createMyConnectionErrorDialog(isUnexpectedError: Boolean): Dialog {
        val dialog = Dialog(this)

        if (isUnexpectedError) {
            dialog.setContentView(R.layout.unexpected_error_dialog)
            dialog.findViewById<TextView>(R.id.textView2).text = getString(R.string.unexpected_error_message)

            val btnPositive: Button = dialog.findViewById(R.id.btn_positive)

            btnPositive.setOnClickListener {
                finish()
                exitProcess(0)
            }
        } else {
            dialog.setContentView(R.layout.connection_error_dialog)
            dialog.findViewById<TextView>(R.id.textView2).text = getString(R.string.connection_error_message)

            val btnPositive: Button = dialog.findViewById(R.id.btn_positive)
            val btnNegative: Button = dialog.findViewById(R.id.btn_negative)

            btnPositive.setOnClickListener {
                recyclerViewMain.adapter = null
                fetchDatabaseInformations(mSelectorReference)
                dialog.dismiss()
            }

            btnNegative.setOnClickListener {
                dialog.dismiss()
            }
        }

        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)

        dialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setGravity(Gravity.CENTER)

        return dialog
    }

    var y1: Float = 0F
    var y2: Float = 0F

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val height = Resources.getSystem().displayMetrics.heightPixels

        // MIN_DISTANCE = width * x% of Screen Width
        val MIN_DISTANCE = (height * 0.20)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> y1 = event.y

            MotionEvent.ACTION_UP -> {
                y2 = event.y

                val deltaY = y2 - y1
                if (abs(deltaY) > MIN_DISTANCE) {
                    super.onBackPressed()
                }
            }

            MotionEvent.ACTION_MOVE -> {
                velocity.addMovement(event)
                velocity.computeCurrentVelocity(1000)

                //var xVelocity: Float = velocity.xVelocity
                var yVelocity: Float = velocity.yVelocity

                if (yVelocity > 4000) {
                    if (!alreadyCalled)
                        super.onBackPressed()
                }
            }
        }

        return super.onTouchEvent(event)
    }

}