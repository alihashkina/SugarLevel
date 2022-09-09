package com.example.sugarlevel.fragment

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sugarlevel.R
import com.example.sugarlevel.adapters.CardAdapter
import com.example.sugarlevel.databinding.GeneralPageFragmentBinding
import com.example.sugarlevel.databinding.StatisticsFragmentBinding
import com.example.sugarlevel.fragment.GeneralPage.Companion.arrayDateGraph
import com.example.sugarlevel.viewModel.StatisticsViewModel

class Statistics : Fragment() {

    companion object {
        fun newInstance() = Statistics()
        lateinit var bindingStatistics: StatisticsFragmentBinding
        var adapter = CardAdapter()
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

        bindingStatistics.apply {
            recyclerStatistics.layoutManager = LinearLayoutManager(context)
            recyclerStatistics.adapter = adapter
        }

//        bindingStatistics.recyclerStatistics.layoutManager = GridLayoutManager(requireContext(),1)
//        Log.i("LOG", "$cardFlag")
//        if(!cardFlag){
//    cardStatistics()
       // }
    }

//     fun cardStatistics(){
//         val cardsAdapter = CardsAdapter(arrayDateStaistics, arrayTimeStaistics, arrayHealthyS, arrayUnHealthyS, arraySymptomsS, arrayCareS, arraySugarS)
//         bindingStatistics.recyclerStatistics.adapter = cardsAdapter
//     }


}