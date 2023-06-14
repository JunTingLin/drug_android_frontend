package com.junting.drug_android_frontend.services

import com.junting.drug_android_frontend.constants.DataApiConstants
import com.junting.drug_android_frontend.model.TakeRecord.TakeRecord
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ITakeRecordService {
    @GET("takeRecords/")
    suspend fun getTakeRecords(): List<TakeRecord>

    @GET("takeRecords")
    suspend fun getTakeRecordsByDate(
        @Query("date") date: String,
        @Query("status") status: Int
    ): List<TakeRecord>


    companion object {
        var takeRecordService: ITakeRecordService? = null
        fun getInstance(): ITakeRecordService {
            if (takeRecordService == null) {
                takeRecordService = Retrofit.Builder()
                    .baseUrl(DataApiConstants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(ITakeRecordService::class.java)
            }
            return takeRecordService!!
        }
    }
}