package com.junting.drug_android_frontend.model

data class DrugRecord(
    val dosage: Int,
    val drug: Drug,
    val frequency: Int,
    val hospitalDepartment: String,
    val hospitalName: String,
    val id: Int,
    val interactingDrugs: List<InteractingDrugs>?,
    val notificationSetting: NotificationSetting,
    val onDemand: Boolean,
    val position: Int,
    val returnSetting: ReturnSetting,
    val stock: Int,
    val timeSlot: List<String>,
    val timings: List<Int>
)