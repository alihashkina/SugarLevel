package com.example.sugarlevel.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sugarlevel.R
import com.example.sugarlevel.TinyDB
import com.example.sugarlevel.adapters.CardAdapter
import com.example.sugarlevel.adapters.CardAdapter.Companion.cardList
import com.example.sugarlevel.databinding.GeneralPageFragmentBinding
import com.example.sugarlevel.db.MyDBHelper
import com.example.sugarlevel.viewModel.GeneralPageViewModel
import com.example.sugarlevel.viewModel.GeneralPageViewModel.Companion.day
import com.example.sugarlevel.viewModel.GeneralPageViewModel.Companion.hour
import com.example.sugarlevel.viewModel.GeneralPageViewModel.Companion.minute
import com.example.sugarlevel.viewModel.GeneralPageViewModel.Companion.month
import com.example.sugarlevel.viewModel.GeneralPageViewModel.Companion.year
import java.util.*
import kotlin.math.sign
import kotlin.properties.Delegates


class GeneralPage : Fragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    companion object {
        fun newInstance() = GeneralPage()
        var editSugar = ""
        var dateDB = ""
        var chipsHDB = ""
        var chipsUhDB = ""
        var chipsSDB = ""
        var chipsCDB = ""
        var chipsODB = ""
        var sugarDB: Float = 0.0F
        var sugarDBml = 0.0F
        var arrayDateGraph : MutableList<String> = mutableListOf()
        var arraySugarGraph : MutableList<Float> = mutableListOf()
        var chipsHealthyCheck = arrayListOf<String>()
        var chipsUnHealthyCheck = arrayListOf<String>()
        var chipsSymptomsCheck = arrayListOf<String>()
        var chipsCareCheck = arrayListOf<String>()
        var chipsOtherCheck = arrayListOf<String>()
        lateinit var bindingGeneralPage: GeneralPageFragmentBinding
        var chipsHealthyCheckDistinct = listOf<String>()
        var chipsUnHealthyCheckDistinct = listOf<String>()
        var chipsSymptomsCheckDistinct = listOf<String>()
        var chipsCareCheckDistinct = listOf<String>()
        var chipsOtherCheckDistinct = listOf<String>()
        lateinit var viewModel: GeneralPageViewModel
    }

    var saveyear = 0
    var savemonth = 0
    var saveday = 0
    var savehour = 0
    var saveminute = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingGeneralPage= DataBindingUtil.inflate(inflater, R.layout.general_page_fragment,container,false)
        return bindingGeneralPage.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(GeneralPageViewModel::class.java)

        bindingGeneralPage.apply {

            //получаем сохраненный сахар
            var tinyDB = TinyDB(context)
            if (!tinyDB.getString("Sugar").isEmpty()) {
                txtSugar.setText(tinyDB.getString("Sugar"))
            } else {
                txtSugar.setText("7.1")
            }

            //цвета чипсов
            viewModel.chipsColor(requireView(), tinyDB, chipGroupHealthy, chipGroupUnhealthy, chipGroupSymptoms, chipGroupCare)

            //текст на кнопке сохранить
            viewModel.btnSaveText(btnSave, txtSugar, requireContext())

            //получаем сохраненные доп чипсы
            viewModel.saveChips(requireContext(), chipGroupOtherTags, tinyDB)

            //прослушиватель для изменения кнопки сохранить
            txtSugar.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    viewModel.btnSaveText(btnSave, txtSugar, requireContext())
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })

            //фокус графика в конец
            scrollGraph.post {
                scrollGraph.fullScroll(View.FOCUS_RIGHT)
            }

            //кнопка на клавиатуре = сохранить
            txtSugar.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
                var handled = false
                if (actionId == EditorInfo.IME_ACTION_DONE && txtSugar.text.toString() != "") {
                    btnSave.callOnClick()
                    handled = true
                }
                handled
            })

            //копка +
            btnPlus.setOnClickListener {
                viewModel.btnPlus(txtSugar)
            }

            //кнопка -
            btnMinus.setOnClickListener {
                viewModel.btnMinus(txtSugar)
            }

            //невидимая кнопка для удаления карточки - костыль
            deleteCard.setOnClickListener {
                viewModel.graph(graph, requireContext(), scrollGraph, txtOnbord)
            }

            //получение графика
            viewModel.graph(graph, requireContext(), scrollGraph, txtOnbord)

            //кнопка сохранения
            btnSave.setOnClickListener {
                if (txtSugar.text.toString() != "") {
                    //добавляем сохраненные данные
                    chipsOtherCheck = tinyDB.getListString("OtherChips")
                    tinyDB.putString("Sugar", txtSugar.text.toString())
                    //флаг для удаленных карточек
                    GeneralPageViewModel.deleteCard = false

                    //цвет чипсов
                    viewModel.chipsColor(requireView(), tinyDB, chipGroupHealthy, chipGroupUnhealthy, chipGroupSymptoms, chipGroupCare)

                    //удаление повторяющихся символов
                    chipsHealthyCheckDistinct = chipsHealthyCheck.distinct()
                    chipsUnHealthyCheckDistinct = chipsUnHealthyCheck.distinct()
                    chipsSymptomsCheckDistinct = chipsSymptomsCheck.distinct()
                    chipsCareCheckDistinct = chipsCareCheck.distinct()
                    chipsOtherCheckDistinct = chipsOtherCheck.distinct()

                    //заполнение бд
                    var cv = ContentValues()
                    cv.put("DATE", "${txtRecord.text.drop(7)}")
                    cv.put("SUGAR", txtSugar.text.toString())
                    cv.put("CHIPSHEALTHY", "${chipsHealthyCheckDistinct.joinToString()}")
                    cv.put("CHIPSUNHEALTHY", "${chipsUnHealthyCheckDistinct.joinToString()}")
                    cv.put("CHIPSSYMPTOMS", "${chipsSymptomsCheckDistinct.joinToString()}")
                    cv.put("CHIPSCARE", "${chipsCareCheckDistinct.joinToString()}")
                    cv.put("DAYS", txtRecord.text.toString().drop(7).split(".")?.get(0).toInt())
                    cv.put("MONTH", txtRecord.text.toString().drop(7).split(".")?.get(1).toInt())
                    cv.put("YEARS", txtRecord.text.toString().drop(7).split(".")?.get(2).dropLast(5).replace(" ", "").toInt())
                    cv.put("HOURS", txtRecord.text.toString().drop(7).split(" ")?.get(1).dropLast(2).replace(":", "").toInt())
                    cv.put("MINUTE", txtRecord.text.toString().drop(7).split(":")?.get(1).toInt())
                    cv.put("CHIPSOTHER", "${chipsOtherCheckDistinct.joinToString()}")

                    MyDBHelper(requireContext()).readableDatabase.insert("USERS", null, cv)

                    //обновляем адаптер
                    cardList.clear()
                    Statistics.bindingStatistics.recyclerStatistics.adapter!!.notifyDataSetChanged()

                    //обновляем график
                    viewModel.graph(graph, requireContext(), scrollGraph, txtOnbord)

                    //фокус графика в конец
                    scrollGraph.post {
                        scrollGraph.fullScroll(View.FOCUS_RIGHT)
                    }

                    //очищаем массивы чипсов
                    chipsHealthyCheckDistinct = arrayListOf()
                    chipsHealthyCheck = arrayListOf()
                    chipsUnHealthyCheckDistinct = arrayListOf()
                    chipsUnHealthyCheck = arrayListOf()
                    chipsSymptomsCheckDistinct = arrayListOf()
                    chipsSymptomsCheck = arrayListOf()
                    chipsCareCheckDistinct = arrayListOf()
                    chipsCareCheck = arrayListOf()
                }
            }

            //фокус при пустой строке/скрытие клавы
            btnOtherTags.setOnClickListener {
                val inputMethodManager =
                    context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

                if (!editTxtOtherTags.text.toString().isEmpty()) {
                    viewModel.addChip(editTxtOtherTags.text.toString(), requireContext(), chipGroupOtherTags, tinyDB)
                    editTxtOtherTags.setText("")
                    inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
                }
                else {
                    editTxtOtherTags.requestFocus()
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
                }
            }

            //кнопка на клавиатуре = добавить
            editTxtOtherTags.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
                var handled = false

                if (actionId == EditorInfo.IME_ACTION_DONE && editTxtOtherTags.text.toString() != "") {
                    btnOtherTags.callOnClick()
                    handled = true
                }
                handled
            })

            //текущая дата
            if (txtRecord.text == "") {
                viewModel.getDateTimeCalendar(txtRecord, requireContext())
            }

            pickDate()
        }
    }

    //дейт/тайм пикеры
    private fun pickDate(){
        bindingGeneralPage.txtRecord.setOnClickListener{
            viewModel.getDateTimeCalendar(bindingGeneralPage.txtRecord, requireContext())
            DatePickerDialog(requireContext(), this, year, month, day).show()
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
        saveday = day
        savemonth = month
        saveyear = year
        viewModel.getDateTimeCalendar(bindingGeneralPage.txtRecord, requireContext())
        TimePickerDialog(requireContext(), this, hour, minute, true).show()
    }

    override fun onTimeSet(view: TimePicker?, hour: Int, minute: Int) {
        savehour = hour
        saveminute = minute
        bindingGeneralPage.txtRecord.text = "Record $saveday.${savemonth + 1}.$saveyear $savehour:$saveminute"
    }
}