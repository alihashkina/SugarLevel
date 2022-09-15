package com.example.sugarlevel.viewModel

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.children
import androidx.core.view.marginBottom
import androidx.lifecycle.ViewModel
import com.example.sugarlevel.R
import com.example.sugarlevel.adapters.dataClass.Card
import com.example.sugarlevel.db.MyDBHelper
import com.example.sugarlevel.fragment.GeneralPage
import com.example.sugarlevel.fragment.GeneralPage.Companion.arrayDateGraph
import com.example.sugarlevel.fragment.GeneralPage.Companion.arraySugarGraph
import com.example.sugarlevel.fragment.GeneralPage.Companion.bindingGeneralPage
import com.example.sugarlevel.fragment.GeneralPage.Companion.chipsCDB
import com.example.sugarlevel.fragment.GeneralPage.Companion.chipsCareCheck
import com.example.sugarlevel.fragment.GeneralPage.Companion.chipsHDB
import com.example.sugarlevel.fragment.GeneralPage.Companion.chipsHealthyCheck
import com.example.sugarlevel.fragment.GeneralPage.Companion.chipsSDB
import com.example.sugarlevel.fragment.GeneralPage.Companion.chipsSymptomsCheck
import com.example.sugarlevel.fragment.GeneralPage.Companion.chipsUhDB
import com.example.sugarlevel.fragment.GeneralPage.Companion.chipsUnHealthyCheck
import com.example.sugarlevel.fragment.GeneralPage.Companion.dateDB
import com.example.sugarlevel.fragment.GeneralPage.Companion.sugarDB
import com.example.sugarlevel.fragment.Statistics
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import im.dacer.androidcharts.LineView
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
        var chipsSP = mutableListOf<Chip>()
    }

    var chipsGroup = ""
    var colors = intArrayOf()

    fun getDateTimeCalendar(txtRecord: TextView){
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH)
        day = calendar.get(Calendar.DAY_OF_MONTH)
        hour = calendar.get(Calendar.HOUR)
        minute = calendar.get(Calendar.MINUTE)
        txtRecord.text = "Record $day.${month + 1}.$year $hour:$minute"
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun graph(graph: LineView, context: Context, scrollGraph: HorizontalScrollView, txtOnbord: LinearLayout, view:View){
        var helper = MyDBHelper(context!!)
        var db = helper.readableDatabase
        var rs = db.rawQuery("SELECT DATE, SUGAR, CHIPSHEALTHY, CHIPSUNHEALTHY, CHIPSSYMPTOMS, CHIPSCARE, DAYS, MONTH, YEARS, HOURS, MINUTE FROM USERS ORDER BY YEARS, MONTH, DAYS, HOURS, MINUTE ASC", null)
        arrayDateGraph = arrayListOf()
        arraySugarGraph = arrayListOf()

        while (rs.moveToNext()) {
            dateDB = rs.getString(0)
            sugarDB = rs.getString(1).toFloat()
            chipsHDB = rs.getString(2).replace(",", " | ")
            chipsUhDB = rs.getString(3).replace(",", " | ")
            chipsSDB = rs.getString(4).replace(",", " | ")
            chipsCDB = rs.getString(5).replace(",", " | ")
            arrayDateGraph.add(dateDB)
            arraySugarGraph.add(sugarDB)
            //saveChips(view)
            cards = Card(dateDB, chipsHDB, chipsUhDB, chipsSDB, chipsCDB, sugarDB.toString())
            Statistics.adapter.addCard(cards)
        }


        if(dateDB != "") {
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
    }

    fun chipsColorHealthy(chipGroupHealthy: ChipGroup, view: View){
        chipsGroup = "Healthy"
        handleSelection(view)
        chipGroupHealthy.children.forEach {
            val chip = it as Chip
            chip.chipBackgroundColor = colorStates()
            (it as Chip).setOnCheckedChangeListener { buttonView, isChecked ->
                handleSelection(view)
            }
        }
    }

    fun chipsColorUnhealthy(chipGroupUnhealthy: ChipGroup, view: View){
        chipsGroup = "Unhealthy"
        handleSelection(view)
        chipGroupUnhealthy.children.forEach {
            val chip = it as Chip
            chip.chipBackgroundColor = colorStates()
            (it as Chip).setOnCheckedChangeListener { buttonView, isChecked ->
                handleSelection(view)
            }
        }
    }

    fun chipsColorSymptoms(chipGroupSymptoms: ChipGroup, view: View){
        chipsGroup = "Symptoms"
        handleSelection(view)
        chipGroupSymptoms.children.forEach {
            val chip = it as Chip
            chip.chipBackgroundColor = colorStates()
            (it as Chip).setOnCheckedChangeListener { buttonView, isChecked ->
                handleSelection(view)
            }
        }
    }

    fun chipsColorCare(chipGroupCare: ChipGroup, view: View){
        chipsGroup = "Care"
        handleSelection(view)
        chipGroupCare.children.forEach {
            val chip = it as Chip
            chip.chipBackgroundColor = colorStates()
            (it as Chip).setOnCheckedChangeListener { buttonView, isChecked ->
                handleSelection(view)
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

     fun handleSelection(view: View){
         bindingGeneralPage.chipGroupHealthy.checkedChipIds.forEach{
            val chip = view?.findViewById<Chip>(it)
            chipsHealthyCheck.add("${chip?.text}")
             chip.isChecked = true
        }
        bindingGeneralPage.chipGroupUnhealthy.checkedChipIds.forEach{
            val chip = view?.findViewById<Chip>(it)
            chipsUnHealthyCheck.add("${chip?.text}")
            chip.isChecked = true
        }
        bindingGeneralPage.chipGroupSymptoms.checkedChipIds.forEach{
            val chip = view?.findViewById<Chip>(it)
            chipsSymptomsCheck.add("${chip?.text}")
            chip.isChecked = true

        }
        bindingGeneralPage.chipGroupCare.checkedChipIds.forEach{
            val chip = view?.findViewById<Chip>(it)
            chipsCareCheck.add("${chip?.text}")
            chip.isChecked = true
        }
    }

}