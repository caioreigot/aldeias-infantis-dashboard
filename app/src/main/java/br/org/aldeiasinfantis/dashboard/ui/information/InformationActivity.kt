package br.org.aldeiasinfantis.dashboard.ui.information

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.org.aldeiasinfantis.dashboard.R
import br.org.aldeiasinfantis.dashboard.data.model.*
import br.org.aldeiasinfantis.dashboard.ui.BaseActivity
import br.org.aldeiasinfantis.dashboard.ui.main.MainActivity
import br.org.aldeiasinfantis.dashboard.ui.information.add.percentage.AddPercentageItemDialog
import br.org.aldeiasinfantis.dashboard.ui.information.add.AddValueItemDialog
import br.org.aldeiasinfantis.dashboard.ui.information.edit.EditValueItemDialog
import br.org.aldeiasinfantis.dashboard.ui.information.edit.percentage.EditPercentageItemDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.math.abs

@AndroidEntryPoint
class InformationActivity : BaseActivity() {

    private val informationViewModel: InformationViewModel by viewModels()

    private var mAdapter: InformationAdapter? = null

    private var idReceived: Int = -1

    lateinit var mInformationItemTouchHelper: InformationItemTouchHelper

    private lateinit var mCurrentInformationType: InformationType
    private lateinit var mCurrentReference: DatabaseReference

    private lateinit var recyclerViewMain: RecyclerView
    private lateinit var infoGroupName: TextView
    private lateinit var infoItemCount: TextView
    private lateinit var infoProgressBar: ProgressBar
    private lateinit var infoRefreshButton: CardView
    private lateinit var infoRefreshImage: ImageView
    private lateinit var addInformationFab: FloatingActionButton

    private lateinit var velocity: VelocityTracker

    private var mFetchingInformation: Boolean = false

