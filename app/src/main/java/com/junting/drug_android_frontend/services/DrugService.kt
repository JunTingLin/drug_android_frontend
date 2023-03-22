package com.junting.drug_android_frontend.services

import com.junting.drug_android_frontend.model.DrugRecord
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface DrugService {
    @GET("drugRecords/")
    suspend fun getDrugs(): List<DrugRecord>

    companion object {
        var drugService: DrugService? = null
        fun getInstance(): DrugService {
            if (drugService == null) {
                drugService = Retrofit.Builder()
                    .baseUrl("https://my-json-server.typicode.com/JunTingLin/drug-json-api-server/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(DrugService::class.java)
            }
            return drugService!!
        }
    }
}