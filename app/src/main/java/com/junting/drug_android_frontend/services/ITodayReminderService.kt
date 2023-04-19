package com.junting.drug_android_frontend.services

import com.junting.drug_android_frontend.model.today_reminder.TodayReminder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ITodayReminderService {
    @GET("todayReminders/")
    suspend fun getTodayReminders(): List<TodayReminder>


    companion object {
        var todayReminderService: ITodayReminderService? = null
        fun getInstance(): ITodayReminderService {
            if (todayReminderService == null) {
                todayReminderService = Retrofit.Builder()
                    .baseUrl("https://my-json-server.typicode.com/JunTingLin/drug-json-api-server/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(ITodayReminderService::class.java)
            }
            return todayReminderService!!
        }
    }
}