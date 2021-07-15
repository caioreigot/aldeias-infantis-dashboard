package br.org.aldeiasinfantis.dashboard.ui.information.add.percentage

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.org.aldeiasinfantis.dashboard.R
import br.org.aldeiasinfantis.dashboard.data.helper.ResourceProvider
import br.org.aldeiasinfantis.dashboard.data.model.MessageType
import br.org.aldeiasinfantis.dashboard.data.model.Singleton
import br.org.aldeiasinfantis.dashboard.ui.information.add.AddItemViewModel
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddPercentageItemDialog @Inject constructor(
    private val resProvider: ResourceProvider,
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
    private var adapter: AddPercentageItemAdapter? = null
    private lateinit var informationHeader: EditText
    private lateinit var plusAddBtn: ImageButton
    private lateinit var addToDashboardBtn: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //region Assignments
        recyclerView = view.findViewById(R.id.recycler_view)

        adapter = AddPercentageItemAdapter(
            INITIAL_SIZE,
            ::scrollRecyclerViewTo
        )

        informationHeader = view.findViewById(R.id.information_header)
        plusAddBtn = view.findViewById(R.id.plus_add_btn_iv)
        addToDashboardBtn = view.findViewById(R.id.add_to_dashboard_btn_tv)
        //endregion

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        val helper = ItemTouchHelper(
            ItemTouchHelper(0,
                androidx.recyclerview.widget.ItemTouchHelper.LEFT
                        or androidx.recyclerview.widget.ItemTouchHelper.RIGHT
            )
        )

        helper.attachToRecyclerView(recyclerView)

        plusAddBtn.setOnClickListener {
            adapter?.insertItem()
        }

        addToDashboardBtn.setOnClickListener {
            adapter?.let { itAdapter ->
                for (i in itAdapter.views.indices) {
                    println("[DEBUG] views[$i]: ${itAdapter.views[i].findViewById<EditText>(R.id.item_indicator).text}")

                    addItemViewModel.addPercentageItem(
                        referenceToAdd,
                        informationHeader.text.toString(),
                        itAdapter.views
                    )
                }
            }
        }
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
            adapter?.let { itAdapter ->
                val position = viewHolder.adapterPosition
                itAdapter.itemSwiped(position)
            }
        }
    }
}