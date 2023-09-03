package com.junting.drug_android_frontend.services

import com.junting.drug_android_frontend.constants.DataApiConstants
import com.junting.drug_android_frontend.model.UglyText
import com.junting.drug_android_frontend.model.drugbag_info.DrugbagInformation
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface IDrugbagGetService {
    @GET("drugBagInformation/10/")
    suspend fun getDrugbagInfo(): DrugbagInformation

    companion object {
        var drugRecordGetService: IDrugbagGetService? = null
        fun getInstance(): IDrugbagGetService {
            if (drugRecordGetService == null) {
                drugRecordGetService = Retrofit.Builder()
                    .baseUrl(DataApiConstants.BASE_URL_HEROKU)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(IDrugbagGetService::class.java)
            }
            return drugRecordGetService!!
        }
    }
}

interface IDrugbagPostService {
    @POST("process_string")
    suspend fun postDrugInfo(@Body params: UglyText): DrugbagInformation

    companion object {
        var drugRecordPostService: IDrugbagPostService? = null
        fun getInstance(): IDrugbagPostService {
            if (drugRecordPostService == null) {
                drugRecordPostService = Retrofit.Builder()
                    .baseUrl(DataApiConstants.BASE_URL_SCHOOL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(IDrugbagPostService::class.java)
            }
            return drugRecordPostService!!
        }
    }
}