package com.example.basekotlin.all.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LanguageModel(
    var name: String, var code: String, var active: Boolean
) : Parcelable

@Parcelize
data class HistoryModel(
    var id: Int,
    var value: String,
    var unit: String,
    var sugarTarget: String,
    var condition: String,
    var time: String,
) : Parcelable

@Parcelize
data class InformationModel(
    var id: Int,
    var image: Int,
    var title: String,
    var content: String,
    var link: String
) : Parcelable