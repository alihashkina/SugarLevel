package com.example.sugarlevel

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.sugarlevel.adapters.CardAdapter
import com.example.sugarlevel.fragment.GeneralPage
import com.example.sugarlevel.fragment.TabFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.lang.Exception


class MainActivity : AppCompatActivity() {

    companion object{
        var tabRecord : String? = null
        var tabStatistcs : String? = null
    }

    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf<String>(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //отключение темной темы
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        //добавление фрагмента
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.containerView, TabFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }
        tabRecord = this.getString(R.string.record)
        tabStatistcs = this.getString(R.string.statistics)

        verifyStoragePermissions(this)
    }

    //выход при нажатии кнопки назад
    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    //запрос разрешений
    fun verifyStoragePermissions(activity: Activity?) {
        val permission =
            ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        }
    }
}