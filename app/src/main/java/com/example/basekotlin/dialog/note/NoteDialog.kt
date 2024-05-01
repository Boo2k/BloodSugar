package com.example.basekotlin.dialog.note

import android.content.Context
import android.util.Log
import com.example.basekotlin.all.adapter.NoteAdapter
import com.example.basekotlin.base.BaseDialog
import com.example.basekotlin.base.tap
import com.example.basekotlin.data.Note
import com.example.basekotlin.databinding.DialogNoteBinding
import com.example.basekotlin.util.DatabaseManager
import com.example.basekotlin.util.SPUtils
import com.example.basekotlin.util.Utils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class NoteDialog(
    context: Context, cancelAble: Boolean?, var onClick: (ArrayList<Note>) -> Unit,
) : BaseDialog<DialogNoteBinding>(context, cancelAble!!) {

    private var TAG = "NoteDialog"

    private var listNote: ArrayList<Note> = ArrayList()
    private var listNoteAll: ArrayList<Note> = ArrayList()
    var list: ArrayList<Note> = ArrayList()

    private var noteAdapter: NoteAdapter? = null

    private var type: Type? = null
    private var data = ""

    private var isNoteChange = false

    override fun setBinding(): DialogNoteBinding {
        return DialogNoteBinding.inflate(layoutInflater)
    }

    init {
        isNoteChange = false
    }

    override fun initView() {

        listNoteAll = ArrayList()
        listNoteAll = DatabaseManager.getListNote(context)

        type = object : TypeToken<List<Note?>?>() {}.type

        list = ArrayList()
        data = SPUtils.getString(context, Utils.KEY_LIST_NOTE, "")

        if (data != "") {
            list = Gson().fromJson(data, type)

            if (list.size > 0) {
                for (note in listNoteAll) {
                    for (item in list) {
                        if (item.order == note.order) {
                            note.isSelected = true
                        }
                    }
                    Log.e(TAG, "note ${note.order}: " + note.isSelected)
                }
            }
        }

        binding.rcvNote.apply {
            noteAdapter = NoteAdapter(context, listNoteAll, true, onClick = {
                listNote = ArrayList()
                listNote.addAll(it)
                isNoteChange = true
            })
            adapter = noteAdapter
        }
    }

    override fun bindView() {
        binding.btnCancel.tap { dismiss() }

        binding.btnOk.tap {
            dismiss()
            if (isNoteChange) {
                onClick.invoke(listNote)
            }
        }
    }
}