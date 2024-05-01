package com.example.basekotlin.data.history

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.basekotlin.data.Note
import com.example.basekotlin.data.TargetRange

@TypeConverters
@Entity(tableName = "History")
data class History(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "id_config") var idConfig: String? = null,
    @ColumnInfo(name = "valueChart") var valueChart: Float? = null,
    @ColumnInfo(name = "valueInput") var valueInput: Float? = null,
    @ColumnInfo(name = "time") var timeDate: Long? = null,
    @ColumnInfo(name = "notes") var notes: List<Note>? = null,
    @ColumnInfo(name = "targetRange") var targetRange: TargetRange? = null,
    @ColumnInfo(name = "sugarTarget") var sugarTarget: String? = null,
)