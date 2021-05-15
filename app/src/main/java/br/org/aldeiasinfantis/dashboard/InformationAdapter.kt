package br.org.aldeiasinfantis.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.org.aldeiasinfantis.dashboard.model.Information
import br.org.aldeiasinfantis.dashboard.model.InformationType

class InformationAdapter(val informations: MutableList<Information>, val informationType: InformationType): RecyclerView.Adapter<InformationAdapter.InformationViewHolder>() {

    inner class InformationViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(info: Information) {
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
                    value.text = info.value
                    date.text = info.date
                }

                InformationType.PERCENTAGE -> {
                    val header: TextView = itemView.findViewById(R.id.information_header)

                    header.text = info.header
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InformationViewHolder {
        val layoutToInflate = when (informationType) {
            InformationType.TEXT -> R.layout.text_item
            InformationType.VALUE -> R.layout.value_item
            InformationType.PERCENTAGE -> R.layout.percentage_item
        }

        val view = LayoutInflater.from(parent.context).inflate(layoutToInflate, parent, false)
        return InformationViewHolder(view)
    }

    override fun onBindViewHolder(holder: InformationViewHolder, position: Int) {
        holder.bind(informations[position])
    }

    override fun getItemCount(): Int {
        return informations.size
    }

}