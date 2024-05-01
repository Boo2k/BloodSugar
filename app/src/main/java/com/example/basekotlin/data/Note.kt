package com.example.basekotlin.data

import com.example.basekotlin.util.NoteType

data class Note(
    var name: String? = null,
    var order: Int? = null,
    var isSelected: Boolean? = null,
    var type: NoteType? = null
)