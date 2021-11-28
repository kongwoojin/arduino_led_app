package com.kongjak.ardionoledcontroller

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {


    var btnSend: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnSend = findViewById(R.id.btn_send)

    }

    fun onClickButtonSend(view: View?) {
        val preferences = getSharedPreferences("pref", Context.MODE_PRIVATE)
        val jsonData = preferences.getString("person", "")

        val gson = Gson()

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun onClickButtonPair(view: View) {
        val intent = Intent(this, ConnectActivity::class.java)
        startActivity(intent)
    }
}