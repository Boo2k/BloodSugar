package com.example.basekotlin.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Convert {
    @TypeConverter
    fun toListNote(value: String): List<Note>? {
        val listType = object : TypeToken<List<Note>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromListNote(date: List<Note>?): String? {
        return Gson().toJson(date)
    }

    @TypeConverter
    fun fromTargetRange(value: TargetRange): String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toTargetRange(value: String): TargetRange {
        return Gson().fromJson(value, TargetRange::class.java)
    }
}