    private var mLoadingDialog: Dialog? = null
    private var mAddItemDialog: DialogFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information)

        //region Assignments
        recyclerViewMain = findViewById(R.id.recycler_view_main)
        infoGroupName = findViewById(R.id.info_group_name_tv)
        infoProgressBar = findViewById(R.id.information_progress_bar)
        infoItemCount = findViewById(R.id.info_item_count)
        infoRefreshButton = findViewById(R.id.info_refresh_button)
        infoRefreshImage = findViewById(R.id.info_refresh_image)
        addInformationFab = findViewById(R.id.add_information_floating_btn)
        velocity = VelocityTracker.obtain()
        //endregion

        idReceived = intent.getIntExtra(MainActivity.INTENT_ID_TAG, -1)

        mInformationItemTouchHelper = InformationItemTouchHelper(
            this,
            ::refreshItemCount,
            ::showMessageCallback,
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        )

        recyclerViewMain.layoutManager = LinearLayoutManager(this)

        // If ID < 0, then it was not found in intent
        if (idReceived < 0) {
            createMessageDialog(
                MessageType.ERROR,
                getString(R.string.unexpected_error_message),
                null,
                null,
                { finish() }
            ).apply {
                show(supportFragmentManager, this.tag)
            }

            return
        }

        //Observers
        with (informationViewModel) {
            val thisActivity = this@InformationActivity

            indicatorTitle.observe(thisActivity, {
                it?.let { title ->
                    infoGroupName.text = title
                }
            })

            informationDataPair.observe(thisActivity, {
                it?.let { informationPair ->
                    loadUIAndRecyclerView(informationPair.first, informationPair.second)
                }
            })

            showDeletingDialog.observe(thisActivity, {
                it?.let { showDialog ->
                    if (!showDialog) {
                        mLoadingDialog?.dismiss()
                        return@observe
                    }

                    mLoadingDialog = Dialog(thisActivity)

                    mLoadingDialog?.apply {
                        setContentView(R.layout.dialog_loading)
                        show()
                    }
                }
            })

            errorMessage.observe(thisActivity, {
                it?.let { message ->
                    createMessageDialog(
                        MessageType.ERROR,
                        message
                    ).apply {
                        show(supportFragmentManager, this.tag)
                    }
                }
            })

            refreshInformation.observe(thisActivity, {
                refreshInformation(0)
            })
        }

        displayRequestInformation(idReceived)

        // Position matters (below displayRequestInformation)
        if (UserSingleton.isAdmin) {
            addInformationFab.visibility = View.VISIBLE

            addInformationFab.setOnClickListener(AddInformationClickListener())
        }
    }

    private fun displayRequestInformation(idReceived: Int) {
        when (idReceived) {
            MainActivity.button1.id -> {
                informationViewModel.fetchInformationTitle(Singleton.DB_ACOLHIMENTO_CASAS_LARES_REF)

                // Reference of the respective indicator and the type of information
                mCurrentReference = Singleton.DB_ACOLHIMENTO_CASAS_LARES_REF
                mCurrentInformationType = InformationType.VALUE

                informationViewModel.fetchDatabaseInformation(mCurrentInformationType, mCurrentReference)
            }

            MainActivity.button2.id -> {
                informationViewModel.fetchInformationTitle(Singleton.DB_FORTALECIMENTO_FAMILIAR_REF)

                // Reference of the respective indicator and the type of information
                mCurrentReference = Singleton.DB_FORTALECIMENTO_FAMILIAR_REF
                mCurrentInformationType = InformationType.VALUE

                informationViewModel.fetchDatabaseInformation(mCurrentInformationType, mCurrentReference)
            }

            MainActivity.button3.id -> {
                informationViewModel.fetchInformationTitle(Singleton.DB_JUVENTUDES_REF)

                // Reference of the respective indicator and the type of information
                mCurrentReference = Singleton.DB_JUVENTUDES_REF
                mCurrentInformationType = InformationType.VALUE

                informationViewModel.fetchDatabaseInformation(mCurrentInformationType, mCurrentReference)
            }

            MainActivity.button4.id -> {
                when (intent.getIntExtra(MainActivity.CHOICE_TAG, -1)) {
                    IndicatorsChoice.MES_ANTERIOR.ordinal -> {
                        informationViewModel.fetchInformationTitle(Singleton.DB_INDICADORES_GERAIS_MES_REF)

                        // Reference of the respective indicator and the type of information
                        mCurrentReference = Singleton.DB_INDICADORES_GERAIS_MES_REF
                        mCurrentInformationType = InformationType.PERCENTAGE

                        informationViewModel.fetchDatabaseInformation(mCurrentInformationType, mCurrentReference)
                    }

                    IndicatorsChoice.ANO_ANTERIOR.ordinal -> {
                        informationViewModel.fetchInformationTitle(Singleton.DB_INDICADORES_GERAIS_ANO_REF)

                        // Reference of the respective indicator and the type of information
                        mCurrentReference = Singleton.DB_INDICADORES_GERAIS_ANO_REF
                        mCurrentInformationType = InformationType.PERCENTAGE

                        informationViewModel.fetchDatabaseInformation(mCurrentInformationType, mCurrentReference)
                    }
                }
            }
        }
    }

    private fun loadUIAndRecyclerView(
        informationData: MutableList<Information>,
        subInformationParent: MutableList<MutableList<Information>>,
    ) {
        mFetchingInformation = false

        mAdapter = InformationAdapter(
            informationData,
            mCurrentInformationType,
            subInformationParent,
            ::scrollRecyclerViewTo,
            ::deleteDatabaseItem,
            ::showEditDialog
        )

        recyclerViewMain.adapter = mAdapter

        if (UserSingleton.isAdmin) {
            mInformationItemTouchHelper.setup(mAdapter)
            ItemTouchHelper(mInformationItemTouchHelper).attachToRecyclerView(recyclerViewMain)

            recyclerViewMain.addOnScrollListener(RecyclerViewFabOnScrollListener(
                this, addInformationFab
            ))
        }

        // If refresh button is running (animated), then cancel
        infoRefreshImage.animation?.cancel()

        infoProgressBar.visibility = View.GONE

        infoItemCount.text =
            if (informationData.size <= 1)
                getString(R.string.information_item_count, informationData.size)
            else
                getString(R.string.information_items_count, informationData.size)

        infoRefreshButton.visibility = View.VISIBLE
        infoRefreshImage.visibility = View.VISIBLE

        // Animation of "refresh button"
        val rotateAnimation = RotateAnimation(0.0F, 720.0F,
                Animation.RELATIVE_TO_SELF, 0.5F,
                Animation.RELATIVE_TO_SELF, 0.5f)

        rotateAnimation.repeatCount = Animation.INFINITE
        rotateAnimation.duration = 1200

        infoRefreshButton.setOnClickListener {
            infoRefreshImage.startAnimation(rotateAnimation)
            refreshInformation(600)
        }
    }

    private fun refreshInformation(delayInMillis: Long = 0) {
        if (mFetchingInformation)
            return

        mFetchingInformation = true

        // Clear adapter
        recyclerViewMain.adapter = null

        Handler(Looper.getMainLooper()).postDelayed({
            informationViewModel.fetchDatabaseInformation(
                mCurrentInformationType, mCurrentReference
            )
        }, delayInMillis)
    }

    private fun scrollRecyclerViewTo(position: Int) =
        recyclerViewMain.scrollToPosition(position)

    private fun refreshItemCount() {
        val value = mAdapter?.informationData?.size

        value?.let {
            infoItemCount.text =
                if (value <= 1)
                    getString(R.string.information_item_count, value)
                else
                    getString(R.string.information_items_count, value)
        }
    }

    private fun deleteDatabaseItem(path: String) =
        informationViewModel.deleteItem(path)

    private fun showEditDialog(
        info: Information,
        subInfo: MutableList<Information>?,
        infoType: InformationType
    ) {
        when (infoType) {
            InformationType.VALUE ->
                EditValueItemDialog(info, ::refreshInformation, ::showMessageCallback)
                    .apply { show(supportFragmentManager, tag) }

            InformationType.PERCENTAGE ->
                EditPercentageItemDialog(info, subInfo, ::refreshInformation, ::showMessageCallback)
                    .apply { show(supportFragmentManager, tag) }

            InformationType.TEXT -> {}
        }
    }

    inner class AddInformationClickListener : View.OnClickListener {

        override fun onClick(v: View?) {
            val isShowing = mAddItemDialog?.dialog?.isShowing

            if (isShowing != null && isShowing == true)
                return

            when (mCurrentInformationType) {
                InformationType.VALUE -> {
                    mAddItemDialog = AddValueItemDialog(
                        mCurrentReference,
                        ::refreshInformation,
                        ::showMessageCallback
                    )

                    mAddItemDialog?.let {
                        it.show(supportFragmentManager, it.tag)
                    }
                }

                InformationType.PERCENTAGE -> {
                    mAddItemDialog = AddPercentageItemDialog(
                        mCurrentReference,
                        ::refreshInformation,
                        ::showMessageCallback
                    )

                    mAddItemDialog?.let {
                        it.show(supportFragmentManager, it.tag)
                    }
                }

                InformationType.TEXT -> {}
            }
        }
    }

    private fun showMessageCallback(
        messageType: MessageType,
        content: String,
        positiveOnClickListener: (() -> Unit)? = null,
        negativeOnClickListener: (() -> Unit)? = null,
        dismissCallback: ((choice: Boolean?) -> Unit)? = null
    ) {
        createMessageDialog(
            messageType,
            content,
            positiveOnClickListener,
            negativeOnClickListener,
            dismissCallback
        ).apply {
            show(supportFragmentManager, this.tag)
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down)
    }

    private var alreadyCalled = false
    private var y1: Float = 0F
    private var y2: Float = 0F

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val height = Resources.getSystem().displayMetrics.heightPixels

        // minDistance = width * x% of Screen Width
        val minDistance = (height * 0.20)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> y1 = event.y

            MotionEvent.ACTION_UP -> {
                y2 = event.y

                val deltaY = y2 - y1

                if (abs(deltaY) > minDistance)
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