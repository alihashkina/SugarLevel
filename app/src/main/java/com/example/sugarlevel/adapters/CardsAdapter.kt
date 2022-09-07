package com.example.sugarlevel.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sugarlevel.R
import com.example.sugarlevel.databinding.CardStatisticsBinding

class CardsAdapter (var cards1: MutableList<String>, var cards2: MutableList<String>, var cards3: MutableList<String>, var cards4: MutableList<String>, var cards5: MutableList<String>, var cards6: MutableList<String> ): RecyclerView.Adapter<CardsAdapter.CardsViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CardsAdapter.CardsViewHolder {
        return CardsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_statistics,parent,false))
    }

    override fun onBindViewHolder(holder: CardsAdapter.CardsViewHolder, position: Int) {
        holder.bind(cards1.get(position), cards2.get(position), cards3.get(position), cards4.get(position), cards5.get(position), cards6.get(position))
    }

    override fun getItemCount(): Int = cards1.size

    inner class CardsViewHolder(view: View): RecyclerView.ViewHolder(view){
        var bindingCardsAdapter = CardStatisticsBinding.bind(view)

        fun bind(item: String, item2: String, item3: String, item4: String, item5: String, item6: String){
            bindingCardsAdapter.cardDate.text = item
            bindingCardsAdapter.cardTime.text = item2
            bindingCardsAdapter.cardHealthy.text = item3
            bindingCardsAdapter.cardUnhealthy.text = item4
            bindingCardsAdapter.cardSymptoms.text = item5
            bindingCardsAdapter.cardCare.text = item6
        }

    }

}