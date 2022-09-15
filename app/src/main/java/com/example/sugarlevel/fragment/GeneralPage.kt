package com.example.sugarlevel.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sugarlevel.R
import com.example.sugarlevel.adapters.CardAdapter.Companion.cardList
import com.example.sugarlevel.databinding.GeneralPageFragmentBinding
import com.example.sugarlevel.db.MyDBHelper
import com.example.sugarlevel.viewModel.GeneralPageViewModel
import com.example.sugarlevel.viewModel.GeneralPageViewModel.Companion.chipsSP
import com.example.sugarlevel.viewModel.GeneralPageViewModel.Companion.day
import com.example.sugarlevel.viewModel.GeneralPageViewModel.Companion.hour
import com.example.sugarlevel.viewModel.GeneralPageViewModel.Companion.minute
import com.example.sugarlevel.viewModel.GeneralPageViewModel.Companion.month
import com.example.sugarlevel.viewModel.GeneralPageViewModel.Companion.year
import com.google.android.material.chip.Chip
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList


class GeneralPage : Fragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    companion object {
        fun newInstance() = GeneralPage()
        var editSugar = ""
        var dateDB = ""
        var chipsHDB = ""
        var chipsUhDB = ""
        var chipsSDB = ""
        var chipsCDB = ""
        var sugarDB: Float = 0.0F
        var arrayDateGraph : MutableList<String> = mutableListOf()
        var arraySugarGraph : MutableList<Float> = mutableListOf()
        var chipsHealthyCheck = mutableListOf<String>()
        var chipsUnHealthyCheck = mutableListOf<String>()
        var chipsSymptomsCheck = mutableListOf<String>()
        var chipsCareCheck = mutableListOf<String>()
        lateinit var bindingGeneralPage: GeneralPageFragmentBinding
        var chipsHealthyCheckDistinct = listOf<String>()
        var chipsUnHealthyCheckDistinct = listOf<String>()
        var chipsSymptomsCheckDistinct = listOf<String>()
        var chipsCareCheckDistinct = listOf<String>()
    }

    var saveyear = 0
    var savemonth = 0
    var saveday = 0
    var savehour = 0
    var saveminute = 0

    private lateinit var viewModel: GeneralPageViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingGeneralPage= DataBindingUtil.inflate(inflater, R.layout.general_page_fragment,container,false)
        return bindingGeneralPage.root
    }


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this).get(GeneralPageViewModel::class.java)

        viewModel.chipsColorHealthy(bindingGeneralPage.chipGroupHealthy, requireView())
        viewModel.chipsColorUnhealthy(bindingGeneralPage.chipGroupUnhealthy, requireView())
        viewModel.chipsColorSymptoms(bindingGeneralPage.chipGroupSymptoms, requireView())
        viewModel.chipsColorCare(bindingGeneralPage.chipGroupCare, requireView())

        bindingGeneralPage.txtSugar.setSelection(bindingGeneralPage.txtSugar.length())
        editSugar = bindingGeneralPage.txtSugar.text.toString()

        bindingGeneralPage.scrollGraph.post {
            bindingGeneralPage.scrollGraph.fullScroll(View.FOCUS_RIGHT)
        }

        bindingGeneralPage.txtSugar.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_DONE && bindingGeneralPage.txtSugar.text.toString() != "") {
                bindingGeneralPage.btnSave.callOnClick()
                handled = true
            }
            handled
        })

        bindingGeneralPage.btnPlus.setOnClickListener {
            if(bindingGeneralPage.txtSugar.text.toString() == ""){
                bindingGeneralPage.txtSugar.setText("0.0")
                bindingGeneralPage.txtSugar.setSelection(bindingGeneralPage.txtSugar.length())
            }
            if(bindingGeneralPage.txtSugar.text.toString().toDouble() < 700.0) {
                bindingGeneralPage.txtSugar.setText(
                    "${((bindingGeneralPage.txtSugar.text.toString().toDouble() * 10) + 1)/10}"
                )
                bindingGeneralPage.txtSugar.setSelection(bindingGeneralPage.txtSugar.length())
            }
            else{
                bindingGeneralPage.txtSugar.setSelection(bindingGeneralPage.txtSugar.length())
                bindingGeneralPage.txtSugar.setText("700.0")
                bindingGeneralPage.txtSugar.setSelection(bindingGeneralPage.txtSugar.length())
            }
        }

        bindingGeneralPage.btnMinus.setOnClickListener {
            if(bindingGeneralPage.txtSugar.text.toString() == ""){
                bindingGeneralPage.txtSugar.setText("0.0")
                bindingGeneralPage.txtSugar.setSelection(bindingGeneralPage.txtSugar.length())
            }
            if(bindingGeneralPage.txtSugar.text.toString().toDouble() != 0.0) {
                bindingGeneralPage.txtSugar.setText(
                    "${((bindingGeneralPage.txtSugar.text.toString().toDouble() * 10) - 1)/10}"
                )
                bindingGeneralPage.txtSugar.setSelection(bindingGeneralPage.txtSugar.length())
            }
            else{
                bindingGeneralPage.txtSugar.setSelection(bindingGeneralPage.txtSugar.length())
                bindingGeneralPage.txtSugar.setText("0.0")
                bindingGeneralPage.txtSugar.setSelection(bindingGeneralPage.txtSugar.length())
            }
        }

        viewModel.graph(bindingGeneralPage.graph, requireContext(), bindingGeneralPage.scrollGraph, bindingGeneralPage.txtOnbord, requireView())

        bindingGeneralPage.btnSave.setOnClickListener {
            if(bindingGeneralPage.txtSugar.text.toString() != ""){

                viewModel.chipsColorHealthy(bindingGeneralPage.chipGroupHealthy, requireView())
                viewModel.chipsColorUnhealthy(bindingGeneralPage.chipGroupUnhealthy, requireView())
                viewModel.chipsColorSymptoms(bindingGeneralPage.chipGroupSymptoms, requireView())
                viewModel.chipsColorCare(bindingGeneralPage.chipGroupCare, requireView())

                chipsHealthyCheckDistinct = chipsHealthyCheck.distinct()
                chipsUnHealthyCheckDistinct = chipsUnHealthyCheck.distinct()
                chipsSymptomsCheckDistinct = chipsSymptomsCheck.distinct()
                chipsCareCheckDistinct = chipsCareCheck.distinct()

                var cv = ContentValues()
                cv.put("DATE", "${bindingGeneralPage.txtRecord.text.drop(7)}")
                cv.put("SUGAR", bindingGeneralPage.txtSugar.text.toString())
                cv.put("CHIPSHEALTHY", "${chipsHealthyCheckDistinct.joinToString()}")
                cv.put("CHIPSUNHEALTHY", "${chipsUnHealthyCheckDistinct.joinToString()}")
                cv.put("CHIPSSYMPTOMS", "${chipsSymptomsCheckDistinct.joinToString()}")
                cv.put("CHIPSCARE", "${chipsCareCheckDistinct.joinToString()}")
                cv.put("DAYS", bindingGeneralPage.txtRecord.text.toString().drop(7).split(".")?.get(0).toInt())
                cv.put("MONTH", bindingGeneralPage.txtRecord.text.toString().drop(7).split(".")?.get(1).toInt())
                cv.put("YEARS", bindingGeneralPage.txtRecord.text.toString().drop(7).split(".")?.get(2).dropLast(5).replace(" ", "").toInt())
                cv.put("HOURS", bindingGeneralPage.txtRecord.text.toString().drop(7).split(" ")?.get(1).dropLast(2).replace(":", "").toInt())
                cv.put("MINUTE", bindingGeneralPage.txtRecord.text.toString().drop(7).split(":")?.get(1).toInt())

                MyDBHelper(requireContext()).readableDatabase.insert("USERS", null, cv)

                cardList.clear()
                Statistics.bindingStatistics.recyclerStatistics.adapter!!.notifyDataSetChanged()

                viewModel.graph(bindingGeneralPage.graph, requireContext(), bindingGeneralPage.scrollGraph, bindingGeneralPage.txtOnbord, requireView())

                bindingGeneralPage.scrollGraph.post {
                    bindingGeneralPage.scrollGraph.fullScroll(View.FOCUS_RIGHT)
                }

                chipsHealthyCheckDistinct = arrayListOf()
                chipsHealthyCheck = arrayListOf()
                chipsUnHealthyCheckDistinct = arrayListOf()
                chipsUnHealthyCheck = arrayListOf()
                chipsSymptomsCheckDistinct = arrayListOf()
                chipsSymptomsCheck = arrayListOf()
                chipsSymptomsCheckDistinct = arrayListOf()
                chipsSymptomsCheck = arrayListOf()
            }
        }

        if(bindingGeneralPage.txtRecord.text == ""){
            viewModel.getDateTimeCalendar(bindingGeneralPage.txtRecord)
        }
        pickDate()
    }

    private fun pickDate(){
        bindingGeneralPage.txtRecord.setOnClickListener{
            viewModel.getDateTimeCalendar(bindingGeneralPage.txtRecord)
            DatePickerDialog(requireContext(), this, year, month, day).show()
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
        saveday = day
        savemonth = month
        saveyear = year
        viewModel.getDateTimeCalendar(bindingGeneralPage.txtRecord)
        TimePickerDialog(requireContext(), this, hour, minute, false).show()
    }

    override fun onTimeSet(view: TimePicker?, hour: Int, minute: Int) {
        savehour = hour
        saveminute = minute
        bindingGeneralPage.txtRecord.text = "Record $saveday.${savemonth + 1}.$saveyear $savehour:$saveminute"
    }
}