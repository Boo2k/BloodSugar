package com.example.basekotlin.ui.result

import android.annotation.SuppressLint
import android.icu.util.Calendar
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.basekotlin.R
import com.example.basekotlin.all.adapter.NoteAdapter
import com.example.basekotlin.base.BaseActivity
import com.example.basekotlin.base.tap
import com.example.basekotlin.data.Note
import com.example.basekotlin.data.history.History
import com.example.basekotlin.databinding.ActivityResultBinding
import com.example.basekotlin.ui.main.MainActivity
import com.example.basekotlin.ui.record.EditRecordActivity
import com.example.basekotlin.util.SPUtils
import com.example.basekotlin.util.Utils
import com.google.gson.Gson
import java.text.SimpleDateFormat
import kotlin.math.roundToInt

class ResultActivity : BaseActivity<ActivityResultBinding>(ActivityResultBinding::inflate) {

    private var TAG = "ResultActivity"

    private var history: History? = null

    private var valueInput: Float = 0.0F

    private var noteAdapter: NoteAdapter? = null

    override fun initView() {
        binding.viewTop.tvToolBar.text = getString(R.string.result)
        binding.viewTop.ivRight.background =
            ContextCompat.getDrawable(this, R.drawable.ic_result_edit)

        setUpUi()
    }

    override fun bindView() {
        binding.viewTop.ivLeft.tap { onBackPressed() }

        binding.viewTop.ivRight.tap { onEdit() }

        binding.tvSugarDashboard.tap {
            startNextActivity(MainActivity::class.java, null)
            finishAffinity()
        }
    }

    private fun onEdit() {
        val bundle = Bundle()
        bundle.putString(Utils.KEY_EDIT_RECORD, Gson().toJson(history))
        startNextActivity(EditRecordActivity::class.java, bundle)
        setResult(RESULT_OK)
        finish()
    }

    @SuppressLint("NewApi", "SimpleDateFormat", "SetTextI18n")
    private fun setUpUi() {
        onUnit()

        history = Gson().fromJson(intent.getStringExtra(Utils.KEY_NEW_RECORD), History::class.java)

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = history!!.timeDate!!
        val timeFormat = SimpleDateFormat("HH:mm ꞏ MM/dd/yy")

        valueInput = history!!.valueInput!!
        if (SPUtils.getBoolean(this, Utils.UNIT, true)) {
            binding.tvLow.text = "4"
            binding.tvNormal.text = "5.5"
            binding.tvPreDiabetes.text = "7"
        } else {
            binding.tvLow.text = "71"
            binding.tvNormal.text = "99"
            binding.tvPreDiabetes.text = "126"

            valueInput *= 18f
        }
        valueInput = (valueInput * 100f).toInt().toFloat() / 100f

        binding.tvCondition.text = history!!.targetRange!!.condition!!.name
        binding.tvTime.text = timeFormat.format(calendar.time)
        binding.tvValue.text = "$valueInput"

        Handler(mainLooper).postDelayed({ setUpSeekBarCustom() }, 500)


        when (history!!.sugarTarget) {
            getString(R.string.low) -> {
                binding.tvSugarTarget.text = getString(R.string.low)
                binding.tvSugarTarget.setTextColor(ContextCompat.getColor(this, R.color.color_low))

                binding.rlResult.removeAllViews()
                val frameLayout =
                    LayoutInflater.from(this).inflate(R.layout.layout_result_low, null)
                binding.rlResult.addView(frameLayout)
            }

            getString(R.string.normal) -> {
                binding.tvSugarTarget.text = getString(R.string.normal)
                binding.tvSugarTarget.setTextColor(
                    ContextCompat.getColor(
                        this, R.color.color_normal
                    )
                )

                binding.rlResult.removeAllViews()
                val frameLayout =
                    LayoutInflater.from(this).inflate(R.layout.layout_result_normal, null)
                binding.rlResult.addView(frameLayout)
            }

            getString(R.string.pre_diabetes) -> {
                binding.tvSugarTarget.text = getString(R.string.pre_diabetes)
                binding.tvSugarTarget.setTextColor(
                    ContextCompat.getColor(
                        this, R.color.color_pre_diabetes
                    )
                )

                binding.rlResult.removeAllViews()
                val frameLayout =
                    LayoutInflater.from(this).inflate(R.layout.layout_result_pre_diabetes, null)
                binding.rlResult.addView(frameLayout)
            }

            getString(R.string.diabetes) -> {
                binding.tvSugarTarget.text = getString(R.string.diabetes)
                binding.tvSugarTarget.setTextColor(
                    ContextCompat.getColor(
                        this, R.color.color_diabetes
                    )
                )

                binding.rlResult.removeAllViews()
                val frameLayout =
                    LayoutInflater.from(this).inflate(R.layout.layout_result_diabetes, null)
                binding.rlResult.addView(frameLayout)
            }
        }

        val size = history!!.notes!!.size

        binding.rcvNote.layoutManager = if (size < 3) {
            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL)
        } else if (size < 5) {
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL)
        } else {
            StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL)
        }
        Log.e(TAG, "size: $size")

        binding.rcvNote.apply {
            noteAdapter = NoteAdapter(
                this@ResultActivity,
                history!!.notes as ArrayList<Note>?,
                false,
                onClick = {

                })
            adapter = noteAdapter
        }

//        Log.e(TAG, "idConfig: " + history!!.idConfig)
//        Log.e(TAG, "valueChart: " + history!!.valueChart)
//        Log.e(TAG, "value input: " + history!!.valueInput)
//        Log.e(TAG, "timeDate: " + history!!.timeDate)
//        Log.e(TAG, "notes: " + history!!.notes)
//        Log.e(TAG, "targetRange: " + history!!.targetRange)
    }

    @SuppressLint("NewApi", "ClickableViewAccessibility")
    private fun setUpSeekBarCustom() {

        binding.sbResult.setOnTouchListener { _, _ -> true }

        if (SPUtils.getBoolean(this, Utils.UNIT, true)) {
            binding.sbResult.min = 1
            binding.sbResult.max = 36

            if (valueInput < 4) {
                binding.sbResult.progress = ((valueInput * 2).roundToInt())
            } else if (4 <= valueInput && valueInput < 5.5) {
                binding.sbResult.progress = ((valueInput * 3.18).roundToInt())
            } else if (5.5 <= valueInput && valueInput < 7) {
                binding.sbResult.progress = ((valueInput * 3.22).roundToInt())
            } else {
                binding.sbResult.progress = ((valueInput * 4).roundToInt())
            }
        } else {
            binding.sbResult.min = 18
            binding.sbResult.max = 630

            if (valueInput < 71) {
                binding.sbResult.progress = ((valueInput * 2).roundToInt())
            } else if (71 <= valueInput && valueInput < 99) {
                binding.sbResult.progress = ((valueInput * 3.18).roundToInt())
            } else if (99 <= valueInput && valueInput < 126) {
                binding.sbResult.progress = ((valueInput * 3.22).roundToInt())
            } else {
                binding.sbResult.progress = ((valueInput * 4).roundToInt())
            }
        }

    }

    @SuppressLint("ResourceType", "NewApi")
    private fun onUnit() {
        if (SPUtils.getBoolean(this, Utils.UNIT, true)) {
            binding.tvUnit.text = getString(R.string.mmol_l)
        } else {
            binding.tvUnit.text = getString(R.string.mg_dL)
        }
    }

    override fun onBackPressed() {
        setResult(RESULT_OK)
        finishThisActivity()
    }
}