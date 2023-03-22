package com.junting.drug_android_frontend.ui.drugRecords

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.junting.drug_android_frontend.model.DrugRecord
import com.junting.drug_android_frontend.services.DrugService
import kotlinx.coroutines.launch

class DrugRecordsViewModel : ViewModel() {

    var records = MutableLiveData<List<DrugRecord>>()

    fun fetchRecords() {
        viewModelScope.launch {
            val drugService = DrugService.getInstance()
            try {
                records.value = drugService.getDrugs()
            } catch (e: Exception) {
                Log.d("DrugsViewModel", "fetch records failed")
                Log.e("DrugsViewModel", e.toString())
            }
        }
    }
}