package com.example.sugarlevel

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import com.example.sugarlevel.fragment.GeneralPage


class MainActivity : AppCompatActivity() {

    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf<String>(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.containerView, GeneralPage.newInstance())
                .addToBackStack(null)
                .commit()
        }
        verifyStoragePermissions(this)
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    fun verifyStoragePermissions(activity: Activity?) {
        // Check if we have write permission
        val permission =
            ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        }
    }
}