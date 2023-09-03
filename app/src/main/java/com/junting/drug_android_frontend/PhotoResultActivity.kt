package com.junting.drug_android_frontend;

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.junting.drug_android_frontend.databinding.ActivityPhotoResultBinding
import com.junting.drug_android_frontend.libs.BitmapUtils
import com.junting.drug_android_frontend.libs.SharedPreferencesManager
import com.junting.drug_android_frontend.model.UglyText
import com.junting.drug_android_frontend.services.CloudVisionService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PhotoResultActivity: AppCompatActivity() {

    private lateinit var binding: ActivityPhotoResultBinding
    private val viewModel: CloudVisionViewModel = CloudVisionViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val imageBase64 = PhotoTakeActivity.photoResultBase64
        binding.ivNormalize.setImageBitmap(BitmapUtils.base64ToBitmap(imageBase64))

        val fakeFlag = SharedPreferencesManager(this).getFakeFlag()
        if(fakeFlag) {
            GlobalScope.launch(Dispatchers.Main) {
                delay(2000)  // 等待 2 秒
                viewModel.text.value = "any data you want to fake"
            }
        } else {
            viewModel.recognize(imageBase64)  // 真的去辨識圖片
        }


        this.viewModel.text.observe(this, Observer {
            // handle your text result there
//            Toast.makeText(this, viewModel.text.value, Toast.LENGTH_SHORT).show()
            Toast.makeText(this, "文字擷取成功", Toast.LENGTH_SHORT).show()
            binding.progressBar.visibility = android.view.View.GONE
            binding.tvLoading.visibility = android.view.View.GONE
            binding.btnConfirm.isEnabled = true
        })
        binding.btnConfirm.setOnClickListener() {
            val intent = Intent(this, DrugbagInfoActivity::class.java)
            intent.putExtra("UglyTextString", viewModel.text.value)
            startActivity(intent)
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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
                    Log.d("CloudVisionViewModel", text.value!!)
                } catch (e: Exception) {
                    Log.d("CloudVisionViewModel", "image recognize failed")
                    Log.e("CloudVisionViewModel", e.toString())
                    Log.e("CloudVisionViewModel", e.message!!)
                }
            }
        }
    }
}
