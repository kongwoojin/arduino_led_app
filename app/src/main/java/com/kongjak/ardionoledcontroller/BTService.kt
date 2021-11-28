package com.kongjak.ardionoledcontroller

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.util.*

class BTService(private var device: BluetoothDevice) {
    private lateinit var connectedThread: ConnectedThread
    lateinit var btSocket: BluetoothSocket

    fun btSocketStart() {
        val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")

        // create & connect socket
        try {
            btSocket = device.createRfcommSocketToServiceRecord(uuid)
            btSocket.connect()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun startBt() {
        connectedThread = ConnectedThread(btSocket)
        connectedThread.start()
    }

    fun writeBt(hex: String) {
        connectedThread = ConnectedThread(btSocket)
        connectedThread.write(hex)
    }

    fun close() {
        connectedThread = ConnectedThread(btSocket)
        connectedThread.cancel()
    }


    fun isConnected(): Boolean {
        connectedThread = ConnectedThread(btSocket)
        return connectedThread.isConnected()
    }
}