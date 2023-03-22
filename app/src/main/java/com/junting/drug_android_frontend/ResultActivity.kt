package com.junting.drug_android_frontend;

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dynamsoft.core.CoreException
import com.junting.drug_android_frontend.services.CloudVisionService
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class ResultActivity: AppCompatActivity() {

    private val viewModel: CloudVisionViewModel = CloudVisionViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val ivNormalize: ImageView = findViewById(R.id.iv_normalize);

        try {
            val bitmap = QuadEditActivity.mNormalizedImageResult.image.toBitmap()
            viewModel.recognize(toBase64(bitmap))
            ivNormalize.setImageBitmap(QuadEditActivity.mNormalizedImageResult.image.toBitmap())
        } catch (e: CoreException) {
            e.printStackTrace()
        }

        this.viewModel.text.observe(this, Observer {
            // handle your text result there
            Toast.makeText(this, viewModel.text.value, Toast.LENGTH_SHORT).show()
        })
    }

    private fun toBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    class CloudVisionViewModel: ViewModel() {

        var text = MutableLiveData<String>()

        fun recognize(imageBase64: String) {
            viewModelScope.launch {
                val service = CloudVisionService.getInstance()
                try {
                    val response = service.imageAnnotate(imageBase64).responses.get(0)
                    // recognize results is in response.textAnnotations list
                    // index 0 is the combined results of all identified texts
                    text.value = if (response.textAnnotations != null)
                        response.textAnnotations.get(0).description
                        else "no text"
                    Log.d("DrugsViewModel", text.value!!)
                } catch (e: Exception) {
                    Log.d("DrugsViewModel", "fetch records failed")
                    Log.e("DrugsViewModel", e.toString())
                    Log.e("DrugsViewModel", e.message!!)
                }
            }
        }
    }
}
