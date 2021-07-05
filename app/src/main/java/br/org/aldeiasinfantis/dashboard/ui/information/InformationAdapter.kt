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
    val subInformationParent: MutableList<MutableList<Information>>,
    val informationType: InformationType,
    val context: Context
) : RecyclerView.Adapter<InformationAdapter.InformationViewHolder>() {

    inner class InformationViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(info: Information, position: Int) {
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

                    // Passando o adapter para RecyclerView de cada item
                    val subRecyclerView = itemView.findViewById<RecyclerView>(R.id.percentage_recycle_view)

                    subRecyclerView.adapter = SubInformationAdapter(
                        subInformationParent[position],
                        InformationType.PERCENTAGE,
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

}