package com.kongjak.ardionoledcontroller

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import dev.sasikanth.colorsheet.ColorSheet
import dev.sasikanth.colorsheet.utils.ColorSheetUtils


class MainActivity : AppCompatActivity() {

    var btnSend: Button? = null

    var ledColor: String = "#ffffff"

    private lateinit var btService: BTService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnSend = findViewById(R.id.btn_send)

        init()
    }

    fun onClickButtonSend(view: View?) {
        ledOn()
    }

    fun onClickButtonPair(view: View) {
        val intent = Intent(this, ConnectActivity::class.java)
        startActivity(intent)
    }

    fun onClickButtonChoose(view: View) {
        val colors = resources.getIntArray(R.array.colors)

        ColorSheet().colorPicker(
            colors = colors,
            listener = { color ->
                ledColor = ColorSheetUtils.colorToHex(color)
                ledOn()
            })
            .show(supportFragmentManager)
    }

    private fun ledOn() {
        btService.startBt()
        btService.writeBt(ledColor)
    }

    fun init() {
        val sp = getSharedPreferences("btDevice", MODE_PRIVATE)
        val address = sp.getString("device", "00:00:00:00:00:00")

        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val device: BluetoothDevice = bluetoothAdapter.getRemoteDevice(address)

        btService = BTService(device)
        btService.btSocketStart()

    }

    override fun onDestroy() {
        super.onDestroy()
        if (btService.isConnected()) {
            btService.close()
        }
    }
}