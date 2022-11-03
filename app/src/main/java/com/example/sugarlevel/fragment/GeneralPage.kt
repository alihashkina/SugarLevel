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
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import im.dacer.androidcharts.LineView
import java.text.SimpleDateFormat


class GeneralPage : Fragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {


    companion object{
        fun newInstance() = GeneralPage()
        lateinit var bindingGeneralPage: GeneralPageFragmentBinding
        lateinit var tinyDB: TinyDB
    }

    lateinit var viewModel: GeneralPageViewModel
    var chipsHealthyCheck = arrayListOf<String>()
    var chipsUnHealthyCheck = arrayListOf<String>()
    var chipsSymptomsCheck = arrayListOf<String>()
    var chipsCareCheck = arrayListOf<String>()
    var chipsOtherCheck = arrayListOf<String>()
    var chipsHealthyCheckDistinct = listOf<String>()
    var chipsUnHealthyCheckDistinct = listOf<String>()
    var chipsSymptomsCheckDistinct = listOf<String>()
    var chipsCareCheckDistinct = listOf<String>()
    var chipsOtherCheckDistinct = listOf<String>()
    var dateVM = ""
    var sugarVM = ""
    var chipsHealthyVM = ""
    var chipsUnHealthyVM = ""
    var chipsSymptomsVM = ""
    var chipsCareVM = ""
    var daysVM = 0
    var monthVM = 0
    var yearsVM = 0
    var hoursVM = 0
    var minuteVM = 0
    var chipsOtherVM = ""
    val calendar = Calendar.getInstance()
    var year = 0
    var month = 0
    var day = 0
    var hour = 0
    var minute = 0
    var saveyear = 0
    var savemonth = 0
    var saveday = 0
    var savehour = 0
    var saveminute = 0
    var chipsGroup = ""
    var colors = intArrayOf()

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
        viewModel = ViewModelProvider(this).get(GeneralPageViewModel::class.java)

        bindingGeneralPage.apply {

            //получаем сохраненный сахар
//             tinyDB = TinyDB(context)
            if (!tinyDB.getString("Sugar").isEmpty()) {
                txtSugar.setText(tinyDB.getString("Sugar"))
            } else {
                txtSugar.setText("7.1")
            }

            //наблюдатель обновление графика
            viewModel.counter.observe(viewLifecycleOwner, Observer{
                graph(graph, scrollGraph, txtOnbord)
            })

            //цвета чипсов
            chipsColor(requireView(), tinyDB, chipGroupHealthy, chipGroupUnhealthy, chipGroupSymptoms, chipGroupCare)

            //текст на кнопке сохранить
            btnSaveText(btnSave, txtSugar, requireContext())

            //получаем сохраненные доп чипсы
            saveChips(requireContext(), chipGroupOtherTags, tinyDB)

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
                btnPlus(txtSugar)
            }

            //кнопка -
            btnMinus.setOnClickListener {
                btnMinus(txtSugar)
            }

            //невидимая кнопка для удаления карточки - костыль
            deleteCard.setOnClickListener {
                graph(graph, scrollGraph, txtOnbord)
            }

            //получение графика
            viewModel.readDB()

            //кнопка сохранения
            btnSave.setOnClickListener {
                if (txtSugar.text.toString() != "") {
                    CardAdapter.deleteCard = false

                    //добавляем сохраненные данные
                    chipsOtherCheck = tinyDB.getListString("OtherChips")
                    tinyDB.putString("Sugar", txtSugar.text.toString())

                    //цвет чипсов
                    chipsColor(requireView(), tinyDB, chipGroupHealthy, chipGroupUnhealthy, chipGroupSymptoms, chipGroupCare)

                    //удаление повторяющихся символов
                    chipsHealthyCheckDistinct = chipsHealthyCheck.distinct()
                    chipsUnHealthyCheckDistinct = chipsUnHealthyCheck.distinct()
                    chipsSymptomsCheckDistinct = chipsSymptomsCheck.distinct()
                    chipsCareCheckDistinct = chipsCareCheck.distinct()
                    chipsOtherCheckDistinct = chipsOtherCheck.distinct()

                    //заполнение бд
                    dateVM = txtRecord.text.toString().drop(7)
                    sugarVM = txtSugar.text.toString()
                    chipsHealthyVM = chipsHealthyCheckDistinct.joinToString()
                    chipsUnHealthyVM = chipsUnHealthyCheckDistinct.joinToString()
                    chipsSymptomsVM = chipsSymptomsCheckDistinct.joinToString()
                    chipsCareVM = chipsCareCheckDistinct.joinToString()
                    daysVM = txtRecord.text.toString().drop(7).split(".")?.get(0).toInt()
                    monthVM = txtRecord.text.toString().drop(7).split(".")?.get(1).toInt()
                    yearsVM = txtRecord.text.toString().drop(7).split(".")?.get(2).dropLast(5).replace(" ", "").toInt()
                    hoursVM = txtRecord.text.toString().drop(7).split(" ")?.get(1).dropLast(2).replace(":", "").toInt()
                    minuteVM = txtRecord.text.toString().drop(7).split(":")?.get(1).toInt()
                    chipsOtherVM = chipsOtherCheckDistinct.joinToString()
                    tinyDB.putInt("idDB", tinyDB.getInt("idDB")+1)

                    viewModel.addDB(requireContext(), dateVM, sugarVM, chipsHealthyVM, chipsUnHealthyVM, chipsSymptomsVM, chipsCareVM, daysVM, monthVM, yearsVM, hoursVM, minuteVM, chipsOtherVM, tinyDB.getInt("idDB"))

                    //обновляем график
                    viewModel.readDB()

                    //фокус графика в конец
                    scrollGraph.post {
                        scrollGraph.fullScroll(View.FOCUS_RIGHT)
                    }

                    //очищаем чипсы
                    chipGroupHealthy.clearCheck()
                    chipGroupUnhealthy.clearCheck()
                    chipGroupSymptoms.clearCheck()
                    chipGroupCare.clearCheck()

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
                    addChip(editTxtOtherTags.text.toString(), requireContext(), chipGroupOtherTags, tinyDB)
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
                getDateTimeCalendar(txtRecord, requireContext())
            }

            pickDate()
        }
    }

    //дейт/тайм пикеры
    private fun pickDate(){
        bindingGeneralPage.txtRecord.setOnClickListener{
            getDateTimeCalendar(bindingGeneralPage.txtRecord, requireContext())
            DatePickerDialog(requireContext(), this, year, month, day).show()
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
        saveday = day
        savemonth = month
        saveyear = year
        getDateTimeCalendar(bindingGeneralPage.txtRecord, requireContext())
        TimePickerDialog(requireContext(), this, hour, minute, true).show()
    }

    override fun onTimeSet(view: TimePicker?, hour: Int, minute: Int) {
        savehour = hour
        saveminute = minute
        bindingGeneralPage.txtRecord.text = "${requireContext().getString(R.string.record)} $saveday.${savemonth + 1}.$saveyear $savehour:$saveminute"
    }

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
            "Symptoms" -> colors = intArrayOf(Color.parseColor("#81D4fA"), Color.parseColor("#E0E0E0"))
            "Care" -> colors = intArrayOf(Color.parseColor("#FFF590"), Color.parseColor("#E0E0E0"))
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

    fun graph(graph: LineView, scrollGraph: HorizontalScrollView, txtOnbord: LinearLayout){
        if (!tinyDB.getString("DateDB").isEmpty()) {
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
            tinyDB.remove("DateDB")
        }
    }
}