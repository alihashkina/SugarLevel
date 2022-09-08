package com.example.sugarlevel.viewModel

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.View
import androidx.core.view.children
import androidx.lifecycle.ViewModel
import com.example.sugarlevel.fragment.GeneralPage
import com.example.sugarlevel.fragment.GeneralPage.Companion.chipsCareCheck
import com.example.sugarlevel.fragment.GeneralPage.Companion.chipsHealthyCheck
import com.example.sugarlevel.fragment.GeneralPage.Companion.chipsSymptomsCheck
import com.example.sugarlevel.fragment.GeneralPage.Companion.chipsUnHealthyCheck
import com.example.sugarlevel.fragment.MoreChipsDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class MoreChipsDialogViewModel : ViewModel() {

    var chipsGroup = ""
    var colors = intArrayOf()

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

    private fun handleSelection(view: View){
        MoreChipsDialog.bindingMoreChipsDialog.chipGroupHealthy.checkedChipIds.forEach{
            val chip = view?.findViewById<Chip>(it)
            chipsHealthyCheck.add("${chip?.text}")
        }
        MoreChipsDialog.bindingMoreChipsDialog.chipGroupUnhealthy.checkedChipIds.forEach{
            val chip = view?.findViewById<Chip>(it)
            chipsUnHealthyCheck.add("${chip?.text}")
        }
        MoreChipsDialog.bindingMoreChipsDialog.chipGroupSymptoms.checkedChipIds.forEach{
            val chip = view?.findViewById<Chip>(it)
            chipsSymptomsCheck.add("${chip?.text}")
        }
        MoreChipsDialog.bindingMoreChipsDialog.chipGroupCare.checkedChipIds.forEach{
            val chip = view?.findViewById<Chip>(it)
            chipsCareCheck.add("${chip?.text}")
        }
    }
}
