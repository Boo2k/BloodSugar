package com.example.basekotlin.data.history

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.basekotlin.data.TargetRange

@Dao
interface HistoryDao {
    @Query("SELECT * FROM History")
    fun getAllHistory(): List<History>

    @Insert
    fun insertHistory(value: History)

    @Query("DELETE FROM History")
    fun clearHistory()

    @Query("SELECT * FROM History WHERE valueChart = :valueChart AND valueInput = :valueInput AND time = :time AND id_config = :idConfig")
    fun checkUser(
        valueChart: Float? = null,
        valueInput: Float? = null,
        time: Long? = null,
        idConfig: String? = null
    ): List<History>

    @Query("UPDATE History SET targetRange = :target WHERE  id_config= :idConfig ")
    fun updateTarget(
        idConfig: String? = null,
        target: TargetRange? = null
    )

    @Update
    fun updateHistory(history: History)

    @Delete
    fun deleteHistory(history: History)
}