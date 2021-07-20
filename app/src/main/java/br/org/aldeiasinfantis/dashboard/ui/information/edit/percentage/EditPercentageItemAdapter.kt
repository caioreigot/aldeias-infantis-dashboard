package br.org.aldeiasinfantis.dashboard.ui.information.edit.percentage

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import br.org.aldeiasinfantis.dashboard.R
import br.org.aldeiasinfantis.dashboard.data.model.Information


class EditPercentageItemAdapter(
    private val informationData: MutableList<Information>,
    private val scrollToPosition: (position: Int) -> Unit,
) : RecyclerView.Adapter<EditPercentageItemAdapter.InformationViewHolder>() {

    private val views: MutableList<View> = mutableListOf()

    inner class InformationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var indicatorET: EditText = itemView.findViewById(R.id.item_indicator)
        private var percentageET: EditText = itemView.findViewById(R.id.item_percentage)

        fun bind(info: Information) {
            indicatorET.setText(info.header)
            percentageET.setText(info.percentage)

            views.add(itemView)
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
        holder.bind(informationData[position])
    }

    override fun getItemCount(): Int = informationData.size

    fun getData(): MutableList<Information> {
        val list = mutableListOf<Information>()

        for (i in views.indices) {
            list.add(Information(
                header = views[i].findViewById<EditText>(R.id.item_indicator).text.toString(),
                percentage = views[i].findViewById<EditText>(R.id.item_percentage).text.toString()
            ))
        }

        return list
    }

    fun itemSwiped(position: Int) {
        informationData.removeAt(position)
        views.removeAt(position)
        notifyItemRemoved(position)
    }

    fun insertItem() {
        val position = informationData.size
        informationData.add(position, Information())
        scrollToPosition(position)
        notifyItemInserted(position)
    }
}