package com.junting.drug_android_frontend.services

import com.junting.drug_android_frontend.model.drug_interaction.InteractingDrug
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface IDrugInteractionService {
    @GET("interactingDrugs/")
    suspend fun getInteractingDrugs(): List<InteractingDrug>


    companion object {
        var drugInteractionService: IDrugInteractionService? = null
        fun getInstance(): IDrugInteractionService {
            if (drugInteractionService == null) {
                drugInteractionService = Retrofit.Builder()
                    .baseUrl("https://my-json-server.typicode.com/JunTingLin/drug-json-api-server/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(IDrugInteractionService::class.java)
            }
            return drugInteractionService!!
        }
    }
}