package com.example.sugarlevel.fragment


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.view.children
import androidx.core.view.marginBottom
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.sugarlevel.R
import com.example.sugarlevel.TinyDB
import com.example.sugarlevel.databinding.GeneralPageFragmentBinding
import com.example.sugarlevel.viewModel.GeneralPageViewModel
import java.util.*
import androidx.lifecycle.Observer
import com.example.sugarlevel.adapters.CardAdapter
import com.example.sugarlevel.viewModel.GeneralPageViewModel.Companion.arrayDateGraph
import com.example.sugarlevel.viewModel.GeneralPageViewModel.Companion.arraySugarGraph
import com.example.sugarlevel.viewModel.StatisticsViewModel
import com.example.sugarlevel.viewModel.StatisticsViewModel.Companion.counterSts
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartSymbolType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import im.dacer.androidcharts.LineView
import java.text.SimpleDateFormat


class GeneralPage : Fragment() {


    companion object{
        fun newInstance() = GeneralPage()
        lateinit var bindingGeneralPage: GeneralPageFragmentBinding
        lateinit var tinyDB: TinyDB
    }

    var chipsOtherCheck = arrayListOf<String>()
    var dateVM = ""
    var daysVM = 0
    var monthVM = 0
    var yearsVM = 0
    var hoursVM = 0
    var minuteVM = 0
    var sugarVM = "7.1"
    lateinit var timePicker: TimePickerDialog
    lateinit var datePicker: DatePickerDialog
    var calendar: Calendar = Calendar.getInstance()
    val viewModel: GeneralPageViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingGeneralPage= DataBindingUtil.inflate(inflater, R.layout.general_page_fragment,container,false)
        return bindingGeneralPage.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        bindingGeneralPage.apply {

            scrollGraph.post {
                scrollGraph.fullScroll(View.FOCUS_RIGHT)
            }

            createChipGroup(
                type = TypeChips.Healthy(),
                chipGroup = chipGroupHealthy,
                list = viewModel.getHealthyList(requireContext())
            )
            createChipGroup(
                type = TypeChips.Unhealthy(),
                chipGroup = chipGroupUnhealthy,
                list = viewModel.getUnhealthyList(requireContext())
            )
            createChipGroup(
                type = TypeChips.Symptoms(),
                chipGroup = chipGroupSymptoms,
                list = viewModel.getSymptomsList(requireContext())
            )
            createChipGroup(
                type = TypeChips.Care(),
                chipGroup = chipGroupCare,
                list = viewModel.getCareList(requireContext())
            )
            createChipGroup(
                type = TypeChips.Other(),
                chipGroup = chipGroupOtherTags,
                list = tinyDB.getListString("OtherChips")
            )

            txtRecord.setOnClickListener {
                datePicker.show()
            }

            //получаем сохраненный сахар
            if (!tinyDB.getString("Sugar").isEmpty()) {
                txtSugar.setText(tinyDB.getString("Sugar"))
            } else {
                txtSugar.setText("7.1")
            }

            //наблюдатель обновление графика
            viewModel.counter.observe(viewLifecycleOwner, Observer{
                graph(scrollGraph, txtOnbord)
            })

            //текст на кнопке сохранить
            btnSaveText(btnSave, txtSugar, requireContext())

            //невидимая кнопка для удаления карточки - костыль
            deleteCard.setOnClickListener {
                graph(scrollGraph, txtOnbord)
            }

            //получение графика
            viewModel.readDB()

            //прослушиватель для изменения кнопки сохранить
            txtSugar.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    btnSaveText(btnSave, txtSugar, requireContext())
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })

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
                btnPlus(txtSugar)
            }

            //кнопка -
            btnMinus.setOnClickListener {
                btnMinus(txtSugar)
            }


            //кнопка сохранения
            btnSave.setOnClickListener {
                if (txtSugar.text.toString() != "") {
                    CardAdapter.deleteCard = false

                    //добавляем сохраненные данные
                    chipsOtherCheck = tinyDB.getListString("OtherChips")
                    tinyDB.putString("Sugar", txtSugar.text.toString())

                    val healthySelectedItems: List<String> = getSelectedItems(chipGroupHealthy)
                    val unhealthySelectedItems: List<String> = getSelectedItems(chipGroupUnhealthy)
                    val symptomsSelectedItems: List<String> = getSelectedItems(chipGroupSymptoms)
                    val careSelectedItems: List<String> = getSelectedItems(chipGroupCare)
                    val otherSelectedItems: List<String> = getSelectedItems(chipGroupOtherTags)

                    //заполнение бд
                    dateVM = txtRecord.text.toString().replace("${context?.getString(R.string.record)} ", "")
                    sugarVM = txtSugar.text.toString()
                    daysVM = txtRecord.text.toString().replace("${context?.getString(R.string.record)} ", "").split(".")?.get(0).toInt()
                    monthVM = txtRecord.text.toString().replace("${context?.getString(R.string.record)} ", "").split(".")?.get(1).toInt()
                    yearsVM = txtRecord.text.toString().replace("${context?.getString(R.string.record)} ", "").split(".")?.get(2).dropLast(5).replace(" ", "").toInt()
                    hoursVM = txtRecord.text.toString().replace("${context?.getString(R.string.record)} ", "").split(" ")?.get(1).dropLast(2).replace(":", "").toInt()
                    minuteVM = txtRecord.text.toString().replace("${context?.getString(R.string.record)} ", "").split(":")?.get(1).toInt()
                    tinyDB.putInt("idDB", tinyDB.getInt("idDB")+1)

                    viewModel.addDB(requireContext(), dateVM, sugarVM,
                        healthySelectedItems,
                        unhealthySelectedItems,
                        symptomsSelectedItems,
                        careSelectedItems, daysVM, monthVM, yearsVM, hoursVM, minuteVM, otherSelectedItems, tinyDB.getInt("idDB"))

                    //обновляем график
                    viewModel.readDB()

                    Toast.makeText(requireContext(), requireContext().getString(R.string.toastSave), Toast.LENGTH_SHORT).show()
                    scrollGeneral.post {
                        scrollGeneral.fullScroll(View.FOCUS_UP)
                    }
                }
            }

            //фокус при пустой строке/скрытие клавы
            btnOtherTags.setOnClickListener {
                val inputMethodManager =
                    context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (editTxtOtherTags.text.isNotEmpty()) {
                    val chip = layoutInflater.inflate(
                        R.layout.chip,
                        chipGroupOtherTags,
                        false
                    ) as Chip
                    chip.isCheckable = true
                    chip.chipBackgroundColor =
                        resources.getColorStateList(R.drawable.chip_state_other)
                    chip.text = editTxtOtherTags.text.toString()
                    chip.isCloseIconVisible = true
                    chip.setOnCloseIconClickListener {
                        chipGroupOtherTags.removeView(chip)
                        chipsOtherCheck.remove(chip?.text)
                        tinyDB.putListString("OtherChips", chipsOtherCheck)
                    }
                    chipsOtherCheck.add("${chip?.text}")
                    chip.isChecked = true
                    chipGroupOtherTags.addView(chip)
                    tinyDB.putListString("OtherChips", chipsOtherCheck)
                    editTxtOtherTags.setText("")
                    inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
                } else {
                    editTxtOtherTags.requestFocus()
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

            pickDate()
        }
    }

    //дейт/тайм пикеры
    private fun pickDate() {
        val timeListener =
            TimePickerDialog.OnTimeSetListener { view: TimePicker?, hour: Int, minute: Int ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                updateDateAndTime()
            }
        val dateListener =
            DatePickerDialog.OnDateSetListener { view: DatePicker?, year: Int, month: Int, day: Int ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)
                timePicker.show()
            }
        timePicker = TimePickerDialog(
            requireContext(),
            timeListener,
            calendar.get(Calendar.HOUR),
            calendar.get(Calendar.MINUTE),
            true
        )
        datePicker = DatePickerDialog(
            requireContext(),
            dateListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        bindingGeneralPage.txtRecord.text =
            "${requireContext().getString(R.string.record)} ${getCurrentDateAndTime()}"
    }

    fun updateDateAndTime() {
        bindingGeneralPage.txtRecord.text =
            "${requireContext().getString(R.string.record)} ${getCurrentDateAndTime()}"
    }

    fun getCurrentDateAndTime(): String {
        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm")
        val currentDateAndTime = sdf.format(calendar.time)
        return currentDateAndTime
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

    fun graph(scrollGraph: HorizontalScrollView, txtOnbord: LinearLayout) {
        if (!tinyDB.getString("DateDB").isEmpty()) {

            counterSts.value?.let {
                counterSts.value = false
            }

            bindingGeneralPage.txtOnbord.visibility = View.GONE
            scrollGraph.visibility = View.VISIBLE

            val aaChartModel: AAChartModel = AAChartModel()
                .chartType(AAChartType.Spline)
                .backgroundColor("#FFFFFF")
                .dataLabelsEnabled(true)
                .legendEnabled(true)
                .animationDuration(10)
                .yAxisVisible(true)
                .yAxisLabelsEnabled(false)
                .yAxisTitle("")
                .markerSymbol(AAChartSymbolType.Circle)
                .colorsTheme(arrayOf("#29B6FC"))
                .categories(arrayDateGraph.toTypedArray())
                .series(
                    arrayOf(
                        AASeriesElement()
                            .name(this.getString(R.string.sugar))
                            .data(arraySugarGraph.toTypedArray())
                    )
                )
            bindingGeneralPage.aaChartView.aa_drawChartWithChartModel(aaChartModel)

            tinyDB.remove("DateDB")

            //фокус графика в конец
            scrollGraph.post {
                scrollGraph.fullScroll(View.FOCUS_RIGHT)
            }
        }else{
            txtOnbord.visibility = View.VISIBLE
            scrollGraph.visibility = View.GONE
        }
    }

    @SuppressLint("ResourceType")
    fun createChipGroup(type: TypeChips, chipGroup: ChipGroup, list: List<String>) {
        val chipBg = when (type) {
            is TypeChips.Care -> resources.getColorStateList(R.drawable.chip_state_care)
            is TypeChips.Healthy -> resources.getColorStateList(R.drawable.chip_state_healthy)
            is TypeChips.Symptoms -> resources.getColorStateList(R.drawable.chip_state_symptoms)
            is TypeChips.Unhealthy -> resources.getColorStateList(R.drawable.chip_state_unhealthy)
            is TypeChips.Other -> resources.getColorStateList(R.drawable.chip_state_other)
        }
        list.forEach {
            val chip = layoutInflater.inflate(
                R.layout.chip,
                chipGroup,
                false
            ) as Chip
            chip.chipBackgroundColor = chipBg
            chip.isCheckable = true
            chip.text = it
            chipGroup.addView(chip)
            if (chipBg == resources.getColorStateList(R.drawable.chip_state_other)){
                chip.isCloseIconVisible = true
            }
        }
    }

    fun getSelectedItems(chipGroup: ChipGroup): List<String> {
        return chipGroup.children.filter {
            (it as Chip).isChecked
        }.map {
            return@map (it as Chip).text.toString()
        }.toList()
    }
}

sealed class TypeChips() {
    class Healthy() : TypeChips()
    class Unhealthy() : TypeChips()
    class Symptoms() : TypeChips()
    class Care() : TypeChips()
    class Other() : TypeChips()
}
