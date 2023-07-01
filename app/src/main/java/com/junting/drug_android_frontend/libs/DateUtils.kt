package com.junting.drug_android_frontend.libs

import android.content.ContentValues.TAG
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


object DateUtils {
    fun calculateRemainingDate(
        timeSlots: List<String>,
        dosage: Int,
        stock: Int,
        left: Int,
    ): String {
        val remainingDays: Double

        val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

        // 獲取當前日期
        val currentDate = Date()
        Log.d(TAG, "calculateRemainingDate: $currentDate")

        // 計算剩餘天數
        if (timeSlots.isEmpty()) {
            return "無"
        }else if (dosage == 0 || (stock - left) <= 0){
            return dateFormat.format(currentDate)
        }
        else{
            remainingDays = (stock - left).toDouble() / (dosage * timeSlots.size)
        }


        // 使用 Calendar 進行日期計算
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        calendar.add(Calendar.DAY_OF_MONTH, remainingDays.toInt())

        // 格式化日期為字串
        val remainingDate = dateFormat.format(calendar.time)

        return remainingDate
    }
}
