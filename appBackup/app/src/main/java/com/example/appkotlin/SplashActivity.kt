package com.example.appkotlin

import android.annotation.SuppressLint
import android.content.Intent
//import android.database.Cursor
import android.database.SQLException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activty_pin.*
import net.sqlcipher.Cursor
import net.sqlcipher.DatabaseUtils
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteException
import org.json.JSONObject
import java.lang.Exception
import kotlin.math.log


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_splash)

        //val db: SQLiteDatabase = getDb()
        // val pinUser:String? = getPinFromDb(db)
        // if (pinUser==null){
        // navigate to login
        // Log.i(TAG,"navigate to login")
        // val intent = Intent(this, LoginActivity::class.java)
        // startActivity(intent)
        // }
        // else {
        // navigate to put your secret pin
        // Log.i(TAG,"put your secret pin")
        // setContentView(R.layout.activty_pin);
        //}

        //logout()
        if (!isDbExist()) {
            // navigate to login
            Log.i(TAG, "navigate to login")

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        } else {
            // navigate to put your secret pin
            Log.i(TAG, "put your secret pin")

            setContentView(R.layout.activty_pin);
        }


    }

    ////////// OnClick functions //////////

    private var TAG: String = "LOGIN_MESSAGE"
    private var pin: String = ""
    private var COUNT_MAX: Int = 3

    ////////// On ck=lc //////////

    fun onClickPinButton(v: View) {
        Log.i(TAG, "Splash")
        val button = v as Button // cast view en button
        val radioArray = arrayOf(radioButton1, radioButton2, radioButton3, radioButton4)

        if (button.id == buttonDelete.id) {
            if ((pin.length > 0) and (pin.length <= 4)) {
                radioArray[pin.length - 1].isChecked = false
                pin = pin.slice(0 until pin.length - 1)
            }
        } else if (button.id == buttonSubmit.id) {
            if (pin.length == 4) {
                try {
                    val db: SQLiteDatabase = getDb(pin)
                    val cursor: Cursor = db.rawQuery("select * from users", null)
                    Log.i(TAG, DatabaseUtils.dumpCursorToString(cursor).toString())
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.putExtra("pin", pin)
                    startActivity(intent)
                }
                catch(err:SQLiteException) {
                    COUNT_MAX--
                    pinMessage.text = "pin didn't match, il vous reste $COUNT_MAX essais"
                }
                // val db: SQLiteDatabase = getDb(pin)
                // val cursor: Cursor = db.rawQuery("select * from users", null)
                // Log.i(TAG, DatabaseUtils.dumpCursorToString(cursor).toString())
                // if (db.isOpen) {
                    // val intent = Intent(this, HomeActivity::class.java)
                    // intent.putExtra("pin", pin)
                    // startActivity(intent)
                // } else {
                    // COUNT_MAX--
                    // pinMessage.text = "pin didn't match, il vous reste $COUNT_MAX essais"
                // }
                //val pinUser:String? = getPinFromDb(db)
                //if (pin == pinUser){
                // val intent = Intent(this, HomeActivity::class.java)
                // startActivity(intent)
                // }
                // else {
                // COUNT_MAX--
                // pinMessage.text = "pin didn't match, il vous reste $COUNT_MAX essais"
                // }
                if (COUNT_MAX == 0) {
                    logout()
                    val intent = Intent(this, SplashActivity::class.java)
                    startActivity(intent)
                }
            } else {
                pinMessage.text = "put 4 pins"
            }
        } else if (!button.text.isNullOrEmpty()) {
            if ((pin.length >= 0) and (pin.length < 4)) {
                pin += button.text.toString()
                radioArray[pin.length - 1].isChecked = true
            }
        }
    }

    ////////// Database //////////

    private fun isDbExist(): Boolean {
        SQLiteDatabase.loadLibs(this)
        val databaseFile = getDatabasePath("demo.db")
        return databaseFile.exists()
    }

    private fun getDb(pin: String): SQLiteDatabase {
        SQLiteDatabase.loadLibs(this)
        val databaseFile = getDatabasePath("demo.db")
        return SQLiteDatabase.openOrCreateDatabase(databaseFile, pin, null)
    }

    private fun initDb(database: SQLiteDatabase) {
        database.execSQL("create table if not exists users(id, name, lastname)")
    }

    private fun logout() {
        SQLiteDatabase.loadLibs(this)
        val databaseFile = getDatabasePath("demo.db")
        databaseFile.delete()
    }
}