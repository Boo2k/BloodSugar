package com.example.basekotlin.ui.record

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.basekotlin.R
import com.example.basekotlin.all.adapter.NoteAdapter
import com.example.basekotlin.base.BaseActivity
import com.example.basekotlin.base.tap
import com.example.basekotlin.data.Condition
import com.example.basekotlin.data.Note
import com.example.basekotlin.data.TargetRange
import com.example.basekotlin.data.history.History
import com.example.basekotlin.databinding.ActivityNewRecordBinding
import com.example.basekotlin.dialog.condition.ConditionDialog
import com.example.basekotlin.dialog.datetime.DateTimeDialog
import com.example.basekotlin.dialog.note.NoteDialog
import com.example.basekotlin.dialog.recordexists.RecordExistsDialog
import com.example.basekotlin.ui.result.ResultActivity
import com.example.basekotlin.util.DatabaseManager
import com.example.basekotlin.util.SPUtils
import com.example.basekotlin.util.Utils
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Calendar
import kotlin.math.roundToInt


class NewRecordActivity :
    BaseActivity<ActivityNewRecordBinding>(ActivityNewRecordBinding::inflate) {

    private val TAG = "NewRecordActivity"
    private var isUnit = true
    private var valueInput: Float = 5.0F

    private var listTargetRange = ArrayList<TargetRange>()
    private var listCondition = ArrayList<Condition>()
    private var listNote = ArrayList<Note>()
    private var noteAdapter: NoteAdapter? = null

    private var dateHours: Int = 0
    private var dateDate: Int = 0
    private var dateTime: Long = 0

    private var low: Float = 0.0F
    private var normal: Float = 0.0F
    private var preDiabetes: Float = 0.0F
    private val targetRange = TargetRange()

    private var idHistory = -1

    private val timeFormat = SimpleDateFormat("MM/dd/yyyy, HH:mm")

    @SuppressLint("NewApi", "SetTextI18n")
    override fun initView() {
        binding.viewTop.ivRight.visibility = View.INVISIBLE
        binding.viewTop.tvToolBar.text = getString(R.string.new_record)

        isUnit = SPUtils.getBoolean(this, Utils.UNIT, true)

        setUpListData()

        if (!isUnit) {
            valueInput *= 18
        }
        onUnit(isUnit)
        binding.edtValue.setText("$valueInput")
        checkValueInput(valueInput)

        dateHours = Calendar.getInstance().time.hours
        dateDate = Calendar.getInstance().time.date
        dateTime = Calendar.getInstance().time.time


        binding.tvDateTime.text = "${timeFormat.format(Calendar.getInstance().time)}"
    }

    override fun bindView() {
        binding.viewTop.ivLeft.tap { onBackPressed() }

        binding.btnDateTime.tap { onDateTime() }

        binding.btnCondition.tap { onCondition() }

        binding.btnMgDL.tap {
            if (isUnit) {
                onUnit(false)
            }
        }

        binding.btnMmolL.tap {
            if (!isUnit) {
                onUnit(true)
            }
        }

        binding.btnNote.tap { onNote() }

        binding.btnSave.tap { onSave() }

        binding.edtValue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                try {
                    valueInput = editable.toString().toFloat()
                } catch (e: Exception) {
                    Log.e(TAG, "afterTextChanged: $e")
                }
                checkValueInput(valueInput)
            }
        })
    }

    private fun onDateTime() {
        val dateTimeDialog = DateTimeDialog(this, false, onClick = {
            binding.tvDateTime.text = "${timeFormat.format(it)}"
            dateHours = it.hours
            dateDate = it.date
            dateTime = it.time
        })
        dateTimeDialog.window!!.setGravity(Gravity.BOTTOM)
        dateTimeDialog.window!!.setLayout(
            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        dateTimeDialog.show()
    }

    private fun onCondition() {
        SPUtils.setBoolean(this, "all_type", false)
        val conditionDialog = ConditionDialog(this, false, onClick = {
            binding.tvCondition.text = listCondition[it + 1].name
        })
        conditionDialog.window!!.setGravity(Gravity.BOTTOM)
        conditionDialog.window!!.setLayout(
            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        conditionDialog.show()
    }

    @SuppressLint("ResourceType", "NewApi")
    private fun onUnit(flag: Boolean) {
        isUnit = flag

        binding.tvMgDL.setTextColor(ContextCompat.getColor(this, R.color.black))
        binding.tvMmolL.setTextColor(ContextCompat.getColor(this, R.color.black))

        binding.btnMgDL.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.color_btn_us)
        binding.btnMmolL.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.color_btn_us)

        val newValue: Float

        if (flag) {
            binding.tvMmolL.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.btnMmolL.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.color_1B6211)
            newValue = valueInput / 18
        } else {
            binding.tvMgDL.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.btnMgDL.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.color_1B6211)
            newValue = valueInput * 18
        }

        val text = ((newValue * 100).roundToInt().toDouble() / 100).toString()
        binding.edtValue.setText(text)
        checkValueInput(newValue)
    }

    private fun onNote() {
        SPUtils.setString(this, Utils.KEY_LIST_NOTE, Gson().toJson(listNote))

        val noteDialog = NoteDialog(this, false, onClick = {
            listNote = ArrayList()
            listNote.addAll(it)
            Log.e(TAG, "listNote: " + listNote.size)
        })

        noteDialog.window!!.setGravity(Gravity.BOTTOM)
        noteDialog.window!!.setLayout(
            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        noteDialog.show()

        noteDialog.setOnDismissListener {
            val manager = if (listNote.size < 3) {
                StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL)
            } else if (listNote.size < 5) {
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL)
            } else {
                StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL)
            }
            binding.rcvNote.layoutManager = manager

            noteAdapter!!.updateData(listNote)
        }
    }

    private fun onSave() {
        try {
            valueInput = binding.edtValue.text.toString().toFloat()
            if (isUnit && (valueInput < 1 || valueInput > 35)) {
                Toast.makeText(
                    this,
                    getString(R.string.please_input_a_valid_number_1_0_35_0_mmol_l),
                    Toast.LENGTH_SHORT
                ).show()
                if (valueInput < 1) {
                    valueInput = 1F
                } else if (valueInput > 35) {
                    valueInput = 35F
                }
                binding.edtValue.setText("$valueInput")
            } else if (!isUnit && (valueInput < 18 || valueInput > 630)) {
                Toast.makeText(
                    this,
                    getString(R.string.please_input_a_valid_number_18_0_630_0_mg_dl),
                    Toast.LENGTH_SHORT
                ).show()
                if (valueInput < 18) {
                    valueInput = 18F
                } else if (valueInput > 630) {
                    valueInput = 630F
                }
                binding.edtValue.setText("$valueInput")
            } else {
                saveHistory()
            }

        } catch (_: Exception) {
            if (isUnit) {
                Toast.makeText(
                    this,
                    getString(R.string.please_input_a_valid_number_1_0_35_0_mmol_l),
                    Toast.LENGTH_SHORT
                ).show()
                if (valueInput < 1) {
                    valueInput = 1F
                } else if (valueInput > 35) {
                    valueInput = 35F
                }
                binding.edtValue.setText("$valueInput")
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.please_input_a_valid_number_18_0_630_0_mg_dl),
                    Toast.LENGTH_SHORT
                ).show()
                if (valueInput < 18) {
                    valueInput = 18F
                } else if (valueInput > 630) {
                    valueInput = 630F
                }
                binding.edtValue.setText("$valueInput")
            }
        }
    }

    private fun saveHistory() {
        var idConfig = ""
        var name = ""
        for (data in listCondition) {
            if (data.name == binding.tvCondition.text) {
                idConfig = data.idConfig.toString()
                name = data.name.toString()
            }
        }
        val valueChart: Float = dateHours.toFloat() / 24 + (dateDate + 1)

        val valueInputSave: Float = if (isUnit) {
            valueInput
        } else {
            valueInput / 18f
        }

        if (dateTime <= Calendar.getInstance().timeInMillis) {
            val target = TargetRange(
                Condition(idConfig, name), low, normal, preDiabetes, 0
            )
            val listHistory = DatabaseManager.checkRecordExists(
                History(
                    null, idConfig, valueChart, valueInputSave, dateTime, listNote, target
                )
            )

            if (listHistory!!.isEmpty()) {
                saveRecord(idConfig, valueChart, valueInputSave, target)
            } else {
                val dialog = RecordExistsDialog(this, false, onClick = {
                    saveRecord(idConfig, valueChart, valueInputSave, target)
                })
                dialog.show()
            }
        }
    }

    private fun saveRecord(
        idConfig: String, valueChart: Float, valueInput: Float, target: TargetRange,
    ) {
        val startLimit = 1f
        val endLimit = 35f
        val strUnit: String = if (isUnit) {
            getString(R.string.mmol_l)
        } else {
            getString(R.string.mg_dL)
        }
        if (valueInput in startLimit..endLimit) {
            idHistory += 1

            val historyModel = History(
                idHistory,
                idConfig,
                valueChart,
                valueInput,
                dateTime,
                listNote,
                target,
                binding.tvSugarTarget.text.toString()
            )
            DatabaseManager.addHistory(
                historyModel
            )

            SPUtils.setBoolean(this, Utils.UNIT, isUnit)
            SPUtils.setInt(this, Utils.KEY_ID_HISTORY, idHistory)

            val bundle = Bundle()
            bundle.putString(Utils.KEY_NEW_RECORD, Gson().toJson(historyModel))

            setResult(RESULT_OK)
            startNextActivity(ResultActivity::class.java, bundle)
            finish()
        } else {
            Toast.makeText(
                this, getString(
                    R.string.please_enter_valid_value,
                    startLimit.toString(),
                    endLimit.toString(),
                    strUnit
                ), Toast.LENGTH_LONG
            ).show()
        }
    }

    @SuppressLint("NewApi", "ResourceType", "SetTextI18n")
    private fun checkValueInput(value: Float) {
        for (target: TargetRange in listTargetRange) {
            if (target.condition!!.name == binding.tvCondition.text) {
                targetRange.low = target.low
                targetRange.normal = target.normal
                targetRange.preDiabetes = target.preDiabetes
            }
        }

        low = targetRange.low!!
        normal = targetRange.normal!!
        preDiabetes = targetRange.preDiabetes!!

        if (!isUnit) {
            low *= 18
            normal *= 18
            preDiabetes *= 18
        }

        binding.viewCheckUnit.tvLow.text = "<$low"
        binding.viewCheckUnit.tvNormal.text = "$low-$normal"
        binding.viewCheckUnit.tvPreDiabetes.text = "$normal-$preDiabetes"
        binding.viewCheckUnit.tvDiabetes.text = ">$preDiabetes"

        binding.viewCheckUnit.lnLow.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.white)
        binding.viewCheckUnit.lnNormal.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.white)
        binding.viewCheckUnit.lnPreDiabetes.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.white)
        binding.viewCheckUnit.lnDiabetes.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.white)

        if (value < low) {
            binding.tvSugarTarget.text = getString(R.string.low)
            binding.tvSugarTarget.setTextColor(ContextCompat.getColor(this, R.color.color_low))

            binding.viewCheckUnit.lnLow.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.color_E7F4DF)
        } else if (value < normal) {
            binding.tvSugarTarget.text = getString(R.string.normal)
            binding.tvSugarTarget.setTextColor(ContextCompat.getColor(this, R.color.color_normal))

            binding.viewCheckUnit.lnNormal.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.color_E7F4DF)
        } else if (value < preDiabetes) {
            binding.tvSugarTarget.text = getString(R.string.pre_diabetes)
            binding.tvSugarTarget.setTextColor(
                ContextCompat.getColor(
                    this, R.color.color_pre_diabetes
                )
            )

            binding.viewCheckUnit.lnPreDiabetes.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.color_E7F4DF)
        } else {
            binding.tvSugarTarget.text = getString(R.string.diabetes)
            binding.tvSugarTarget.setTextColor(ContextCompat.getColor(this, R.color.color_diabetes))

            binding.viewCheckUnit.lnDiabetes.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.color_E7F4DF)
        }


    }

    private fun setUpListData() {
        listTargetRange = ArrayList()
        listTargetRange.addAll(DatabaseManager.getListTargetRange(this))
        if (listTargetRange.size > 0) {
            listTargetRange.removeAt(0)
        }
        listCondition = ArrayList()
        listCondition.addAll(DatabaseManager.getListCondition(this))

        listNote = ArrayList()

        binding.rcvNote.apply {
            noteAdapter = NoteAdapter(this@NewRecordActivity, listNote, false, onClick = {

            })
            adapter = noteAdapter
        }

        idHistory = SPUtils.getInt(this, Utils.KEY_ID_HISTORY, -1)
    }

    override fun onBackPressed() {
        setResult(RESULT_OK)
        finishThisActivity()
    }
}