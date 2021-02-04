package com.example.appkotlin.Helper

import android.database.Cursor
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import net.sqlcipher.database.SQLiteDatabase
import org.json.JSONObject

class DbHelper : AppCompatActivity{

    private var db : SQLiteDatabase

    constructor() {
        this.db = this.getDb()
    }

    ////////// Constantes //////////

    private var TAG:String = "LOGIN_MESSAGE"

    ////////// Database //////////

    private fun getDb(): SQLiteDatabase {
        SQLiteDatabase.loadLibs(this)
        val databaseFile = getDatabasePath("demo.db")
        Log.i(TAG, databaseFile.toString())
        return SQLiteDatabase.openOrCreateDatabase(databaseFile, "test123", null)
    }

    private fun initDb(database: SQLiteDatabase){
        database.execSQL("create table if not exists users(id, name, lastname, pin)")
    }

    private fun insertConfigToDb(database: SQLiteDatabase, config: JSONObject){
        database.execSQL("insert into users(id, name, lastname) values(?, ?, ?)",
            arrayOf<Any>(config.get("id").toString().toInt(), config.get("name").toString(),config.get("lastname").toString())
        )
    }

    private fun insertPinToDb(database: SQLiteDatabase, pin:Int){
        database.execSQL("insert into users(pin) values(?)",
            arrayOf<Any>(pin)
        )
    }

    private fun loadConfigFromDb(db: SQLiteDatabase): JSONObject {
        val config: JSONObject = JSONObject()
        val cursor: Cursor = db.rawQuery("select * from users", null)
        return config
    }

}