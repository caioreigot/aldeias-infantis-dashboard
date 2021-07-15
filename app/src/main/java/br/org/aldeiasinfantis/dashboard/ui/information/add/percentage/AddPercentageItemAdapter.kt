package br.org.aldeiasinfantis.dashboard.ui.information.add.percentage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.org.aldeiasinfantis.dashboard.R

class AddPercentageItemAdapter(
    var itemsSize: Int,
    private val scrollToPosition: (position: Int) -> Unit,
) : RecyclerView.Adapter<AddPercentageItemAdapter.InformationViewHolder>() {

    val views: MutableList<View> = mutableListOf()

    inner class InformationViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private var index = 0

        fun bind(position: Int) {
            index = position
            views.add(position, itemView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InformationViewHolder {
        val layoutToInflate = R.layout.item_percentage_editable_unique
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(layoutToInflate, parent, false)

        return InformationViewHolder(view)
    }

    override fun onBindViewHolder(holder: InformationViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return itemsSize
    }

    fun itemSwiped(position: Int) {
        itemsSize--
        views.removeAt(position)
        notifyItemRemoved(position)
    }

    fun insertItem() {
        itemsSize++

        val position = itemsSize - 1
        notifyItemInserted(position)
        scrollToPosition(position)
    }
}