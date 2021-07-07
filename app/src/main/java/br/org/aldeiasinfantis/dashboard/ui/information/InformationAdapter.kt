package br.org.aldeiasinfantis.dashboard.ui.information

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.org.aldeiasinfantis.dashboard.R
import br.org.aldeiasinfantis.dashboard.data.model.Information
import br.org.aldeiasinfantis.dashboard.data.model.InformationType

class InformationAdapter(
    val informationData: MutableList<Information>,
    private val informationType: InformationType,
    private val subInformationParent: MutableList<MutableList<Information>>,
    private val scrollToPosition: (position: Int) -> Unit,
    private val deleteDatabaseItem: (path: String) -> Unit,
    private val context: Context
) : RecyclerView.Adapter<InformationAdapter.InformationViewHolder>() {

    private var mRecentlyDeletedItem: Information? = null
    private var mRecentlyDeletedItemPosition = -1

    inner class InformationViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private var uid: String = ""

        fun bind(info: Information, position: Int) {

            uid = info.uid

            when (informationType) {
                InformationType.TEXT -> {
                    val header: TextView = itemView.findViewById(R.id.information_header)
                    val content: TextView = itemView.findViewById(R.id.information_content)

                    header.text = info.header
                    content.text = info.content
                }

                InformationType.VALUE -> {
                    val header: TextView = itemView.findViewById(R.id.information_header)
                    val value: TextView = itemView.findViewById(R.id.information_value)
                    val date: TextView = itemView.findViewById(R.id.information_date)

                    header.text = info.header
                    value.text = info.value.toString()
                    date.text = info.competence
                }

                InformationType.PERCENTAGE -> {
                    val header: TextView = itemView.findViewById(R.id.information_header)
                    header.text = info.header

                    val subRecyclerView: RecyclerView = itemView.findViewById(R.id.percentage_recycle_view)

                    subRecyclerView.adapter = SubInformationAdapter(
                        InformationType.PERCENTAGE,
                        subInformationParent[position],
                        context
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InformationViewHolder {
        val layoutToInflate = when (informationType) {
            InformationType.TEXT -> R.layout.item_text
            InformationType.VALUE -> R.layout.item_value
            InformationType.PERCENTAGE -> R.layout.item_percentage
        }

        val view = LayoutInflater.from(parent.context).inflate(layoutToInflate, parent, false)
        return InformationViewHolder(view)
    }

    override fun onBindViewHolder(holder: InformationViewHolder, position: Int) {
        holder.bind(informationData[position], position)
    }

    override fun getItemCount(): Int {
        return informationData.size
    }

    fun itemSwiped(position: Int) {
        mRecentlyDeletedItem = informationData[position]
        mRecentlyDeletedItemPosition = position

        informationData.removeAt(position)
        notifyItemRemoved(position)
    }

    fun deleteItem() {
        mRecentlyDeletedItem?.path?.let {
            deleteDatabaseItem(it)
        }
    }

    fun undoDelete() {
        mRecentlyDeletedItem?.let { recentlyDeletedItem ->
            informationData.add(
                mRecentlyDeletedItemPosition,
                recentlyDeletedItem
            )

            notifyItemInserted(mRecentlyDeletedItemPosition)

            if (mRecentlyDeletedItemPosition == 0 ||
                mRecentlyDeletedItemPosition == informationData.size - 1)
            {
                scrollToPosition(mRecentlyDeletedItemPosition)
            }
        }
    }
}