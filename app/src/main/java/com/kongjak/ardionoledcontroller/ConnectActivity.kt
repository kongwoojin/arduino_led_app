package com.kongjak.ardionoledcontroller

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.kongjak.ardionoledcontroller.databinding.ActivityConnectBinding
import java.util.*
import kotlin.collections.ArrayList
import android.content.Context.MODE_PRIVATE

import android.content.SharedPreferences




class ConnectActivity : AppCompatActivity() {

    private val REQUEST_ENABLE_BT = 1

    lateinit var bluetoothAdapter: BluetoothAdapter

    var uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")

    var btArrayAdapter: ArrayAdapter<String>? = null
    var deviceAddressArray: ArrayList<String>? = null


    lateinit var listView: ListView

    private lateinit var binding: ActivityConnectBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityConnectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listView = findViewById(R.id.listview)

        listView.onItemClickListener = myOnItemClickListener()

        val permission_list = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        ActivityCompat.requestPermissions(this, permission_list, 1)

        setSupportActionBar(binding.toolbar)

        binding.fab.setOnClickListener {
            Log.d("Test", "Searching...")
            search()
        }

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }


        btArrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1)
        deviceAddressArray = ArrayList()
        listView.adapter = btArrayAdapter
    }

    private fun search() {
        btArrayAdapter!!.clear()
        if (deviceAddressArray != null && !deviceAddressArray!!.isEmpty()) {
            deviceAddressArray!!.clear()
        }
        val pairedDevices = bluetoothAdapter.bondedDevices
        if (pairedDevices.isNotEmpty()) {
            // There are paired devices. Get the name and address of each paired device.
            for (device in pairedDevices) {
                val deviceName = device.name
                val deviceHardwareAddress = device.address // MAC address
                btArrayAdapter!!.add(deviceName)
                deviceAddressArray!!.add(deviceHardwareAddress)
            }
        }
    }

    inner class myOnItemClickListener : AdapterView.OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
            Toast.makeText(
                applicationContext,
                btArrayAdapter!!.getItem(position),
                Toast.LENGTH_SHORT
            ).show()
            val name: String? = btArrayAdapter!!.getItem(position) // get name
            val address: String = deviceAddressArray!![position] // get address
            val device: BluetoothDevice = bluetoothAdapter.getRemoteDevice(address)

            Log.d("Test", device.toString())

            val btService = BTService(device)
            btService.btSocketStart()
            btService.startBt()

            val sp = getSharedPreferences("btDevice", MODE_PRIVATE)
            val editor = sp.edit()
            editor.putString("device", address)
            editor.apply()

            Toast.makeText(baseContext, name, Toast.LENGTH_SHORT).show()
        }
    }
}