package com.example.sugarlevel.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.sugarlevel.R
import com.example.sugarlevel.databinding.MoreChipsDialogFragmentBinding
import com.example.sugarlevel.viewModel.MoreChipsDialogViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip

class MoreChipsDialog : BottomSheetDialogFragment() {

    companion object {
        fun newInstance() = MoreChipsDialog()
        lateinit var bindingMoreChipsDialog: MoreChipsDialogFragmentBinding
    }

    private lateinit var viewModel: MoreChipsDialogViewModel

    override fun onStart() {
        super.onStart()
        dialog?.let {
            val bottomSheet = it.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_DRAGGING
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingMoreChipsDialog= DataBindingUtil.inflate(inflater, R.layout.more_chips_dialog_fragment,container,false)
        return bindingMoreChipsDialog.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MoreChipsDialogViewModel::class.java)

        viewModel.chipsColorHealthy(bindingMoreChipsDialog.chipGroupHealthy, view!!)
        viewModel.chipsColorUnhealthy(bindingMoreChipsDialog.chipGroupUnhealthy, view!!)
        viewModel.chipsColorSymptoms(bindingMoreChipsDialog.chipGroupSymptoms, view!!)
        viewModel.chipsColorCare(bindingMoreChipsDialog.chipGroupCare, view!!)

    }

    override fun getTheme() = R.style.AppBottomSheetDialogTheme
}