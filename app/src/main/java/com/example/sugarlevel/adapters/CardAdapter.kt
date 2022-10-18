package com.example.sugarlevel.adapters

import android.R.id
import android.annotation.SuppressLint
import android.app.PendingIntent.getActivity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.core.view.marginBottom
import androidx.recyclerview.widget.RecyclerView
import com.example.sugarlevel.R
import com.example.sugarlevel.adapters.dataClass.Card
import com.example.sugarlevel.databinding.CardStatisticsBinding
import com.example.sugarlevel.db.MyDBHelper
import com.example.sugarlevel.fragment.GeneralPage
import com.example.sugarlevel.fragment.GeneralPage.Companion.bindingGeneralPage
import com.example.sugarlevel.fragment.Statistics
import com.example.sugarlevel.viewModel.GeneralPageViewModel
import com.example.sugarlevel.viewModel.GeneralPageViewModel.Companion.helper
import im.dacer.androidcharts.LineView

//адаптер для статистики
class CardAdapter: RecyclerView.Adapter<CardAdapter.CardHolder>() {

    companion object{
    val cardList = ArrayList<Card>()
         var id: Int = 0
    }

    class CardHolder(item: View): RecyclerView.ViewHolder(item){

        var bindingCardAdapter = CardStatisticsBinding.bind(item)

        fun bind(card: Card, index: Int) = with(bindingCardAdapter){
            cardDate.text = card.cardDate
            cardHealthy.text = card.cardHealthy
            cardUnhealthy.text = card.cardUnhealthy
            cardSymptoms.text = card.cardSymptoms
            cardCare.text = card.cardCare
            cardSugar.text = card.cardSugar
            cardSugarml.text = card.cardSugarml
            cardOther.text = card.cardOther

            //кнопка поделиться/удалить
            cardMore.setOnClickListener {
                val popupMenu = PopupMenu(it.context, cardMore)
                var sugar = it.context.getString(R.string.sugar)
                var date = it.context.getString(R.string.date)
                var healthy = it.context.getString(R.string.healthy)
                var txtUnhealthy = it.context.getString(R.string.txtUnhealthy)
                var txtSymptoms = it.context.getString(R.string.txtSymptoms)
                var txtCare = it.context.getString(R.string.txtCare)
                var txtOtherTags = it.context.getString(R.string.txtOtherTags)

                popupMenu.menuInflater.inflate(R.menu.popup_menu,popupMenu.menu)
                popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->

                    when(item.itemId) {
                        R.id.share ->
                        {
                            val intent = Intent()
                            intent.action = Intent.ACTION_SEND
                            intent.putExtra(Intent.EXTRA_TEXT, "${sugar}: ${cardSugar.text} ${cardSugarml.text} \n$date: ${cardDate.text} \n$healthy: ${cardHealthy.text} \n$txtUnhealthy: ${cardUnhealthy.text} \n$txtSymptoms: ${cardSymptoms.text} \n$txtCare: ${cardCare.text} \n$txtOtherTags: ${cardOther.text}")
                            intent.type = "text/plain"
                            it.context.startActivity(Intent.createChooser(intent, "Share To:"))
                        }
                        R.id.delete ->
                        {
                            id = index+1
                            Statistics.adapter.deleteCard(index)
                        }
                    }
                    true
                })
                popupMenu.show()
            }
        }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_statistics, parent, false)
    return CardHolder(view)
    }

    override fun onBindViewHolder(holder: CardHolder, position: Int) {
        holder.bind(cardList[position], position)
    }

    override fun getItemCount(): Int {
        return cardList.size
    }

    //добавить карточку
    fun addCard(card: Card){
        cardList.add(card)
        notifyDataSetChanged()
    }

    //удалить карочку
    fun deleteCard(index: Int){
        GeneralPageViewModel.deleteCard = true
        var db = helper.writableDatabase
        if(cardList.size == 1){
            db.delete("USERS", null, null)
            bindingGeneralPage.scrollGraph.visibility = View.GONE
            bindingGeneralPage.txtOnbord.visibility = View.VISIBLE
        }else{
            db.delete("USERS", "USERID=${id}", null)
            bindingGeneralPage.deleteCard.callOnClick()
        }
        cardList.removeAt(index)
        notifyDataSetChanged()
    }

}