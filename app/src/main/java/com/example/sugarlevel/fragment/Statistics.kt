package com.example.sugarlevel.fragment

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.activity.findViewTreeOnBackPressedDispatcherOwner
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sugarlevel.R
import com.example.sugarlevel.adapters.CardAdapter
import com.example.sugarlevel.adapters.dataClass.Card
import com.example.sugarlevel.databinding.StatisticsFragmentBinding
import com.example.sugarlevel.viewModel.StatisticsViewModel

class Statistics : Fragment() {

    companion object {
        fun newInstance() = Statistics()
        lateinit var bindingStatistics: StatisticsFragmentBinding
        val adapter = CardAdapter()
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
            recyclerStatistics.layoutManager = GridLayoutManager(context, 1)
            recyclerStatistics.adapter = adapter
        }
    }
}