package com.junting.drug_android_frontend

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.junting.drug_android_frontend.model.drug_record.DrugRecord
import com.junting.drug_android_frontend.model.today_reminder.TodayReminder
import com.junting.drug_android_frontend.services.IDrugRecordService
import kotlinx.coroutines.launch

class OnDemandViewModel : ViewModel() {

    var drugRecor = MutableLiveData<DrugRecord>()
    var drugRecors = MutableLiveData<List<DrugRecord>>()
    var onDemandDrugRecors = MutableLiveData<List<DrugRecord>>()
    val actualTakingTime  = ObservableField<String>()

    fun setDosage(dosage: Int) {
        val info: DrugRecord = drugRecor.value!!
        info.dosage = dosage
        triggerUpdate(info)
    }

    fun setActualTakingTime(actualTakingTime: String) {
        this.actualTakingTime.set(actualTakingTime)
    }

    private fun triggerUpdate(newDrugRecord: DrugRecord) {
        drugRecor.value = newDrugRecord
    }

    fun fetchDrugRecordById(Id:Int) {
        viewModelScope.launch {
            val drugRecordService = IDrugRecordService.getInstance()
            try {
                drugRecor.value = drugRecordService.getDrugById(Id)
            } catch (e: Exception) {
                Log.d("OnDemandViewModel", "fetch drugRecord failed")
                Log.e("OnDemandViewModel", e.toString())
            }
        }
    }
    fun fetchDrugRecords() {
        viewModelScope.launch {
            val drugRecordService = IDrugRecordService.getInstance()
            try {
                drugRecors.value = drugRecordService.getDrugs()
            } catch (e: Exception) {
                Log.d("OnDemandViewModel", "fetch drugRecords failed")
                Log.e("OnDemandViewModel", e.toString())
            }
        }
    }
    fun fetchDrugRecordByOnDemand(onDemand: Boolean) {
        viewModelScope.launch {
            val drugRecordService = IDrugRecordService.getInstance()
            try {
                onDemandDrugRecors.value = drugRecordService.getDrugsByOnDemand(onDemand)
            } catch (e: Exception) {
                Log.d("OnDemandViewModel", "fetch onDemandDrugRecors failed")
                Log.e("OnDemandViewModel", e.toString())
            }
        }
    }

}