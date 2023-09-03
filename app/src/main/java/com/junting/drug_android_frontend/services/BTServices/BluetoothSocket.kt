package com.junting.drug_android_frontend.services.BTServices

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class BluetoothSocket {

    val ss = SerialSocket()
    init {
        GlobalScope.launch(Dispatchers.IO) {
            ss.run()
        }
    }

    suspend fun openPillbox(cellPosition: String) = withContext(Dispatchers.IO) {
        val message = when (cellPosition) {
            "7" -> "b"
            "8" -> "d"
            "9" -> "f"
            "4" -> "h"
            "5" -> "j"
            "6" -> "l"
            "1" -> "n"
            "2" -> "p"
            "3" -> "r"
            else -> ""
        }
        try {
            val messageBytes = message.toByteArray()
            ss.write(messageBytes)
        } catch (e: IOException) {
            e.printStackTrace()
//            throw e  // 重新拋出異常
        }
    }

    suspend fun closePillbox(cellPosition: String) = withContext(Dispatchers.IO) {
        val message = when (cellPosition) {
            "7" -> "a"
            "8" -> "c"
            "9" -> "e"
            "4" -> "g"
            "5" -> "i"
            "6" -> "k"
            "1" -> "m"
            "2" -> "o"
            "3" -> "q"
            else -> ""
        }
        try {
            val messageBytes = message.toByteArray()
            ss.write(messageBytes)
            ss.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    suspend fun closeMultiplePillbox(arr: IntArray) = withContext(Dispatchers.IO) {
        for (i in arr) {
            val position = i.toString()
            val message = when (position) {
                "7" -> "a"
                "8" -> "c"
                "9" -> "e"
                "4" -> "g"
                "5" -> "i"
                "6" -> "k"
                "1" -> "m"
                "2" -> "o"
                "3" -> "q"
                else -> ""
            }
            try {
                val messageBytes = message.toByteArray()
                ss.write(messageBytes)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        ss.close()
    }
}
