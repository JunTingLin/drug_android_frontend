package com.junting.drug_android_frontend

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.junting.drug_android_frontend.model.UglyText
import com.junting.drug_android_frontend.model.drug_record.DrugRecord
import com.junting.drug_android_frontend.model.drugbag_info.DrugbagInformation
import com.junting.drug_android_frontend.services.IDrugbagGetService
import com.junting.drug_android_frontend.services.IDrugbagPostService
import kotlinx.coroutines.launch

class DrugbagInfoViewModel : ViewModel() {

    val drugbagInfo = MutableLiveData<DrugbagInformation>()

    fun setDrugName(name: String) {
        val info: DrugbagInformation = drugbagInfo.value!!
        info.drug.name = name
        triggerUpdate(info)
    }

    fun setHospitalName(name: String) {
        val info: DrugbagInformation = drugbagInfo.value!!
        info.hospital.name = name
        triggerUpdate(info)
    }
    fun setDepartmentName(name: String) {
        val info: DrugbagInformation = drugbagInfo.value!!
        info.hospital.department = name
        triggerUpdate(info)
    }
    fun setHospitalPhone(phone: String) {
        val info: DrugbagInformation = drugbagInfo.value!!
        info.hospital.phone = phone
        triggerUpdate(info)
    }
    fun setExtension(Extension: String) {
        val info: DrugbagInformation = drugbagInfo.value!!
        info.hospital.extension = Extension
        triggerUpdate(info)
    }
    fun setIndication(indication: String) {
        val info: DrugbagInformation = drugbagInfo.value!!
        info.drug.indication = indication
        triggerUpdate(info)
    }
    fun setSideEffect(sideEffect: String) {
        val info: DrugbagInformation = drugbagInfo.value!!
        info.drug.sideEffect = sideEffect
        triggerUpdate(info)
    }
    fun setAppearance(appearance: String) {
        val info: DrugbagInformation = drugbagInfo.value!!
        info.drug.appearance = appearance
        triggerUpdate(info)
    }
    fun setOnDemand(onDemand: Boolean) {
        val info: DrugbagInformation = drugbagInfo.value!!
        info.onDemand = onDemand
        triggerUpdate(info)
    }
    fun setStock(stockStr: String) {
        val info: DrugbagInformation = drugbagInfo.value!!
        val stock: Int? = stockStr.toIntOrNull()
        if (stock != null) {
            info.stock = stock
        }
        triggerUpdate(info)
    }
    fun setTimings(timings: Set<Int>) {
        val info: DrugbagInformation = drugbagInfo.value!!
        info.timings = timings
        triggerUpdate(info)
    }
    fun addTiming(timing: Int) {
        val info: DrugbagInformation = drugbagInfo.value!!
        info.timings = info.timings.plus(timing)
        triggerUpdate(info)
    }
    fun removeTiming(timing: Int) {
        val info: DrugbagInformation = drugbagInfo.value!!
        info.timings = info.timings.minus(timing)
        triggerUpdate(info)
    }
    private fun triggerUpdate(newDrugbagInfo: DrugbagInformation) {
        drugbagInfo.value = newDrugbagInfo
        Log.d("DrugbagInfoViewModel", "triggerUpdate: ${drugbagInfo.value}")
    }


    fun fetchDrugbagInfo() {

        viewModelScope.launch {
            val service = IDrugbagGetService.getInstance()
            try {
                drugbagInfo.value = service.getDrugbagInfo()
            }catch (e: Exception) {
                Log.e("DrugbagInfoViewModel", "Error: ${e.message}")
            }
        }
    }
    fun sendDrugbagInfo(uglyText: UglyText) {
        viewModelScope.launch {
            val service = IDrugbagPostService.getInstance()
            try {
                drugbagInfo.value = service.postDrugInfo(uglyText)
            }catch (e: Exception) {
                Log.e("DrugbagInfoViewModel", "Error: ${e.message}")
            }
        }
    }
}