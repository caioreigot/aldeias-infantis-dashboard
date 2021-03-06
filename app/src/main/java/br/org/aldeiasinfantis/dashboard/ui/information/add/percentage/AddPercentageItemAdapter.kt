package br.org.aldeiasinfantis.dashboard.ui.information.add.percentage

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InformationViewHolder {
        val layoutToInflate = R.layout.item_percentage_editable_unique
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(layoutToInflate, parent, false)

        val itemIndicator: EditText = view.findViewById(R.id.item_indicator)

        itemIndicator.setOnTouchListener { v, event ->
            if (v.id == R.id.item_indicator) {
                val canScrollUp = v.canScrollVertically(-1)
                val canScrollDown = v.canScrollVertically(1)

                if (canScrollDown || canScrollUp) {
                    v.parent.requestDisallowInterceptTouchEvent(true)

                    when (event.action and MotionEvent.ACTION_MASK) {
                        MotionEvent.ACTION_UP ->
                            v.parent.requestDisallowInterceptTouchEvent(false)
                    }
                }
            }

            false
        }

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