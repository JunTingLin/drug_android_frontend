package com.junting.drug_android_frontend.services.BTServices

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID

class SerialSocket {

    companion object {
        private val BLUETOOTH_SPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }

    var socket: BluetoothSocket? = null

    suspend fun write(data: ByteArray) = withContext(Dispatchers.IO) {
        if (socket?.isConnected == true) {
            socket?.getOutputStream()?.write(data)
        } else {
            // Handle the error here
            throw IOException("BluetoothSocket is not connected")
        }
    }

    suspend fun close() = withContext(Dispatchers.IO) {
        try {
            socket?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun run() = withContext(Dispatchers.IO) {
        try {
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            val device = bluetoothAdapter.getRemoteDevice("78:21:84:8C:A6:92") //MAC address of the device(old)
            socket = device.createRfcommSocketToServiceRecord(BLUETOOTH_SPP)
            socket?.connect()
        } catch (e: Exception) {
            socket = null
            e.printStackTrace()
        }
    }
}
