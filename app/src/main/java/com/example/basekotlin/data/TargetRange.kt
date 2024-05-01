package com.example.basekotlin.data

data class TargetRange(
    var condition: Condition? = null,
    var low: Float? = null,
    var normal: Float? = null,
    var preDiabetes: Float? = null,
    var status: Int? = null,
)