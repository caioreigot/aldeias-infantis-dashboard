package br.org.aldeiasinfantis.dashboard.ui.information

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import br.org.aldeiasinfantis.dashboard.R
import br.org.aldeiasinfantis.dashboard.data.model.Information
import br.org.aldeiasinfantis.dashboard.data.model.InformationType

class SubInformationAdapter(
    val subInformationData: MutableList<Information>,
    val informationType: InformationType,
    val context: Context
) : RecyclerView.Adapter<SubInformationAdapter.InformationViewHolder>() {

    @SuppressLint("ResourceType")
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
                    val itemTopic: TextView = itemView.findViewById(R.id.item_topic)
                    val itemPercentage: TextView = itemView.findViewById(R.id.item_percentage)

                    itemTopic.text = info.header

                    // Deixar os valores positivos em azul (por padrão é vermelho)
                    if (info.percentage[0] != '-')
                        itemPercentage.setTextColor(Color.parseColor(context.getString(R.color.primaryBlue)))

                    itemPercentage.text = info.percentage

                    // Se for o ultimo item, deixar as bordas arredondadas
                    if (position == subInformationData.size - 1) {
                        val leftItemPart = itemView.findViewById<LinearLayout>(R.id.left_item)
                        val rightItemPart = itemView.findViewById<LinearLayout>(R.id.right_item)

                        leftItemPart.background = ContextCompat.getDrawable(
                            context,
                            R.drawable.bottom_left_rounded
                        )

                        rightItemPart.background = ContextCompat.getDrawable(
                            context,
                            R.drawable.bottom_right_rounded
                        )
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InformationViewHolder {
        val layoutToInflate = when (informationType) {
            InformationType.TEXT -> R.layout.text_item
            InformationType.VALUE -> R.layout.value_item
            InformationType.PERCENTAGE -> R.layout.percentage_unique_item
        }

        val view = LayoutInflater.from(parent.context).inflate(layoutToInflate, parent, false)
        return InformationViewHolder(view)
    }

    override fun onBindViewHolder(holder: InformationViewHolder, position: Int) {
        holder.bind(subInformationData[position], position)
    }

    override fun getItemCount(): Int {
        return subInformationData.size
    }

}