package com.example.sugarlevel.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.RuntimeException
import java.lang.StringBuilder

//хелпер для бд
class MyDBHelper(context: Context) : SQLiteOpenHelper(context, "USERS", null, 2) {

    private val context: Context? = null

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE USERS(USERID INTEGER PRIMARY KEY AUTOINCREMENT, DATE TEXT, SUGAR TEXT, CHIPSHEALTHY TEXT, CHIPSUNHEALTHY TEXT, CHIPSSYMPTOMS TEXT, CHIPSCARE TEXT, CHIPSOTHER TEXT, DAYS INTEGER, MONTH INTEGER, YEARS INTEGER, HOURS INTEGER, MINUTE INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        for (i in oldVersion until newVersion) {
            val migrationFileName = String.format("from_1_to_2", i, i + 1)
            val migrationFileResId: Int = context?.getResources()!!.getIdentifier(migrationFileName, "raw", context.getPackageName())
            if (migrationFileResId != 0) {
                readAndExecuteSQLScript(db!!, context, migrationFileResId)
            }
        }
    }

    private fun readAndExecuteSQLScript(db: SQLiteDatabase, context: Context, sqlScriptResId: Int) {
        val res = context.resources
        try {
            val `is` = res.openRawResource(sqlScriptResId)
            val reader = BufferedReader(InputStreamReader(`is`))
            executeSQLScript(db, reader)
            reader.close()
            `is`.close()
        } catch (e: IOException) {
            throw RuntimeException("Unable to read SQL script", e)
        }
    }


    private fun executeSQLScript(db: SQLiteDatabase, reader: BufferedReader) {
        var line: String
        var statement = StringBuilder()
        while (reader.readLine().also { line = it } != null) {
            statement.append(line)
            statement.append("\n")
            if (line.endsWith(";")) {
                val toExec = statement.toString()
                db.execSQL(toExec)
                statement = StringBuilder()
            }
        }
    }
}