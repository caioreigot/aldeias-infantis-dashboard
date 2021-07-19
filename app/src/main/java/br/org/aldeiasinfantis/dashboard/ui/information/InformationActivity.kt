package br.org.aldeiasinfantis.dashboard.ui.information

import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.org.aldeiasinfantis.dashboard.R
import br.org.aldeiasinfantis.dashboard.data.helper.ResourceProvider
import br.org.aldeiasinfantis.dashboard.data.model.*
import br.org.aldeiasinfantis.dashboard.ui.BaseActivity
import br.org.aldeiasinfantis.dashboard.ui.MainActivity
import br.org.aldeiasinfantis.dashboard.ui.information.add.percentage.AddPercentageItemDialog
import br.org.aldeiasinfantis.dashboard.ui.information.add.AddValueItemDialog
import br.org.aldeiasinfantis.dashboard.ui.information.edit.EditValueItemDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.math.abs

@AndroidEntryPoint
class InformationActivity : BaseActivity() {

    private val informationViewModel: InformationViewModel by viewModels()

    private var adapter: InformationAdapter? = null

    private var idReceived: Int = -1

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

        idReceived = intent.getIntExtra(MainActivity.intentIDTag, -1)

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

        //region Observers
        with (informationViewModel) {
            val thisActivity = this@InformationActivity

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
        //endregion

        displayRequestInformation(idReceived)

        // Position matters (below displayRequestInformation)
        if (UserSingleton.isAdmin) {
            addInformationFab.visibility = View.VISIBLE

            addInformationFab.setOnClickListener(AddInformationClickListener(this))
        }
    }

