package com.example.sugarlevel.viewModel

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.ViewModel
import com.example.sugarlevel.R
import com.example.sugarlevel.db.MyDBHelper
import com.example.sugarlevel.fragment.GeneralPage
import com.example.sugarlevel.fragment.GeneralPage.Companion.arrayDateGraph
import com.example.sugarlevel.fragment.GeneralPage.Companion.arraySugarGraph
import com.example.sugarlevel.fragment.GeneralPage.Companion.chipsCheckTxt
import com.example.sugarlevel.fragment.GeneralPage.Companion.dateDB
import com.google.android.material.chip.Chip
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

    fun chipsCheck(chip1: Chip, chip2: Chip, chip3: Chip, chip4: Chip, chip5: Chip){
        if (chip1.isChecked) {
            if(chipsCheckTxt.contains(GeneralPage.bindingGeneralPage.chip1.text.toString())){
            }else{
                chipsCheckTxt = chipsCheckTxt + "${GeneralPage.bindingGeneralPage.chip1.text.toString() + " "}"
            }
        }

        if (chip2.isChecked) {
            if(chipsCheckTxt.contains(GeneralPage.bindingGeneralPage.chip2.text.toString())){
            }else {
                chipsCheckTxt = chipsCheckTxt + "${GeneralPage.bindingGeneralPage.chip2.text.toString() + " "}"
            }
        }

        if (chip3.isChecked) {
            if(chipsCheckTxt.contains(GeneralPage.bindingGeneralPage.chip3.text.toString())){
            }else {
                chipsCheckTxt = chipsCheckTxt + "${GeneralPage.bindingGeneralPage.chip3.text.toString() + " "}"
            }
        }

        if (chip4.isChecked) {
            if(chipsCheckTxt.contains(GeneralPage.bindingGeneralPage.chip4.text.toString())){
            }else {
                chipsCheckTxt = chipsCheckTxt + "${GeneralPage.bindingGeneralPage.chip4.text.toString() + " "}"
            }
        }

        if (chip5.isChecked) {
            if(chipsCheckTxt.contains(GeneralPage.bindingGeneralPage.chip5.text.toString())){
            }else {
                chipsCheckTxt = chipsCheckTxt + "${GeneralPage.bindingGeneralPage.chip5.text.toString() + " "}"
            }
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

    fun graph(graph: LineView, context: Context){
        var helper = MyDBHelper(context!!)
        var db = helper.readableDatabase
        var rs = db.rawQuery("SELECT DATE, SUGAR, CHIPS FROM USERS", null)
        arrayDateGraph = arrayListOf()
        arraySugarGraph = arrayListOf()

        while (rs.moveToNext()) {
            dateDB = rs.getString(0)
//            var sugarDB = rs.getString(1).toBigDecimal().setScale(2, RoundingMode.HALF_UP).toDouble()
            var sugarDB = rs.getString(1).toFloat()
            rs.getString(2)
            arrayDateGraph.add(dateDB)
            arraySugarGraph.add(sugarDB)
        }

        if(dateDB != "") {
            var dataLists = ArrayList<ArrayList<Float>>()
            dataLists = arrayListOf(arraySugarGraph as ArrayList<Float>)
            graph.setDrawDotLine(false) //optional
            graph.getResources().getColor(R.color.md_white_1000)
            graph.setShowPopup(LineView.SHOW_POPUPS_All) //optional
            graph.setBottomTextList(arrayDateGraph as ArrayList<String>?)
            graph.setColorArray(intArrayOf(R.color.md_blue_700, R.color.md_blue_700, R.color.md_blue_700))
            graph.setFloatDataList(dataLists)
        }
    }
}