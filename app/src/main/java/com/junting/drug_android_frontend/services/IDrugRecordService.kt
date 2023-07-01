package com.junting.drug_android_frontend.services

import com.junting.drug_android_frontend.constants.DataApiConstants
import com.junting.drug_android_frontend.model.drug_record.DrugRecord
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface IDrugRecordService {
    // 獲取全部 DrugRecords
    @GET("drugRecords/")
    suspend fun getDrugs(): List<DrugRecord>

    // 獲取指定 ID 的 DrugRecord
    @GET("drugRecords/{id}")
    suspend fun getDrugById(@Path("id") id: Int): DrugRecord

    // 更新指定 ID 的 DrugRecord
    @PUT("drugRecords/{id}")
    suspend fun updateDrugById(@Path("id") id: Int, @Body drugRecord: DrugRecord): Response<DrugRecord>

    // 獲取指定名稱的 DrugRecords
    @GET("drugRecords/")
    suspend fun getDrugByName(@Query("drug.name") name: String): List<DrugRecord>

    // 獲取指定 onDemand 的 DrugRecords
    @GET("drugRecords/")
    suspend fun getDrugsByOnDemand(@Query("onDemand") onDemand: Boolean): List<DrugRecord>

    companion object {
        var drugRecordService: IDrugRecordService? = null
        fun getInstance(): IDrugRecordService {
            if (drugRecordService == null) {
                drugRecordService = Retrofit.Builder()
                    .baseUrl(DataApiConstants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(IDrugRecordService::class.java)
            }
            return drugRecordService!!
        }
    }
}