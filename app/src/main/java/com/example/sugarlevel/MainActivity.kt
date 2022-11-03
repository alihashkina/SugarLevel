package com.example.sugarlevel

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import com.example.sugarlevel.db.MyDBHelper
import com.example.sugarlevel.fragment.GeneralPage
import com.example.sugarlevel.fragment.TabFragment


class MainActivity : AppCompatActivity() {

    companion object{
        var tabRecord : String? = null
        var tabStatistcs : String? = null
        lateinit var helper: MyDBHelper
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
        GeneralPage.tinyDB = TinyDB(this)

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
            ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
            GeneralPage.tinyDB.putInt("idDB", 0)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        helper = MyDBHelper(this)
        helper.close()
    }

    override fun onResume() {
        super.onResume()
        helper = MyDBHelper(this)
        helper.readableDatabase
    }
}