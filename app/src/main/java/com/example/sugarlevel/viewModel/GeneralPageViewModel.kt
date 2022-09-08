package com.example.sugarlevel.viewModel

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.children
import androidx.core.view.marginBottom
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.GridLayoutManager
import com.example.sugarlevel.R
import com.example.sugarlevel.adapters.CardsAdapter
import com.example.sugarlevel.db.MyDBHelper
import com.example.sugarlevel.fragment.GeneralPage
import com.example.sugarlevel.fragment.GeneralPage.Companion.arrayDateGraph
import com.example.sugarlevel.fragment.GeneralPage.Companion.arraySugarGraph
import com.example.sugarlevel.fragment.GeneralPage.Companion.bindingGeneralPage
import com.example.sugarlevel.fragment.GeneralPage.Companion.chipsSymptomsCheck
import com.example.sugarlevel.fragment.GeneralPage.Companion.chipsSymptomsCheckDistinct
import com.example.sugarlevel.fragment.GeneralPage.Companion.dateDB
import com.example.sugarlevel.fragment.GeneralPage.Companion.sugarDB
import com.example.sugarlevel.fragment.Statistics
import com.example.sugarlevel.fragment.Statistics.Companion.arrayCareS
import com.example.sugarlevel.fragment.Statistics.Companion.arrayDateStaistics
import com.example.sugarlevel.fragment.Statistics.Companion.arrayHealthyS
import com.example.sugarlevel.fragment.Statistics.Companion.arraySugarS
import com.example.sugarlevel.fragment.Statistics.Companion.arraySymptomsS
import com.example.sugarlevel.fragment.Statistics.Companion.arrayTimeStaistics
import com.example.sugarlevel.fragment.Statistics.Companion.arrayUnHealthyS
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.slider.LabelFormatter
import im.dacer.androidcharts.LineView
import im.dacer.androidcharts.MyUtils
import java.math.RoundingMode
import java.security.AccessController.getContext
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class GeneralPageViewModel : ViewModel() {

    companion object{
        val calendar = Calendar.getInstance()
        var year = 0
        var month = 0
        var day = 0
        var hour = 0
        var minute = 0
    }

    fun chipsCheck(chipsGeneral: ChipGroup, view: View){
        handleSelection(view)
        chipsGeneral.children.forEach {
            val chip = it as Chip
            (it as Chip).setOnCheckedChangeListener { buttonView, isChecked ->
                handleSelection(view)
            }
        }
    }

     private fun handleSelection(view: View) {
         bindingGeneralPage.chipsGeneral.checkedChipIds.forEach {
             val chip = view?.findViewById<Chip>(it)
            chipsSymptomsCheck.add("${chip?.text}")
         }
     }

    fun getDateTimeCalendar(txtRecord: TextView){
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH)
        day = calendar.get(Calendar.DAY_OF_MONTH)
        hour = calendar.get(Calendar.HOUR)
        minute = calendar.get(Calendar.MINUTE)
        txtRecord.text = "Record $day.${month + 1}.$year $hour:$minute"
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun graph(graph: LineView, context: Context, scrollGraph: HorizontalScrollView, txtOnbord: LinearLayout){
        var helper = MyDBHelper(context!!)
        var db = helper.readableDatabase
        var rs = db.rawQuery("SELECT DATE, SUGAR, CHIPSHEALTHY, CHIPSUNHEALTHY, CHIPSSYMPTOMS, CHIPSCARE, DAYS, MONTH, YEARS, HOURS, MINUTE FROM USERS ORDER BY YEARS, MONTH, DAYS, HOURS, MINUTE ASC", null)
        arrayDateGraph = arrayListOf()
        arraySugarGraph = arrayListOf()


        while (rs.moveToNext()) {
            dateDB = rs.getString(0)
            sugarDB = rs.getString(1).toFloat()
            arrayDateGraph.add(dateDB)
            arraySugarGraph.add(sugarDB)
            arrayDateStaistics.add(dateDB.split(" ")?.get(0))
            arrayTimeStaistics.add(dateDB.split(" ")?.get(1))
            arrayHealthyS.add(rs.getString(2).replace("[", "").replace("]", "").replace(",", " | "))
            arrayUnHealthyS.add(rs.getString(3).replace("[", "").replace("]", "").replace(",", " | "))
            arraySymptomsS.add(rs.getString(4).replace("[", "").replace("]", "").replace(",", " | "))
            arrayCareS.add(rs.getString(5).replace("[", "").replace("]", "").replace(",", " | "))
            arraySugarS.add(rs.getString(1))
        }

        if(dateDB != "") {
            Statistics.bindingStatistics.recyclerStatistics.adapter = CardsAdapter(arrayDateStaistics, arrayTimeStaistics, arrayHealthyS, arrayUnHealthyS, arraySymptomsS, arrayCareS, arraySugarS)

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
}