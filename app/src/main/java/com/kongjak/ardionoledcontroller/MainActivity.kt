package com.kongjak.ardionoledcontroller

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.snackbar.Snackbar
import dev.sasikanth.colorsheet.ColorSheet
import dev.sasikanth.colorsheet.utils.ColorSheetUtils

class MainActivity : AppCompatActivity() {

    var ledColor: String = "#FFFFFF"
    var isConnected: Boolean = false
    lateinit var sp: SharedPreferences
    lateinit var address: String
    var isBTServiceInitialized: Boolean = false

    private lateinit var btService: BTService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sp = getSharedPreferences("btDevice", MODE_PRIVATE)
        address = sp.getString("device", "00:00:00:00:00:00").toString()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    fun onClickButtonChoose(view: View) {
        val colors = resources.getIntArray(R.array.colors)

        ColorSheet().colorPicker(
            colors = colors,
            listener = { color ->
                ledColor = ColorSheetUtils.colorToHex(color)
                Log.d("Test", "Color Set to " + ColorSheetUtils.colorToHex(color))
                if (this::btService.isInitialized) {
                    ledOn()
                    Log.d("Test", ledColor)
                } else {
                    Snackbar.make(view, "Connect Lamp First!", Snackbar.LENGTH_SHORT).show()
                }
            })
            .show(supportFragmentManager)
    }

    fun onClickButtonOff(view: View) {
        if (isBTServiceInitialized) {
            ledOff()
        } else {
            Snackbar.make(view, "Connect Lamp First!", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun ledOn() {
        btService.startBt()
        btService.writeBt(ledColor)
    }

    private fun ledOff() {
        btService.startBt()
        btService.writeBt("off")
    }


    private fun connect() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val device: BluetoothDevice = bluetoothAdapter.getRemoteDevice(address)

        btService = BTService(device)
        btService.btSocketStart()
        isBTServiceInitialized = !isBTServiceInitialized
    }

    private fun disconnect() {
        if (btService.isConnected()) {
            btService.close()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disconnect()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            val intent = Intent(this, ConnectActivity::class.java)
            startActivity(intent)
            true
        }

        R.id.action_connect -> {
            if (isConnected) {
                disconnect()
                isConnected = !isConnected
                item.icon = resources.getDrawable(R.drawable.ic_bluetooth, null)
            } else if (!isConnected) {
                connect()
                ledOn()
                isConnected = !isConnected
                item.icon = resources.getDrawable(R.drawable.ic_bluetooth_connected, null)
            }
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        address = sp.getString("device", "00:00:00:00:00:00").toString()
    }
}