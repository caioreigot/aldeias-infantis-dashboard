package br.org.aldeiasinfantis.dashboard.ui.information.edit.percentage

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.org.aldeiasinfantis.dashboard.R
import br.org.aldeiasinfantis.dashboard.data.model.Information
import br.org.aldeiasinfantis.dashboard.data.model.MessageType
import br.org.aldeiasinfantis.dashboard.ui.information.edit.EditItemViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EditPercentageItemDialog @Inject constructor(
    private val information: Information,
    private val subInformation: MutableList<Information>?,
    private val refreshInformation: (() -> Unit),
    private val showMessageCallback: (
        messageType: MessageType,
        content: String,
        positiveOnClickListener: (() -> Unit)?,
        negativeOnClickListener: (() -> Unit)?,
        dismissCallback: ((choice: Boolean?) -> Unit)?) -> Unit
) : DialogFragment(R.layout.dialog_edit_percentage_item) {

    private val editItemViewModel: EditItemViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private var mAdapter: EditPercentageItemAdapter? = null

    private lateinit var informationHeader: EditText
    private lateinit var plusAddBtn: ImageButton
    private lateinit var saveBtn: TextView
    private lateinit var viewFlipper: ViewFlipper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //region Assignments
        with (view) {
            recyclerView = view.findViewById(R.id.recycler_view)
            informationHeader = findViewById(R.id.information_header)
            plusAddBtn = findViewById(R.id.plus_add_btn_iv)
            saveBtn = findViewById(R.id.add_to_dashboard_btn_tv)
            viewFlipper = findViewById(R.id.add_view_flipper)
        }
        //endregion

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        informationHeader.setText(information.header)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.assignPercentageAdapter()

        val helper = ItemTouchHelper(
            EditItemTouchHelper(0,
                ItemTouchHelper.LEFT
                        or ItemTouchHelper.RIGHT
            )
        )

        helper.attachToRecyclerView(recyclerView)

        plusAddBtn.setOnClickListener {
            mAdapter?.insertItem()
        }

        saveBtn.setOnClickListener {
            mAdapter?.let { adapter ->
                val infoData = adapter.getData()

                val subInfoTreated = mutableListOf<Information>()

                for (i in infoData.indices) {
                    with (infoData[i]) {
                        if (TextUtils.isEmpty(percentage) || TextUtils.isEmpty(header)) {
                            Toast.makeText(
                                requireContext(),
                                requireContext().getString(R.string.empty_field_error_message),
                                Toast.LENGTH_LONG
                            ).show()

                            return@setOnClickListener
                        }

                        val lastChar = percentage[percentage.length - 1]

                        if (lastChar != '%')
                            percentage = StringBuilder(percentage)
                                .insert(percentage.length, "%")
                                .toString()

                        subInfoTreated.add(this)
                    }
                }

                editItemViewModel.editPercentageItem(
                    information.path,
                    informationHeader.text.toString(),
                    subInfoTreated
                )
            }
        }

        editItemViewModel.viewFlipperChildToDisplay.observe(viewLifecycleOwner, {
            it?.let { childToDisplay ->
                viewFlipper.displayedChild = childToDisplay
            }
        })

        editItemViewModel.notifyItemEdited.observe(viewLifecycleOwner, {
            refreshInformation()
            dismiss()
        })

        editItemViewModel.errorMessage.observe(viewLifecycleOwner, {
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
        subInformation?.let { infoData ->
            val infoCopy: MutableList<Information> = mutableListOf()
            infoCopy.addAll(infoData)

            val adapter = EditPercentageItemAdapter(
                infoCopy,
                ::scrollRecyclerViewTo
            )

            this.adapter = adapter
            mAdapter = adapter
        }
    }

    private fun scrollRecyclerViewTo(position: Int) =
        recyclerView.scrollToPosition(position)

    inner class EditItemTouchHelper(
        dragDirs: Int, swipeDirs: Int
    ) : ItemTouchHelper.SimpleCallback(
        dragDirs, swipeDirs
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder) : Boolean
        { return false }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            mAdapter?.let { adapter ->
                val position = viewHolder.adapterPosition
                adapter.itemSwiped(position)
            }
        }
    }
}