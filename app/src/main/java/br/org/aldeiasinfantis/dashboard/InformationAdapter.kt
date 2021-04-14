package br.org.aldeiasinfantis.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.org.aldeiasinfantis.dashboard.model.Information

class InformationAdapter(val informations: MutableList<Information>): RecyclerView.Adapter<InformationAdapter.InformationViewHolder>() {

    inner class InformationViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val header: TextView = itemView.findViewById(R.id.information_header_text)
        private val content: TextView = itemView.findViewById(R.id.information_content_text)

        fun bind(info: Information) {
            header.text = info.header
            content.text = info.content
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InformationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.information_item, parent, false)
        return InformationViewHolder(view)
    }

    override fun onBindViewHolder(holder: InformationViewHolder, position: Int) {
        holder.bind(informations[position])
    }

    override fun getItemCount(): Int {
        return informations.size
    }

}