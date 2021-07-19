package br.org.aldeiasinfantis.dashboard.ui.information.add.percentage

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.org.aldeiasinfantis.dashboard.R
import br.org.aldeiasinfantis.dashboard.data.model.MessageType
import br.org.aldeiasinfantis.dashboard.ui.information.add.AddItemViewModel
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddPercentageItemDialog @Inject constructor(
    private val referenceToAdd: DatabaseReference,
    private val refreshInformation: (() -> Unit),
    private val showMessageCallback: (
        messageType: MessageType,
        content: String,
        positiveOnClickListener: (() -> Unit)?,
        negativeOnClickListener: (() -> Unit)?,
        dismissCallback: ((choice: Boolean?) -> Unit)?) -> Unit
) : DialogFragment(R.layout.dialog_add_percentage_item) {

    private val addItemViewModel: AddItemViewModel by viewModels()

    private val INITIAL_SIZE = 1

    private lateinit var recyclerView: RecyclerView
    private var mAdapter: AddPercentageItemAdapter? = null

    private lateinit var informationHeader: EditText
    private lateinit var plusAddBtn: ImageButton
    private lateinit var addToDashboardBtn: TextView
    private lateinit var viewFlipper: ViewFlipper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //region Assignments
        with (view) {
            recyclerView = view.findViewById(R.id.recycler_view)
            informationHeader = findViewById(R.id.information_header)
            plusAddBtn = findViewById(R.id.plus_add_btn_iv)
            addToDashboardBtn = findViewById(R.id.add_to_dashboard_btn_tv)
            viewFlipper = findViewById(R.id.add_view_flipper)
        }
        //endregion

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.assignPercentageAdapter()

        val helper = ItemTouchHelper(
            ItemTouchHelper(0,
                androidx.recyclerview.widget.ItemTouchHelper.LEFT
                        or androidx.recyclerview.widget.ItemTouchHelper.RIGHT
            )
        )

        helper.attachToRecyclerView(recyclerView)

        plusAddBtn.setOnClickListener {
            mAdapter?.insertItem()
        }

        addToDashboardBtn.setOnClickListener {
            mAdapter?.let { itAdapter ->
                val adapterViewsTreated = mutableListOf<View>()

                for (i in itAdapter.views.indices) {
                    with (itAdapter.views[i]) {
                        val percentageText = findViewById<EditText>(R.id.item_percentage).text
                        val lastChar = percentageText[percentageText.length - 1]

                        if (lastChar != '%')
                            percentageText.insert(percentageText.length, "%")

                        adapterViewsTreated.add(this)
                    }
                }

                addItemViewModel.addPercentageItem(
                    referenceToAdd,
                    informationHeader.text.toString(),
                    adapterViewsTreated
                )
            }
        }

        addItemViewModel.viewFlipperChildToDisplay.observe(viewLifecycleOwner, {
            it?.let { childToDisplay ->
                viewFlipper.displayedChild = childToDisplay
            }
        })

        addItemViewModel.notifyItemAdded.observe(viewLifecycleOwner, {
            informationHeader.text.clear()
            recyclerView.assignPercentageAdapter()

            refreshInformation()
        })

        addItemViewModel.errorMessage.observe(viewLifecycleOwner, {
            it?.let { message ->
                showMessageCallback(
                    MessageType.ERROR,
                    message,
                    null,
                    null,
                    null
                )

                dismiss()
            }
        })
    }

    private fun RecyclerView.assignPercentageAdapter() {
        val adapter = AddPercentageItemAdapter(
            INITIAL_SIZE,
            ::scrollRecyclerViewTo
        )

        this.adapter = adapter
        mAdapter = adapter
    }

    private fun scrollRecyclerViewTo(position: Int) =
        recyclerView.scrollToPosition(position)

    inner class ItemTouchHelper(
        dragDirs: Int, swipeDirs: Int
    ) : androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback(
        dragDirs, swipeDirs
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder) : Boolean
        { return false }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            mAdapter?.let { itAdapter ->
                val position = viewHolder.adapterPosition
                itAdapter.itemSwiped(position)
            }
        }
    }
}