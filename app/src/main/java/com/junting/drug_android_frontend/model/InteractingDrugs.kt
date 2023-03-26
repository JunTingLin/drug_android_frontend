package com.junting.drug_android_frontend.model

class InteractingDrugs(
    val interactionDrugs: List<InteractionDrugs>
)
data class InteractionDrugs(
    val cause: String,
    val id: Int,
    val level: String,
    val drug: DrugInfo
)
data class DrugInfo(
    val id: Int,
    val name: String
    )