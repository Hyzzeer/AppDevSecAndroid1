package com.example.appkotlin

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_home.*
import net.sqlcipher.Cursor
import net.sqlcipher.database.SQLiteDatabase
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException


class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        pin= intent.getStringExtra("pin").toString()

        val database: SQLiteDatabase? = pin?.let { getDb(it) }
        if (database != null) {

            initDb(database)

            val accountsArray : JSONArray? = getAllContentFromDb(database)
            if (accountsArray!=null){
                displayAccountsTable(accountsArray)
            }
            else{
                refresh(button)
            }
        }

    }

    private var TAG:String = "LOGIN_MESSAGE"
    private val config: JSONObject = JSONObject()
    lateinit var pin:String

    private fun getDb(pin: String): SQLiteDatabase {
        SQLiteDatabase.loadLibs(this)
        val databaseFile = getDatabasePath("demo.db")
        return SQLiteDatabase.openOrCreateDatabase(databaseFile, pin, null)
    }

    private fun initDb(database: SQLiteDatabase){
        database.execSQL("create table if not exists accountsAndAmounts(id,account_name, amount, iban, currency)")
    }
    private fun clearAccountTable(database: SQLiteDatabase){

        database.execSQL("delete from accountsAndAmounts")
    }
    private fun insertAccountToDb(database: SQLiteDatabase, config: JSONObject){
        database.execSQL("insert into accountsAndAmounts(id,account_name, amount, iban, currency) values(?, ?, ? ,? ,?)",
                arrayOf<Any>(config.get("id").toString(), config.get("account_name").toString(), config.get("amount").toString(), config.get("iban").toString(), config.get("currency").toString())
        )
    }

    private fun getAllContentFromDb(database: SQLiteDatabase): JSONArray? {
        val cursor: Cursor = database.rawQuery("select * from accountsAndAmounts", null)
        val arrayTmp : JSONArray = JSONArray()
        val arrayLen : Int = cursor.count
        if (cursor.count==0){
            return null
        }
        cursor.moveToFirst()
        for (line in 0 until arrayLen){
            val jsonTmp : JSONObject = JSONObject()
            for (col in cursor.columnNames){
                jsonTmp.put(col, cursor.getString(cursor.getColumnIndex(col)))
            }
            cursor.moveToNext()
            arrayTmp.put(jsonTmp)
        }
        return arrayTmp
    }

    private val client = OkHttpClient()

    private fun run(url: String) {
        val request = Request.Builder()
            .url(url)
            .build()
        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {

                var a = response.body()?.string()
                a = a.toString()
                val account: JSONArray = JSONArray(a)
                displayAccountsTable(account)
                val db: SQLiteDatabase? = pin?.let { getDb(it) }
                if (db != null) {
                    clearAccountTable(db)
                }

                for (i in 0 until account.length()) {
                    val item = account.getJSONObject(i)
                    if (db != null) {
                        insertAccountToDb(db, item)
                    }
                }
                //var cursor = db.rawQuery("select * from accountsAndAmounts", null)
                //println(DatabaseUtils.dumpCursorToString(cursor))
                //cursor.close()
            }
        })
    }




    fun refresh(button:View){
        run("https://6007f1a4309f8b0017ee5022.mockapi.io/api/m1/accounts")
        //val db:SQLiteDatabase = getDb(pin)
        //val dataTmp : JSONArray? = getAllContentFromDb(db)
        //println("AAAAAAAAAAAAAAAAA : $dataTmp")
    }

    @SuppressLint("SetTextI18n")
    private fun displayAccountsTable(response : JSONArray?){

        Thread(Runnable {
            // performing some dummy time taking operation


            // try to touch View of UI thread
            this@HomeActivity.runOnUiThread(java.lang.Runnable {
                scrollView1.removeAllViews()
            })
        }).start()


        var t : TableLayout = TableLayout(this)

        for (i in 0 until response!!.length()) {
            val item = response.getJSONObject(i)

            var row : TableRow = TableRow(this)
            var breaker : TextView = TextView(this)
            breaker.text = "-----------------------------------------------------------"
            row.addView(breaker)
            t.addView(row)

            for (keyItem in item.keys()){
                if (keyItem!="id"){
                    val valueItem = item.get(keyItem)

                    var row : TableRow = TableRow(this)
                    var textRow : TextView = TextView(this)
                    textRow.text = "$keyItem : $valueItem\n"
                    row.addView(textRow)
                    t.addView(row)
                }

            }

        }

        Thread(Runnable {
            // performing some dummy time taking operation

            // try to touch View of UI thread
            this@HomeActivity.runOnUiThread(java.lang.Runnable {
                scrollView1.addView(t)
            })
        }).start()


    }

}


