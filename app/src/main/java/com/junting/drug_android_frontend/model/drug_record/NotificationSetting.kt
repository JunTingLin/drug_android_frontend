package com.junting.drug_android_frontend.model.drug_record

import java.io.Serializable

data class NotificationSetting(
    var remind: Int = 0,
    var repeat: Int = 0,
    var startDate: String = "yyyy/MM/dd",
    var status: Boolean = false
): Serializable