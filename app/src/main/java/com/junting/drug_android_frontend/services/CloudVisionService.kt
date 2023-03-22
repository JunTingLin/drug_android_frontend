package com.junting.drug_android_frontend.services

import com.junting.drug_android_frontend.model.cloud_vision.ImageAnnotateResponse
import okhttp3.MediaType
import okhttp3.RequestBody

class CloudVisionService(service: ICloudVisionService) {

    private val service: ICloudVisionService

    init {
        this.service = service
    }

    suspend fun imageAnnotate(imageBase64: String): ImageAnnotateResponse {
        val json = String.format(
            """{
                "requests": [
                    {
                        "features": [
                            {
                              "maxResults": 50,
                              "model": "builtin/latest",
                              "type": "DOCUMENT_TEXT_DETECTION"
                            }
                        ],
                        "image": {
                            "content": "%s"
                        }
                    }
                ]
            }""", imageBase64
        )
        val body = RequestBody.create(MediaType.parse("application/json"), json)
        return service.imageAnnotate(body)
    }

    companion object {
        var cloudVisionService: CloudVisionService? = null
        fun getInstance(): CloudVisionService {
            if (cloudVisionService == null) {
                cloudVisionService = CloudVisionService(ICloudVisionService.getInstance())
            }
            return cloudVisionService!!
        }
    }
}
