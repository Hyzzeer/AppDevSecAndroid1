package com.example.appkotlin

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activty_pin.*
import net.sqlcipher.database.SQLiteDatabase
import org.json.JSONArray
import org.json.JSONObject


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Log.i(TAG, "START")

    }

    ////////// Constantes //////////

    private var TAG:String = "LOGIN_MESSAGE"
    private var ERROR_FETCH:String = "error fetch"
    private val URL_CONFIG:String = "https://6007f1a4309f8b0017ee5022.mockapi.io/api/m1/config/1"
    private val URL_ACCOUNTS:String = "https://6007f1a4309f8b0017ee5022.mockapi.io/api/m1/accounts/"
    private var pin:String = ""
    private val config: JSONObject = JSONObject()

    ////////// Database //////////

    private fun getDb(pin:String): SQLiteDatabase {
        SQLiteDatabase.loadLibs(this)
        val databaseFile = getDatabasePath("demo.db")
        return SQLiteDatabase.openOrCreateDatabase(databaseFile, pin, null)
    }

    private fun initDb(database: SQLiteDatabase){
        database.execSQL("create table if not exists users(id, name, lastname)")
    }

    private fun logout(){
        SQLiteDatabase.loadLibs(this)
        val databaseFile = getDatabasePath("demo.db")
        databaseFile.delete()
    }

    private fun insertConfigToDb(database: SQLiteDatabase, config:JSONObject){
        database.execSQL("insert into users(id, name, lastname) values(?, ?, ?)",
                arrayOf<Any>(config.get("id").toString(), config.get("name").toString(),config.get("lastname").toString())
        )
    }

    ////////// OnClick functions //////////

    fun onClickConnexion(v:View){
        checkCredentialsOnline(nameInput.text.toString(), lastnameInput.text.toString()) { user:JSONObject?, err:VolleyError? ->
            if (err!=null) loginMessage.text = ERROR_FETCH
            else{
                if ((user==null)){
                    loginMessage.text = "Wrong credentials"
                }
                else{
                    loginMessage.text = "Good"
                    // save user info dans bdd
                    config.put("id", user.get("id"))
                    config.put("name", user.get("name"))
                    config.put("lastname", user.get("lastname"))
                    // changer activity
                    setContentView(R.layout.activty_pin);
                }
            }
        }
    }

    fun onClickPinButton(v: View){
        Log.i(TAG, "Login")
        val button = v as Button // cast view en button
        val radioArray = arrayOf(radioButton1, radioButton2, radioButton3, radioButton4)

        if (button.id == buttonDelete.id){
            if ((pin.length>0) and (pin.length<=4)) {
                radioArray[pin.length - 1].isChecked = false
                pin = pin.slice(0 until pin.length - 1)
            }
        }
        else if (button.id == buttonSubmit.id){
            if (pin.length==4) {
                val database: SQLiteDatabase = getDb(pin)
                initDb(database)
                insertConfigToDb(database, config)
                val intent = Intent(this, SplashActivity::class.java)
                startActivity(intent)
            }
            else{
                pinMessage.text = "put 4 pins"
            }
        }
        else if (!button.text.isNullOrEmpty()){
            if ((pin.length>=0) and (pin.length<4)){
                pin += button.text.toString()
                radioArray[pin.length-1].isChecked = true
            }
        }
    }

    ////////// API request functions //////////

    // check user credentials, return user:JSONObject if ok, else return null
    private fun checkCredentialsOnline(name:String, lastname:String, cb:(user:JSONObject?, err:VolleyError?)->Unit){
        val queue = Volley.newRequestQueue(this)

        var ok:Boolean = false

        val stringRequest = StringRequest(Request.Method.GET, URL_CONFIG,
                Response.Listener<String>{ response ->
                    val resStr:String = response.toString()
                    val user:JSONObject = JSONObject(resStr)
                    ok = (user.get("name").toString()==name) and (user.get("lastname").toString()==lastname)
                    if (ok) cb(user, null)
                    else cb(null, null)
                },
                Response.ErrorListener { error ->
                    cb(null, error)
                })

        queue.add(stringRequest)
        //queue.start()
    }
}