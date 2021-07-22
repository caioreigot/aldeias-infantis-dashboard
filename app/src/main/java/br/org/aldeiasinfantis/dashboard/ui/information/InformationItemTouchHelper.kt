package br.org.aldeiasinfantis.dashboard.ui.information

import android.content.Context
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import br.org.aldeiasinfantis.dashboard.R
import br.org.aldeiasinfantis.dashboard.data.model.MessageType

class InformationItemTouchHelper(
    private val context: Context,
    private val refreshItemCount: () -> Unit,
    private val showMessageDialog: (
        messageType: MessageType,
        content: String,
        positiveOnClickListener: (() -> Unit)?,
        negativeOnClickListener: (() -> Unit)?,
        dismissCallback: ((choice: Boolean?) -> Unit)?
    ) -> Unit,
    dragDirs: Int,
    swipeDirs: Int
) : ItemTouchHelper.SimpleCallback(
    dragDirs, swipeDirs
) {

    var mAdapter: InformationAdapter? = null

    fun setup(adapter: InformationAdapter?) {
        mAdapter = adapter
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        mAdapter?.let { adapter ->
            val position = viewHolder.adapterPosition

            adapter.itemSwiped(position)
            refreshItemCount()

            showMessageDialog(
                MessageType.CONFIRMATION,
                context.getString(R.string.delete_information_confirmation_message),
                // Positive OnClickListener
                {
                    adapter.deleteItem()
                    refreshItemCount()
                },

                // Negative OnClickListener
                {
                    adapter.undoDelete()
                    refreshItemCount()
                },

                // onDismiss Dialog Callback
                { choice ->
                    // if user closed the dialog without choosing an option
                    if (choice == null) {
                        adapter.undoDelete()
                        refreshItemCount()
                    }
                }
            )
        } ?: throw IllegalArgumentException(
            "mAdapter is null! (did you forget to call the setup() function?)"
        )
    }
}