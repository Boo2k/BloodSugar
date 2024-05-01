package com.example.basekotlin.util

import android.content.Context
import com.example.basekotlin.R
import com.example.basekotlin.data.Condition
import com.example.basekotlin.data.Database
import com.example.basekotlin.data.Note
import com.example.basekotlin.data.TargetRange
import com.example.basekotlin.data.TypeTargetRange
import com.example.basekotlin.data.history.History
import com.google.gson.Gson

object DatabaseManager {

    private var database: Database? = null

    fun initDatabase(context: Context) {
        database = Database.getInstance(context)
    }

    fun getAllHistory(): List<History> {
        return database?.historyDao()!!.getAllHistory()
    }

    fun checkRecordExists(history: History): List<History>? {
        return database?.historyDao()
            ?.checkUser(history.valueChart, history.valueInput, history.timeDate, history.idConfig)
    }

    fun addHistory(history: History) {
        database?.historyDao()?.insertHistory(history)
    }

    fun deleteHistory(history: History) {
        database?.historyDao()?.deleteHistory(history)
    }

    fun updateHistory(history: History) {
        database?.historyDao()?.updateHistory(history)
    }

    fun getListCondition(context: Context): ArrayList<Condition> {
        val listCondition = ArrayList<Condition>()
        listCondition.clear()
        listCondition.add(
            Condition(
                ConditionId.Condition01.name, context.getString(R.string.All_type)
            )
        )
        listCondition.add(
            Condition(
                ConditionId.Condition02.name, context.getString(R.string.Default)
            )
        )
        listCondition.add(
            Condition(
                ConditionId.Condition03.name, context.getString(R.string.Fasting)
            )
        )
        listCondition.add(
            Condition(
                ConditionId.Condition04.name, context.getString(R.string.Before_a_meal)
            )
        )
        listCondition.add(
            Condition(
                ConditionId.Condition05.name, context.getString(R.string.After_a_meal_1_2h)
            )
        )
        listCondition.add(
            Condition(
                ConditionId.Condition06.name, context.getString(R.string.Asleep)
            )
        )
        listCondition.add(
            Condition(
                ConditionId.Condition07.name, context.getString(R.string.Before_exercise)
            )
        )
        listCondition.add(
            Condition(
                ConditionId.Condition08.name, context.getString(R.string.After_exercise)
            )
        )
        return listCondition
    }

    fun getListTargetRange(context: Context): ArrayList<TargetRange> {
        val listTarget = ArrayList<TargetRange>()
        listTarget.clear()
        listTarget.add(
            TargetRange(
                Condition(ConditionId.Condition01.name, context.getString(R.string.All_type)),
                4.0f,
                5.5f,
                7.0f,
                1
            )
        )
        listTarget.add(
            TargetRange(
                Condition(ConditionId.Condition02.name, context.getString(R.string.Default)),
                4.0f,
                5.5f,
                7.0f,
                1
            )
        )
        listTarget.add(
            TargetRange(
                Condition(ConditionId.Condition03.name, context.getString(R.string.Fasting)),
                4.0f,
                5.5f,
                7.0f,
                0
            )
        )
        listTarget.add(
            TargetRange(
                Condition(ConditionId.Condition04.name, context.getString(R.string.Before_a_meal)),
                4.0f,
                5.5f,
                7.0f,
                0
            )
        )
        listTarget.add(
            TargetRange(
                Condition(
                    ConditionId.Condition05.name,
                    context.getString(R.string.After_a_meal_1_2h)
                ),
                4.0f,
                7.8f,
                8.5f,
                0
            )
        )
        listTarget.add(
            TargetRange(
                Condition(ConditionId.Condition06.name, context.getString(R.string.Asleep)),
                4.0f,
                4.7f,
                7.0f,
                0
            )
        )
        listTarget.add(
            TargetRange(
                Condition(
                    ConditionId.Condition07.name,
                    context.getString(R.string.Before_exercise)
                ),
                4.0f,
                5.5f,
                7.0f,
                0
            )
        )
        listTarget.add(
            TargetRange(
                Condition(ConditionId.Condition08.name, context.getString(R.string.After_exercise)),
                4.0f,
                5.5f,
                7.0f,
                0
            )
        )
        return listTarget
    }

    fun initTypeTargetRange(context: Context) {
        val listTypeTargetRange = ArrayList<TypeTargetRange>()
        listTypeTargetRange.clear()
        listTypeTargetRange.add(TypeTargetRange("0", context.getString(R.string.low), false))
        listTypeTargetRange.add(TypeTargetRange("1", context.getString(R.string.normal), false))
        listTypeTargetRange.add(
            TypeTargetRange(
                "2",
                context.getString(R.string.pre_diabetes),
                false
            )
        )
        listTypeTargetRange.add(TypeTargetRange("3", context.getString(R.string.diabetes), false))
        SPUtils.setString(context, Utils.KEY_TYPE_TARGET_RANGE, Gson().toJson(listTypeTargetRange))
    }

    fun getListNote(context: Context):ArrayList<Note> {
        val listNote = ArrayList<Note>()
        listNote.add(
            Note(
                context.getString(R.string.feel_good), 0, false, NoteType.NORMAL
            )
        )
        listNote.add(
            Note(
                context.getString(R.string.feel_uncomfortable),
                1,
                false,
                NoteType.NORMAL
            )
        )
        listNote.add(
            Note(
                context.getString(R.string.pregnancy),
                2,
                false,
                NoteType.NORMAL
            )
        )
        listNote.add(
            Note(
                context.getString(R.string.after_insulin), 3, false, NoteType.NORMAL
            )
        )
        listNote.add(
            Note(
                context.getString(R.string.morning), 4, false, NoteType.NORMAL
            )
        )
        listNote.add(
            Note(
                context.getString(R.string.daytime), 5, false, NoteType.NORMAL
            )
        )
        listNote.add(Note(context.getString(R.string.evening), 6, false, NoteType.NORMAL))
        return listNote
    }


}