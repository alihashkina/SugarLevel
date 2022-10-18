package com.example.sugarlevel.adapters.dataClass

//карточки для статистики
data class Card (
    val cardDate: String?,
    val cardHealthy: String?,
    val cardUnhealthy: String?,
    val cardSymptoms: String?,
    val cardCare: String?,
    val cardSugar: String?,
    val cardSugarml: String?,
    val cardOther: String?,
    var id: Int
        )