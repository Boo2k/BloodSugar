package com.example.basekotlin.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

object Utils {
    const val STORAGE = "STORAGE"

    const val UNIT = "UNIT"

    //Key data
    const val KEY_CONDITION =  "KEY_CONDITION"
    const val KEY_TYPE_TARGET_RANGE =  "KEY_TYPE_TARGET_RANGE"
    const val KEY_NEW_RECORD =  "KEY_NEW_RECORD"
    const val KEY_EDIT_RECORD =  "KEY_EDIT_RECORD"
    const val KEY_LIST_NOTE =  "KEY_LIST_NOTE"
    const val KEY_INFORMATION =  "KEY_INFORMATION"
    const val KEY_ID_HISTORY =  "KEY_ID_HISTORY"

}

enum class NoteType {
    NORMAL, EDIT
}

enum class SugarTargetType {
    LOW, NORMAL, PRE_DIABETES, DIABETES
}

enum class ConditionId {
    Condition01, Condition02, Condition03, Condition04, Condition05, Condition06, Condition07, Condition08
}

@SuppressLint("SimpleDateFormat")
fun convertTimeToDate(calendar: Calendar?): Date? {
    return try {
        val format1 = SimpleDateFormat("yyyy/MM/dd")
        calendar?.let { convertTimeToString(it)?.let { format1.parse(it) } }
    } catch (ex: Exception) {
        null
    }
}

@SuppressLint("SimpleDateFormat")
fun convertTimeToString(calendar: Calendar): String? {
    return try {
        val format1 = SimpleDateFormat("yyyy/MM/dd")
        format1.format(calendar.time)
    } catch (ex: java.lang.Exception) {
        ""
    }
}