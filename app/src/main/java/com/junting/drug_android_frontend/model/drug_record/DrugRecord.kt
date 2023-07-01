package com.junting.drug_android_frontend.model.drug_record

import java.io.Serializable

data class DrugRecord(
    var dosage: Int = 0,
    var drug: Drug = Drug(),
    var frequency: Int = 0,
    var hospital: Hospital = Hospital(),
    var id: Int = 0,
    var interactingDrugs: List<InteractingDrug>? = emptyList(),
    var notificationSetting: NotificationSetting = NotificationSetting(),
    var indicationTag:String = "",
    var onDemand: Boolean = false,
    var position: Int = 0,
    var returnSetting: ReturnSetting = ReturnSetting(),
    var stock: Int = 0,
    var timeSlots: List<String> = emptyList(),
    var timings: Set<Int> = emptySet()
): Serializable