    private fun displayRequestInformation(idReceived: Int) {
        when (idReceived) {
            MainActivity.button1.id -> {
                infoGroupName.text = getString(R.string.acolhimento_casas_lares)

                // Reference of the respective indicator and the type of information
                mCurrentReference = Singleton.DB_ACOLHIMENTO_CASAS_LARES_REF
                mCurrentInformationType = InformationType.VALUE

                informationViewModel.fetchDatabaseInformation(mCurrentInformationType, mCurrentReference)
            }

            MainActivity.button2.id -> {
                infoGroupName.text = getString(R.string.fortalecimento_familiar)

                // Reference of the respective indicator and the type of information
                mCurrentReference = Singleton.DB_FORTALECIMENTO_FAMILIAR_REF
                mCurrentInformationType = InformationType.VALUE

                informationViewModel.fetchDatabaseInformation(mCurrentInformationType, mCurrentReference)
            }

            MainActivity.button3.id -> {
                infoGroupName.text = getString(R.string.juventudes)

                // Reference of the respective indicator and the type of information
                mCurrentReference = Singleton.DB_JUVENTUDES_REF
                mCurrentInformationType = InformationType.VALUE

                informationViewModel.fetchDatabaseInformation(mCurrentInformationType, mCurrentReference)
            }

            MainActivity.button4.id -> {
                val choiceIdReceived = intent.getIntExtra(MainActivity.choiceTag, -1)

                when (choiceIdReceived) {
                    // ID 1 = MES ANTERIOR
                    1 -> {
                        infoGroupName.text = getString(R.string.indicadores_gerais_mes)

                        // Reference of the respective indicator and the type of information
                        mCurrentReference = Singleton.DB_INDICADORES_GERAIS_MES_REF
                        mCurrentInformationType = InformationType.PERCENTAGE

                        informationViewModel.fetchDatabaseInformation(mCurrentInformationType, mCurrentReference)
                    }

                    // ID 2 = ANO ANTERIOR
                    2 -> {
                        infoGroupName.text = getString(R.string.indicadores_gerais_ano)

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

        adapter = InformationAdapter(
            informationData,
            mCurrentInformationType,
            subInformationParent,
            ::scrollRecyclerViewTo,
            ::deleteDatabaseItem,
            ::showEditDialog
        )

        recyclerViewMain.adapter = adapter

        if (UserSingleton.isAdmin) {
            val helper = ItemTouchHelper(
                ItemTouchHelper(0,
                    androidx.recyclerview.widget.ItemTouchHelper.LEFT
                            or androidx.recyclerview.widget.ItemTouchHelper.RIGHT
                )
            )

            helper.attachToRecyclerView(recyclerViewMain)

            recyclerViewMain.addOnScrollListener(RecyclerViewOnScrollListener(this))
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

    inner class ItemTouchHelper(
        dragDirs: Int, swipeDirs: Int
    ) : androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback(
        dragDirs, swipeDirs
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder)
        : Boolean { return false }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            adapter?.let { itAdapter ->
                val position = viewHolder.adapterPosition

                itAdapter.itemSwiped(position)
                refreshItemCount()

                createMessageDialog(
                    MessageType.CONFIRMATION,
                    getString(R.string.delete_information_confirmation_message),
                    // Positive OnClickListener
                    {
                        itAdapter.deleteItem()
                        refreshItemCount()
                    },

                    // Negative OnClickListener
                    {
                        itAdapter.undoDelete()
                        refreshItemCount()
                    },

                    // onDismiss Dialog Callback
                    { choice ->
                        // if user closed the dialog without choosing an option
                        if (choice == null) {
                            itAdapter.undoDelete()
                            refreshItemCount()
                        }
                    }
                ).apply {
                    show(supportFragmentManager, this.tag)
                }
            }
        }
    }

    private fun scrollRecyclerViewTo(position: Int) =
        recyclerViewMain.scrollToPosition(position)

    private fun refreshItemCount() {
        val value = adapter?.informationData?.size

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

    private fun showEditDialog(info: Information) {
        EditValueItemDialog(info, ::refreshInformation, ::showMessageCallback).apply {
            show(supportFragmentManager, tag)
        }
    }

    inner class AddInformationClickListener(
        private val context: Context,
    ) : View.OnClickListener {

        override fun onClick(v: View?) {
            val activity = (context as FragmentActivity)

            when (mCurrentInformationType) {
                InformationType.VALUE -> {
                    val addValueItemDialog = AddValueItemDialog(
                        mCurrentReference,
                        ::refreshInformation,
                        ::showMessageCallback
                    )

                    addValueItemDialog.show(activity.supportFragmentManager, addValueItemDialog.tag)
                }

                InformationType.PERCENTAGE -> {
                    val addPercentageItemDialog = AddPercentageItemDialog(
                        mCurrentReference,
                        ::refreshInformation,
                        ::showMessageCallback
                    )

                    addPercentageItemDialog.show(activity.supportFragmentManager, addPercentageItemDialog.tag)

/*                    createMessageDialog(
                        MessageType.ERROR,
                        "Este recurso que permite adicionar informações aos indicadores gerais ainda está em desenvolvimento"
                    ).apply {
                        show(supportFragmentManager, this.tag)
                    }*/
                }

                InformationType.TEXT -> {/*TODO*/}
            }
        }
    }

    inner class RecyclerViewOnScrollListener(context: Context) : RecyclerView.OnScrollListener() {

        private var itsInside = true
        private var animating = false

        private val fabExit: Animation = AnimationUtils.loadAnimation(
            context, R.anim.fab_exit)

        private val fabEnter: Animation = AnimationUtils.loadAnimation(
            context, R.anim.fab_enter)

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            // Scrolling up
            if (dy < 0 && !itsInside && !animating) {
                fabEnter.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationEnd(animation: Animation?) {
                        itsInside = true
                        animating = false

                        addInformationFab.isEnabled = true
                        addInformationFab.isClickable = true
                    }

                    override fun onAnimationStart(animation: Animation?) {
                        addInformationFab.isEnabled = true
                        animating = true
                    }

                    override fun onAnimationRepeat(animation: Animation?) {}
                })

                addInformationFab.startAnimation(fabEnter)
            }

            if (!recyclerView.canScrollVertically(1) &&
                recyclerView.canScrollVertically(-1) &&
                itsInside &&
                !animating
            ) {
                fabExit.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationEnd(animation: Animation?) {
                        addInformationFab.isEnabled = false
                        itsInside = false
                        animating = false
                    }

                    override fun onAnimationStart(animation: Animation?) {
                        addInformationFab.isClickable = false
                        animating = true
                    }

                    override fun onAnimationRepeat(animation: Animation?) {}
                })

                addInformationFab.startAnimation(fabExit)
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