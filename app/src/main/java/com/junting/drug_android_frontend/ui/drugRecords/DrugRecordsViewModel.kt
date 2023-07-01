package com.junting.drug_android_frontend.ui.drugRecords

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.junting.drug_android_frontend.model.drug_record.DrugRecord
import com.junting.drug_android_frontend.model.drug_record.InteractingDrug
import com.junting.drug_android_frontend.services.IDrugRecordService
import kotlinx.coroutines.launch

class DrugRecordsViewModel : ViewModel() {

    val records = MutableLiveData<List<DrugRecord>>()
    val record = MutableLiveData<DrugRecord>()

    fun setDrugName(name: String) {
        val info: DrugRecord = record.value!!
        info.drug.name = name
        triggerUpdate(info)
    }

    fun setHospitalName(name: String) {
        val info: DrugRecord = record.value!!
        info.hospital.name = name
        triggerUpdate(info)
    }

    fun setHospitalDepartment(name: String) {
        val info: DrugRecord = record.value!!
        info.hospital.department = name
        triggerUpdate(info)
    }

    fun setHospitalPhone(phone: String) {
        val info: DrugRecord = record.value!!
        info.hospital.phone = phone
        triggerUpdate(info)
    }
    fun setExtension(extension: String) {
        val info: DrugRecord = record.value!!
        info.hospital.extension = extension
        triggerUpdate(info)
    }

    fun setInteractingDrugs(interactingDrugs: List<InteractingDrug>) {
        val info: DrugRecord = record.value!!
        info.interactingDrugs = interactingDrugs
        triggerUpdate(info)
    }

    fun setIndication(indication: String) {
        val info: DrugRecord = record.value!!
        info.drug.indication = indication
        triggerUpdate(info)
    }
    fun setIndicationTag(indicationTag: String) {
        val info: DrugRecord = record.value!!
        info.indicationTag = indicationTag
        triggerUpdate(info)
    }

    fun setSideEffect(sideEffect: String) {
        val info: DrugRecord = record.value!!
        info.drug.sideEffect = sideEffect
        triggerUpdate(info)
    }

    fun setAppearance(appearance: String) {
        val info: DrugRecord = record.value!!
        info.drug.appearance = appearance
        triggerUpdate(info)
    }

    fun setOnDemand(onDemand: Boolean) {
        val info: DrugRecord = record.value!!
        info.onDemand = onDemand
        triggerUpdate(info)
    }

    fun setTimeSlots(timeSlots: List<String>) {
        val info: DrugRecord = record.value!!
        info.timeSlots = timeSlots
        triggerUpdate(info)
    }

    fun setTimings(timings: Set<Int>) {
        val info: DrugRecord = record.value!!
        info.timings = timings
        triggerUpdate(info)
    }

    fun setDosage(dosage: Int) {
        val info: DrugRecord = record.value!!
        info.dosage = dosage
        triggerUpdate(info)
    }

    fun setStock(stock: Int) {
        val info: DrugRecord = record.value!!
        info.stock = stock
        triggerUpdate(info)
    }

    fun setPosition(position: Int) {
        val info: DrugRecord = record.value!!
        info.position = position
        triggerUpdate(info)
    }
    fun setNotificationSettingStatus(status: Boolean) {
        val info: DrugRecord = record.value!!
        info.notificationSetting.status = status
        triggerUpdate(info)
    }
    fun setNotificationSettingStartDate(startDate: String) {
        val info: DrugRecord = record.value!!
        info.notificationSetting.startDate = startDate
        triggerUpdate(info)
    }
    fun setNotificationSettingRepeat(repeat: Int) {
        val info: DrugRecord = record.value!!
        info.notificationSetting.repeat = repeat
        triggerUpdate(info)
    }
    fun setReturnSettingStatus(status: Boolean) {
        val info: DrugRecord = record.value!!
        info.returnSetting.status = status
        triggerUpdate(info)
    }
    fun setReturnSettingDate(date: String) {
        val info: DrugRecord = record.value!!
        info.returnSetting.date = date
        triggerUpdate(info)
    }
    fun setReturnSettingLeft(left: Int) {
        val info: DrugRecord = record.value!!
        info.returnSetting.left = left
        triggerUpdate(info)
    }

    fun setRecords(newRecords: List<DrugRecord>) {
        records.value = newRecords
    }

    private fun triggerUpdate(newRecord: DrugRecord) {
        record.value = newRecord
    }

    fun fetchRecordsByAll() {
        viewModelScope.launch {
            val drugRecordService = IDrugRecordService.getInstance()
            try {
                records.value = drugRecordService.getDrugs()
            } catch (e: Exception) {
                Log.d("DrugsViewModel", "fetch takeRecords failed")
                Log.e("DrugsViewModel", e.toString())
            }
        }
    }

    fun fetchRecordsByHospital(hospitalName: String) {
        viewModelScope.launch {
            val drugRecordService = IDrugRecordService.getInstance()
            try {
                records.value = drugRecordService.getDrugs()
            } catch (e: Exception) {
                Log.d("DrugsViewModel", "fetch takeRecords failed")
                Log.e("DrugsViewModel", e.toString())
            }
        }
    }

    fun fetchRecordById(id: Int) {
        viewModelScope.launch {
            val drugRecordService = IDrugRecordService.getInstance()
            try {
                record.value = drugRecordService.getDrugById(id)
            } catch (e: Exception) {
                Log.d("EditDrugRecordViewModel", "fetch record id=${id} failed")
                Log.e("EditDrugRecordViewModel", e.toString())
            }
        }
    }
}