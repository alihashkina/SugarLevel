package com.example.sugarlevel.fragment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.example.sugarlevel.R
import com.example.sugarlevel.adapters.PagerAdapter
import com.example.sugarlevel.databinding.GeneralPageFragmentBinding
import com.example.sugarlevel.databinding.TabFragmentBinding
import com.google.android.material.tabs.TabLayout

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
        val pagerAdapter = PagerAdapter(childFragmentManager)
        bindingTab.viewPager.adapter = pagerAdapter
        bindingTab.tabs.setupWithViewPager(bindingTab.viewPager)
    }

}