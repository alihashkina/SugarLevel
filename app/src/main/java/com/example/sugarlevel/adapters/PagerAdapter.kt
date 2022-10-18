package com.example.sugarlevel.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.sugarlevel.MainActivity.Companion.tabRecord
import com.example.sugarlevel.MainActivity.Companion.tabStatistcs
import com.example.sugarlevel.R
import com.example.sugarlevel.fragment.GeneralPage
import com.example.sugarlevel.fragment.Statistics
import com.example.sugarlevel.fragment.TabFragment
import kotlin.coroutines.coroutineContext

//адаптер для табов
class PagerAdapter (fm: FragmentManager): FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position){
            0 -> {GeneralPage()}
            else -> {
                return Statistics()
            }
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {

        return when (position){
            0 -> tabRecord
            else ->{
                return tabStatistcs
            }
        }
    }
}