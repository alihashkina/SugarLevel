package com.example.sugarlevel.viewModel

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.view.children
import androidx.core.view.marginBottom
import androidx.lifecycle.ViewModel
import com.example.sugarlevel.R
import com.example.sugarlevel.db.MyDBHelper
import com.example.sugarlevel.fragment.GeneralPage
import com.example.sugarlevel.fragment.GeneralPage.Companion.arrayDateGraph
import com.example.sugarlevel.fragment.GeneralPage.Companion.arraySugarGraph
import com.example.sugarlevel.fragment.GeneralPage.Companion.bindingGeneralPage
import com.example.sugarlevel.fragment.GeneralPage.Companion.chipsCheckTxt
import com.example.sugarlevel.fragment.GeneralPage.Companion.dateDB
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.slider.LabelFormatter
import im.dacer.androidcharts.LineView
import im.dacer.androidcharts.MyUtils
import java.math.RoundingMode
import java.security.AccessController.getContext
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
             chipsCheckTxt.add("${chip?.text}")
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
        var rs = db.rawQuery("SELECT DATE, SUGAR, CHIPS, DAYS, MONTH, YEARS, HOURS, MINUTE FROM USERS ORDER BY YEARS, MONTH, DAYS, HOURS, MINUTE ASC", null)
        arrayDateGraph = arrayListOf()
        arraySugarGraph = arrayListOf()

        while (rs.moveToNext()) {
            dateDB = rs.getString(0)
            var sugarDB = rs.getString(1).toFloat()
            rs.getString(2)
            arrayDateGraph.add(dateDB)
            arraySugarGraph.add(sugarDB)
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
}