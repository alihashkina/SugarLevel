package com.example.sugarlevel.fragment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.example.sugarlevel.R
import com.example.sugarlevel.adapters.CardsAdapter
import com.example.sugarlevel.databinding.GeneralPageFragmentBinding
import com.example.sugarlevel.databinding.StatisticsFragmentBinding
import com.example.sugarlevel.fragment.GeneralPage.Companion.arrayDateGraph
import com.example.sugarlevel.viewModel.StatisticsViewModel

class Statistics : Fragment() {

    companion object {
        fun newInstance() = Statistics()
        lateinit var bindingStatistics: StatisticsFragmentBinding
        var arrayDateStaistics = mutableListOf<String>()
        var arrayTimeStaistics = mutableListOf<String>()
        var arrayHealthyS = mutableListOf<String>()
        var arrayUnHealthyS = mutableListOf<String>()
        var arraySymptomsS = mutableListOf<String>()
        var arrayCareS = mutableListOf<String>()
        var arraySugarS = mutableListOf<String>()
    }

    private lateinit var viewModel: StatisticsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingStatistics = DataBindingUtil.inflate(inflater, R.layout.statistics_fragment,container,false)
        return bindingStatistics.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(StatisticsViewModel::class.java)

        bindingStatistics.recyclerStatistics.layoutManager = GridLayoutManager(requireContext(),1)
        bindingStatistics.recyclerStatistics.adapter = CardsAdapter(arrayDateStaistics, arrayTimeStaistics, arrayHealthyS, arrayUnHealthyS, arraySymptomsS, arrayCareS, arraySugarS)
    }

}