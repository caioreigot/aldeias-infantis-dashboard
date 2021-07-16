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
    val informationType: InformationType,
    val subInformationData: MutableList<Information>,
    val context: Context
) : RecyclerView.Adapter<SubInformationAdapter.InformationViewHolder>() {

    @SuppressLint("ResourceType")
    inner class InformationViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(info: Information, position: Int) {
            with (itemView) {
                when (informationType) {
                    InformationType.TEXT -> {
                        val header: TextView = findViewById(R.id.information_header)
                        val content: TextView = findViewById(R.id.information_content)

                        header.text = info.header
                        content.text = info.content
                    }

                    InformationType.VALUE -> {
                        val header: TextView = findViewById(R.id.information_header)
                        val value: TextView = findViewById(R.id.information_value)
                        val date: TextView = findViewById(R.id.information_date)

                        header.text = info.header
                        value.text = info.value.toString()
                        date.text = info.competence
                    }

                    InformationType.PERCENTAGE -> {
                        val itemTopic: TextView = findViewById(R.id.item_indicator)
                        val itemPercentage: TextView = findViewById(R.id.item_percentage)

                        itemTopic.text = info.header

                        // Leave positive values blue (by default it is red)
                        if (info.percentage[0] != '-')
                            itemPercentage.setTextColor(Color.parseColor(context.getString(R.color.primaryBlue)))

                        itemPercentage.text = info.percentage

                        // If it is the last item, then leave the edges rounded
                        if (position == subInformationData.size - 1) {
                            val leftItemPart = findViewById<LinearLayout>(R.id.left_item)
                            val rightItemPart = findViewById<LinearLayout>(R.id.right_item)

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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InformationViewHolder {
        val layoutToInflate = when (informationType) {
            InformationType.TEXT -> R.layout.item_text
            InformationType.VALUE -> R.layout.item_value
            InformationType.PERCENTAGE -> R.layout.item_percentage_unique
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