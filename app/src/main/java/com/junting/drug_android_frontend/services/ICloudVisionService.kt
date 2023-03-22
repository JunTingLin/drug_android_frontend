package com.junting.drug_android_frontend.services

import com.junting.drug_android_frontend.BuildConfig
import com.junting.drug_android_frontend.model.cloud_vision.ImageAnnotateResponse
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface ICloudVisionService {
    @POST("./images:annotate")
    suspend fun imageAnnotate(@Body params: RequestBody): ImageAnnotateResponse

    companion object {
        var cloudVisionService: ICloudVisionService? = null
        fun getInstance(): ICloudVisionService {
            if (cloudVisionService == null) {
                cloudVisionService = Retrofit.Builder()
                    .baseUrl("https://vision.googleapis.com/v1/")
                    .client(
                        OkHttpClient().newBuilder()
                            .addInterceptor { chain ->
                                val url = chain
                                    .request()
                                    .url()
                                    .newBuilder()
                                    // Change the GCP key here
                                    .addQueryParameter("key", BuildConfig.Cloud_Vision_API)
                                    .build()
                                chain.proceed(chain.request().newBuilder().url(url).build())
                            }
                            .build()
                    )
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(ICloudVisionService::class.java)
            }
            return cloudVisionService!!
        }
    }
}
