package com.example.sugarlevel.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.sugarlevel.R
import com.example.sugarlevel.adapters.PagerAdapter
import com.example.sugarlevel.databinding.TabFragmentBinding

class TabFragment : Fragment() {

    companion object {
        fun newInstance() = TabFragment()
        lateinit var bindingTab: TabFragmentBinding
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingTab = DataBindingUtil.inflate(inflater, R.layout.tab_fragment,container,false)
        return bindingTab.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //заполнение табов
        val pagerAdapter = PagerAdapter(childFragmentManager, requireContext())
        bindingTab.viewPager.adapter = pagerAdapter
        bindingTab.tabs.setupWithViewPager(bindingTab.viewPager)
    }

}