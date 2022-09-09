package com.example.sugarlevel.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sugarlevel.R
import com.example.sugarlevel.databinding.CardStatisticsBinding

class CardAdapter: RecyclerView.Adapter<CardAdapter.CardHolder>() {

    val cardList = ArrayList<Card>()

    class CardHolder(item: View): RecyclerView.ViewHolder(item){
        val binding = CardStatisticsBinding.bind(item)
        fun bind(card: Card) = with(binding){
            cardDate.text = card.cardDate
            cardTime.text = card.cardTime
            cardHealthy.text = card.cardHealthy
            cardUnhealthy.text = card.cardUnhealthy
            cardSymptoms.text = card.cardSymptoms
            cardCare.text = card.cardCare
            cardSugar.text = card.cardSugar
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.card_statistics, parent, false)
    return CardHolder(view)
    }

    override fun onBindViewHolder(holder: CardHolder, position: Int) {
holder.bind(cardList[position])
    }

    override fun getItemCount(): Int {
return cardList.size
    }

    fun addCard(card: Card){
        cardList.add(card)
        notifyDataSetChanged()
    }
}