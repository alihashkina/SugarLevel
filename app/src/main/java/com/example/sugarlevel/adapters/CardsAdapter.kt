package com.example.sugarlevel.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sugarlevel.R
import com.example.sugarlevel.databinding.CardStatisticsBinding

class CardsAdapter (var cards1: MutableList<String>, var cards2: MutableList<String>, var cards3: MutableList<String>, var cards4: MutableList<String>, var cards5: MutableList<String>, var cards6: MutableList<String>, var cards7: MutableList<String> ): RecyclerView.Adapter<CardsAdapter.CardsViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CardsAdapter.CardsViewHolder {
        return CardsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_statistics,parent,false))
    }

    override fun onBindViewHolder(holder: CardsAdapter.CardsViewHolder, position: Int) {
        holder.bind(cards1.get(position), cards2.get(position), cards3.get(position), cards4.get(position), cards5.get(position), cards6.get(position), cards7.get(position))
    }

    override fun getItemCount(): Int = cards1.size

    inner class CardsViewHolder(view: View): RecyclerView.ViewHolder(view){
        var bindingCardsAdapter = CardStatisticsBinding.bind(view)

        fun bind(item: String, item2: String, item3: String, item4: String, item5: String, item6: String, item7: String){
            var value = bindingCardsAdapter.arcChartView.getSectionValue(0)
            bindingCardsAdapter.arcChartView.setFilldeColor(0, Color.GREEN)
            bindingCardsAdapter.arcChartView.setFilldeColor(1, Color.RED)
            bindingCardsAdapter.arcChartView.setFilldeColor(2, Color.BLUE)
            bindingCardsAdapter.arcChartView.setFilldeColor(3, Color.YELLOW)
            bindingCardsAdapter.arcChartView.setUnFilldeColor(0,Color.LTGRAY)
            bindingCardsAdapter.arcChartView.setUnFilldeColor(1,Color.LTGRAY)
            bindingCardsAdapter.arcChartView.setUnFilldeColor(2,Color.LTGRAY)
            bindingCardsAdapter.arcChartView.setUnFilldeColor(3,Color.LTGRAY)
            bindingCardsAdapter.arcChartView.setSectionValue(0, countOccurrences(item3, '|')+1)
            bindingCardsAdapter.arcChartView.setSectionValue(1, countOccurrences(item4, '|')+1)
            bindingCardsAdapter.arcChartView.setSectionValue(2, countOccurrences(item5, '|')+1)
            bindingCardsAdapter.arcChartView.setSectionValue(3, countOccurrences(item6, '|')+1)
            bindingCardsAdapter.cardDate.text = item
            bindingCardsAdapter.cardTime.text = item2
            bindingCardsAdapter.cardHealthy.text = item3
            bindingCardsAdapter.cardUnhealthy.text = item4
            bindingCardsAdapter.cardSymptoms.text = item5
            bindingCardsAdapter.cardCare.text = item6
            bindingCardsAdapter.cardSugar.text = item7
        }

        fun countOccurrences(s: String, ch: Char): Int {
            return s.length - s.replace(ch.toString(), "").length
        }
    }

}