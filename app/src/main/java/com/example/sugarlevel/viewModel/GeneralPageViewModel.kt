package com.example.sugarlevel.viewModel

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.view.children
import androidx.core.view.marginBottom
import androidx.lifecycle.ViewModel
import com.example.sugarlevel.R
import com.example.sugarlevel.TinyDB
import com.example.sugarlevel.adapters.CardAdapter.Companion.cardList
import com.example.sugarlevel.adapters.CardAdapter.Companion.id
import com.example.sugarlevel.adapters.dataClass.Card
import com.example.sugarlevel.db.MyDBHelper
import com.example.sugarlevel.fragment.GeneralPage.Companion.arrayDateGraph
import com.example.sugarlevel.fragment.GeneralPage.Companion.arraySugarGraph
import com.example.sugarlevel.fragment.GeneralPage.Companion.chipsCDB
import com.example.sugarlevel.fragment.GeneralPage.Companion.chipsCareCheck
import com.example.sugarlevel.fragment.GeneralPage.Companion.chipsHDB
import com.example.sugarlevel.fragment.GeneralPage.Companion.chipsHealthyCheck
import com.example.sugarlevel.fragment.GeneralPage.Companion.chipsODB
import com.example.sugarlevel.fragment.GeneralPage.Companion.chipsOtherCheck
import com.example.sugarlevel.fragment.GeneralPage.Companion.chipsSDB
import com.example.sugarlevel.fragment.GeneralPage.Companion.chipsSymptomsCheck
import com.example.sugarlevel.fragment.GeneralPage.Companion.chipsUhDB
import com.example.sugarlevel.fragment.GeneralPage.Companion.chipsUnHealthyCheck
import com.example.sugarlevel.fragment.GeneralPage.Companion.dateDB
import com.example.sugarlevel.fragment.GeneralPage.Companion.sugarDB
import com.example.sugarlevel.fragment.GeneralPage.Companion.sugarDBml
import com.example.sugarlevel.fragment.Statistics
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import im.dacer.androidcharts.LineView
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class GeneralPageViewModel : ViewModel() {

    companion object{
        val calendar = Calendar.getInstance()
        var year = 0
        var month = 0
        var day = 0
        var hour = 0
        var minute = 0
        lateinit var cards: Card
        lateinit var helper: MyDBHelper
        var deleteCard = false
    }

    var chipsGroup = ""
    var colors = intArrayOf()

    fun addChip(text:String, context: Context, chipGroupOtherTags: ChipGroup, tinyDB: TinyDB){
        chipsOtherCheck = tinyDB.getListString("OtherChips")
        var chip = Chip(context)
        chip.text = text
        chip.isCloseIconVisible = true

        chip.setOnCloseIconClickListener{
            chipsOtherCheck.remove(chip?.text)
            chipGroupOtherTags.removeView(chip)
            tinyDB.putListString("OtherChips", chipsOtherCheck)
        }

        chipsOtherCheck.add("${chip?.text}")
        chip.isChecked = true
        chipGroupOtherTags.addView(chip)
        tinyDB.putListString("OtherChips", chipsOtherCheck)
        chip.setChipBackgroundColorResource(R.color.md_purple_100)
    }

    fun saveChips(context: Context, chipGroupOtherTags: ChipGroup, tinyDB: TinyDB){
        if(chipsOtherCheck.isNullOrEmpty() && !tinyDB.getListString("OtherChips").isNullOrEmpty()){

            for(i in tinyDB.getListString("OtherChips").indices){
                var chip = Chip(context)
                chip.text = tinyDB.getListString("OtherChips")[i]
                chip.isCloseIconVisible = true
                chip.isChecked = true
                chipGroupOtherTags.addView(chip)
                chip.setChipBackgroundColorResource(R.color.md_purple_100)

                chip.setOnCloseIconClickListener{
                    chipsOtherCheck.remove(chip?.text)
                    chipGroupOtherTags.removeView(chip)
                    tinyDB.putListString("OtherChips", chipsOtherCheck)
                }

            }
        }
    }

    fun getDateTimeCalendar(txtRecord: TextView, context: Context){
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH)
        day = calendar.get(Calendar.DAY_OF_MONTH)
        hour = calendar.get(Calendar.HOUR)
        minute = calendar.get(Calendar.MINUTE)
        var sdf = SimpleDateFormat("dd.MM.yyyy HH:mm")
        var currentDate = sdf.format(Date())
        txtRecord.text = "${context.getString(R.string.record)} $currentDate"
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun graph(graph: LineView, context: Context, scrollGraph: HorizontalScrollView, txtOnbord: LinearLayout){
           helper = MyDBHelper(context!!)
           var db = helper.readableDatabase
           var rs = db.rawQuery(
               "SELECT DATE, SUGAR, CHIPSHEALTHY, CHIPSUNHEALTHY, CHIPSSYMPTOMS, CHIPSCARE, CHIPSOTHER, DAYS, MONTH, YEARS, HOURS, MINUTE FROM USERS ORDER BY YEARS, MONTH, DAYS, HOURS, MINUTE ASC",
               null
           )

           arrayDateGraph = arrayListOf()
           arraySugarGraph = arrayListOf()

           while (rs != null && rs.getCount() > 0 && rs.moveToNext()) {
               dateDB = rs.getString(0)
               sugarDB = rs.getString(1).toFloat()
               chipsHDB = rs.getString(2).replace(",", " | ")
               chipsUhDB = rs.getString(3).replace(",", " | ")
               chipsSDB = rs.getString(4).replace(",", " | ")
               chipsCDB = rs.getString(5).replace(",", " | ")
               chipsODB = rs.getString(6).replace(",", " | ")
               id = rs.getString(7).toInt()

               if (sugarDB < 40) {
                   sugarDBml = sugarDB * 18
               } else {
                   sugarDBml = sugarDB / 18
               }

               arrayDateGraph.add(dateDB)
               arraySugarGraph.add(sugarDB)
               if (!deleteCard) {
                   cards = Card(
                       dateDB,
                       chipsHDB,
                       chipsUhDB,
                       chipsSDB,
                       chipsCDB,
                       sugarDB.toString(),
                       "(${sugarDBml.toBigDecimal().setScale(1, RoundingMode.UP)})",
                       chipsODB,
                       id
                   )
                   Statistics.adapter.addCard(cards)
               }
           }

           if (dateDB != "") {
               scrollGraph.visibility = View.VISIBLE
               txtOnbord.visibility = View.GONE
               var sugarLists = ArrayList<ArrayList<Float>>()
               sugarLists = arrayListOf(arraySugarGraph as ArrayList<Float>)
               graph.setDrawDotLine(false) //optional
               graph.getResources().getColor(R.color.md_white_1000)
               graph.setShowPopup(LineView.SHOW_POPUPS_All) //optional
               graph.setBottomTextList(arrayDateGraph as ArrayList<String>?)
               graph.setColorArray(intArrayOf(Color.RED))
               graph.marginBottom
               graph.paddingBottom
               graph.setFloatDataList(sugarLists)
           }
       else{
            scrollGraph.visibility = View.GONE
            txtOnbord.visibility = View.VISIBLE
        }
    }

    fun chipsColor(view: View, tinyDB: TinyDB, chipGroupHealthy: ChipGroup, chipGroupUnhealthy: ChipGroup, chipGroupSymptoms: ChipGroup, chipGroupCare: ChipGroup){
        handleSelection(view, tinyDB, chipGroupHealthy, chipGroupUnhealthy, chipGroupSymptoms, chipGroupCare)

        chipGroupHealthy.children.forEach {
            chipsGroup = "Healthy"
            val chip = it as Chip
            chip.chipBackgroundColor = colorStates()
            (it as Chip).setOnCheckedChangeListener { buttonView, isChecked ->
                handleSelection(view, tinyDB, chipGroupHealthy, chipGroupUnhealthy, chipGroupSymptoms, chipGroupCare)
            }
        }

        chipGroupUnhealthy.children.forEach {
            chipsGroup = "Unhealthy"
            val chip = it as Chip
            chip.chipBackgroundColor = colorStates()
            (it as Chip).setOnCheckedChangeListener { buttonView, isChecked ->
                handleSelection(view, tinyDB, chipGroupHealthy, chipGroupUnhealthy, chipGroupSymptoms, chipGroupCare)
            }
        }

        chipGroupSymptoms.children.forEach {
            chipsGroup = "Symptoms"
            val chip = it as Chip
            chip.chipBackgroundColor = colorStates()
            (it as Chip).setOnCheckedChangeListener { buttonView, isChecked ->
                handleSelection(view, tinyDB, chipGroupHealthy, chipGroupUnhealthy, chipGroupSymptoms, chipGroupCare)
            }
        }

        chipGroupCare.children.forEach {
            chipsGroup = "Care"
            val chip = it as Chip
            chip.chipBackgroundColor = colorStates()
            (it as Chip).setOnCheckedChangeListener { buttonView, isChecked ->
                handleSelection(view, tinyDB, chipGroupHealthy, chipGroupUnhealthy, chipGroupSymptoms, chipGroupCare)
            }
        }
    }

    fun colorStates(): ColorStateList {
        val states = arrayOf(
            intArrayOf(android.R.attr.state_checked),
            intArrayOf(-android.R.attr.state_checked)
        )

        when (chipsGroup){
            "Healthy" -> colors = intArrayOf(Color.parseColor("#69F0AE"), Color.parseColor("#E0E0E0"))
            "Unhealthy" -> colors = intArrayOf(Color.parseColor("#FF8A80"), Color.parseColor("#E0E0E0"))
            "Symptoms" -> colors = intArrayOf(Color.parseColor("#81D4fA"),Color.parseColor("#E0E0E0"))
            "Care" -> colors = intArrayOf(Color.parseColor("#FFF590"),Color.parseColor("#E0E0E0"))
        }

        return ColorStateList(states, colors)
    }

    fun handleSelection(view: View, tinyDB: TinyDB, chipGroupHealthy: ChipGroup, chipGroupUnhealthy: ChipGroup, chipGroupSymptoms: ChipGroup, chipGroupCare: ChipGroup){
        chipGroupHealthy.checkedChipIds.forEach{
            val chip = view?.findViewById<Chip>(it)
            chipsHealthyCheck.add("${chip?.text}")
            chip.isChecked = true
        }

        chipGroupUnhealthy.checkedChipIds.forEach{
            val chip = view?.findViewById<Chip>(it)
            chipsUnHealthyCheck.add("${chip?.text}")
            chip.isChecked = true
        }

        chipGroupSymptoms.checkedChipIds.forEach{
            val chip = view?.findViewById<Chip>(it)
            chipsSymptomsCheck.add("${chip?.text}")
            chip.isChecked = true

        }

        chipGroupCare.checkedChipIds.forEach{
            val chip = view?.findViewById<Chip>(it)
            chipsCareCheck.add("${chip?.text}")
            chip.isChecked = true
        }
    }

    fun btnPlus(txtSugar: EditText){
        if(txtSugar.text.toString() == ""){
            txtSugar.setText("0.0")
            txtSugar.setSelection(txtSugar.length())
        }

        if (txtSugar.text.toString().toDouble() < 700.0) {
            txtSugar.setText(
                "${((txtSugar.text.toString().toDouble() * 10) + 1) / 10}"
            )
            txtSugar.setSelection(txtSugar.length())
        } else {
            txtSugar.setSelection(txtSugar.length())
            txtSugar.setText("700.0")
            txtSugar.setSelection(txtSugar.length())
        }
    }

    fun btnMinus(txtSugar: EditText){
        if(txtSugar.text.toString() == ""){
            txtSugar.setText("0.0")
            txtSugar.setSelection(txtSugar.length())
        }

        if(txtSugar.text.toString().toDouble() != 0.0) {
            txtSugar.setText(
                "${((txtSugar.text.toString().toDouble() * 10) - 1)/10}"
            )
            txtSugar.setSelection(txtSugar.length())
        }
        else{
            txtSugar.setSelection(txtSugar.length())
            txtSugar.setText("0.0")
            txtSugar.setSelection(txtSugar.length())
        }
    }

    fun btnSaveText(btnSave: ExtendedFloatingActionButton, txtSugar: EditText, context: Context){
        btnSave.text = "${txtSugar.text} ${context.getString(R.string.save)}"
    }

}