package com.example.sugarlevel.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

//хелпер для бд
class MyDBHelper(context: Context) : SQLiteOpenHelper(context, "USERS", null, 3) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE USERS(USERID INTEGER PRIMARY KEY AUTOINCREMENT, DATE TEXT, SUGAR TEXT, CHIPSHEALTHY TEXT, CHIPSUNHEALTHY TEXT, CHIPSSYMPTOMS TEXT, CHIPSCARE TEXT, CHIPSOTHER TEXT, DAYS INTEGER, MONTH INTEGER, YEARS INTEGER, HOURS INTEGER, MINUTE INTEGER, ID INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < newVersion) {
            db?.execSQL("ALTER TABLE USERS ADD ID INT NOT NULL DEFAULT(0)")
            db?.execSQL("ALTER TABLE USERS ADD CHIPSOTHER TEXT NOT NULL DEFAULT(0)")
            db?.execSQL("UPDATE USERS SET ID = USERS.USERID + 1000000")
        }
    }
}