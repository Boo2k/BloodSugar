package com.example.basekotlin.ui.history

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.Toast
import com.example.basekotlin.R
import com.example.basekotlin.base.BaseFragment
import com.example.basekotlin.base.tap
import com.example.basekotlin.data.Condition
import com.example.basekotlin.data.history.History
import com.example.basekotlin.databinding.FragmentHistoryBinding
import com.example.basekotlin.dialog.condition.ConditionDialog
import com.example.basekotlin.dialog.delete.DeleteRecordDialog
import com.example.basekotlin.ui.history.adapter.HistoryAdapter
import com.example.basekotlin.ui.main.MainActivity
import com.example.basekotlin.ui.record.EditRecordActivity
import com.example.basekotlin.ui.result.ResultActivity
import com.example.basekotlin.util.DatabaseManager
import com.example.basekotlin.util.SPUtils
import com.example.basekotlin.util.Utils
import com.example.basekotlin.util.convertTimeToDate
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


class HistoryFragment : BaseFragment<FragmentHistoryBinding>() {

    private var TAG = "HistoryFragment"

    private var popupDateTimeStart: PopupWindow = PopupWindow()
    private var popupDateTimeEnd: PopupWindow = PopupWindow()
    private var timeStart = Calendar.getInstance()
    private var timeEnd = Calendar.getInstance()

    private var isPopupStart = false
    private var isPopupEnd = false

    private var listCondition: ArrayList<Condition> = ArrayList()

    private var historyAdapter: HistoryAdapter? = null
    private var listNow: ArrayList<History> = ArrayList()
    private var listHistory: ArrayList<History> = ArrayList()


    override fun setBinding(
        inflater: LayoutInflater?, container: ViewGroup?, saveInstanceState: Bundle?,
    ): FragmentHistoryBinding {
        return FragmentHistoryBinding.inflate(inflater!!, container, false)
    }

    @SuppressLint("NewApi", "SetTextI18n")
    override fun initView() {
        timeStart = Calendar.getInstance()
        timeStart.set(
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH) - 2,
            1
        )

        timeEnd = Calendar.getInstance()
        timeEnd.set(
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )
        binding.tvStart.text = SimpleDateFormat("dd/MM/yyyy").format(timeStart.timeInMillis)
        binding.tvEnd.text = SimpleDateFormat("dd/MM/yyyy").format(timeEnd.timeInMillis)

        addDataCondition()
        addDataHistory()

        onCalendarStart()
        onCalendarEnd()
    }

    override fun bindView() {

        binding.btnCalendarStart.tap {
            if (isPopupStart) {
                popupDateTimeStart.dismiss()
            } else {
                popupDateTimeEnd.dismiss()
                popupDateTimeStart.showAsDropDown(binding.btnCalendarStart)
                isPopupStart = true
            }
        }

        binding.btnCalendarEnd.tap {
            if (isPopupEnd) {
                popupDateTimeEnd.dismiss()
            } else {
                popupDateTimeStart.dismiss()
                popupDateTimeEnd.showAsDropDown(binding.btnCalendarEnd)
                isPopupEnd = true
            }
        }

        binding.btnCondition.tap { onCondition() }

    }

    @SuppressLint("InflateParams")
    private fun onCalendarStart() {
        val layoutInflater =
            requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout: View = layoutInflater.inflate(R.layout.popup_date_time, null, false)
        popupDateTimeStart = PopupWindow(
            layout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )

        popupDateTimeStart.contentView = layout
        popupDateTimeStart.isOutsideTouchable = true
        popupDateTimeStart.isFocusable = true

        var tvTime = binding.tvStart.text.toString()

        val cv = layout.findViewById<View>(R.id.calendar_view) as CalendarView
        if (tvTime.isEmpty()) {
            val date: Calendar = Calendar.getInstance()
            date.set(
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH) - 2,
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            )
            cv.date = date.timeInMillis
            timeStart.set(
                date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH)
            )

        } else {
            cv.date = timeStart.timeInMillis
        }


        cv.setOnDateChangeListener { _, year, month, dayOfMonth ->
            var month1 = month
            val calendar: Calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth, 0, 0, 0)

            val date: Date = convertTimeToDate(calendar)!!
            val dateNow: Date = convertTimeToDate(Calendar.getInstance())!!
            val dateEnd: Date = convertTimeToDate(timeEnd)!!

            if (date.time <= dateNow.time) {
                if (date.time <= dateEnd.time) {
                    timeStart = calendar
                    Log.e("timeStart", "timeStart 2: ${timeStart.timeInMillis}")
                    month1 += 1

                    tvTime = "$dayOfMonth/$month1/$year"
                    binding.tvStart.text = tvTime

                    getDataHistory()
                    popupDateTimeStart.dismiss()
                } else {
                    Toast.makeText(requireContext(), R.string.invalid_date, Toast.LENGTH_SHORT)
                        .show()
                }

            } else {
                Toast.makeText(requireContext(), R.string.invalid_date, Toast.LENGTH_SHORT).show()
                cv.date = Calendar.getInstance().timeInMillis
            }
        }

        popupDateTimeStart.setOnDismissListener {
            isPopupStart = false
        }
    }

    @SuppressLint("InflateParams")
    private fun onCalendarEnd() {
        val layoutInflater =
            requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout: View = layoutInflater.inflate(R.layout.popup_date_time, null, false)
        popupDateTimeEnd = PopupWindow(
            layout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )

        popupDateTimeEnd.contentView = layout
        popupDateTimeEnd.isOutsideTouchable = true
        popupDateTimeEnd.isFocusable = true

        var tvTime = binding.tvEnd.text.toString()

        val cv = layout.findViewById<View>(R.id.calendar_view) as CalendarView
        if (tvTime.isEmpty()) {
            val date: Calendar = Calendar.getInstance()
            date.set(
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH) - 2,
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            )
            cv.date = date.timeInMillis

            timeEnd.set(
                date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH)
            )
        } else {
            cv.date = timeEnd.timeInMillis
        }


        cv.setOnDateChangeListener { _, year, month, dayOfMonth ->
            var month1 = month
            val calendar: Calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth, 23, 59, 59)

            val date: Date = convertTimeToDate(calendar)!!
            val dateNow: Date = convertTimeToDate(Calendar.getInstance())!!
            val dateStart: Date = convertTimeToDate(timeStart)!!

            if (date.time <= dateNow.time) {

                if (dateStart.time <= date.time) {
                    timeEnd = calendar
                    month1 += 1

                    tvTime = "$dayOfMonth/$month1/$year"
                    binding.tvEnd.text = tvTime

                    getDataHistory()
                    popupDateTimeEnd.dismiss()
                } else {
                    Toast.makeText(requireContext(), R.string.invalid_date, Toast.LENGTH_SHORT)
                        .show()
                }

            } else {
                Toast.makeText(requireContext(), R.string.invalid_date, Toast.LENGTH_SHORT).show()
                cv.date = Calendar.getInstance().timeInMillis
            }

        }

        popupDateTimeEnd.setOnDismissListener { isPopupEnd = false }
    }

    private fun onCondition() {
        SPUtils.setBoolean(requireActivity(), "all_type", true)
        val conditionDialog = ConditionDialog(requireActivity(), false, onClick = {
            binding.tvCondition.text = listCondition[it].name
            getDataHistory()
        })
        conditionDialog.window!!.setGravity(Gravity.BOTTOM)
        conditionDialog.window!!.setLayout(
            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        conditionDialog.show()
    }

    private fun addDataCondition() {
        listCondition = ArrayList()
        listCondition.addAll(DatabaseManager.getListCondition(requireActivity()))
    }

    private fun addDataHistory() {
        binding.rcvHistory.apply {
            historyAdapter = HistoryAdapter(requireActivity(), listNow, onResult = {
                var history: History? = null
                for (items in listHistory) {
                    if (items.id == it.id) {
                        history = items
                        break
                    }
                }
                onReloadAds(it, true)
            }, onEdit = {
                var history: History? = null
                for (items in listHistory) {
                    if (items.id == it.id) {
                        history = items
                        break
                    }
                }
                onReloadAds(it, false)
            }, onDelete = {
                var history: History? = null
                for (items in listHistory) {
                    if (items.id == it.id) {
                        history = items
                        break
                    }
                }
                val deleteDialog = DeleteRecordDialog(requireActivity(), false, onClick = {
                    DatabaseManager.deleteHistory(it)
                    getData()
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.record_deleted_successfully),
                        Toast.LENGTH_SHORT
                    ).show()
                })
                deleteDialog.show()

            })
            adapter = historyAdapter
        }

        getData()
    }

    @SuppressLint("SetTextI18n")
    private fun getData() {
        listNow = ArrayList()
        listHistory = ArrayList()
        listHistory.addAll(DatabaseManager.getAllHistory())
        listHistory.sortWith(Comparator { (_, _, _, _, timeDate): History, (_, _, _, _, timeDate1): History ->
            if (timeDate!! < timeDate1!!) {
                return@Comparator 1
            } else return@Comparator -1
        })

        getDataHistory()
    }

    @SuppressLint("SetTextI18n")
    private fun getDataHistory() {
        listNow.clear()

        for (items in listHistory) {
            if (timeStart.timeInMillis <= items.timeDate!! && items.timeDate!! <= timeEnd.timeInMillis) {
                if (binding.tvCondition.text == getString(R.string.All_type)) {
                    listNow.add(items)
                } else {
                    if (items.targetRange!!.condition!!.name == binding.tvCondition.text) {
                        listNow.add(items)
                    }
                }
            }
        }

        historyAdapter!!.updateData(listNow)

        if (listNow.size > 1) {
            binding.tvSizeRecords.text =
                "(${listNow.size} ${requireActivity().getString(R.string.records)})"
        } else {
            binding.tvSizeRecords.text =
                "(${listNow.size} ${requireActivity().getString(R.string.record)})"
        }

        Log.e("filter", "data: $listNow")

        if (listNow.size > 0) {
            binding.lnNoData.visibility = View.GONE
        } else {
            binding.lnNoData.visibility = View.VISIBLE
        }
    }

    private fun onReloadAds(history: History, isResult: Boolean) {
        if (requireActivity() is MainActivity) {
            val mainActivity: MainActivity = requireActivity() as MainActivity
            val bundle = Bundle()
            if (isResult) {
                bundle.putString(Utils.KEY_NEW_RECORD, Gson().toJson(history))
                mainActivity.reLoadAds(ResultActivity::class.java, bundle)
            } else {
                bundle.putString(Utils.KEY_EDIT_RECORD, Gson().toJson(history))
                mainActivity.reLoadAds(EditRecordActivity::class.java, bundle)
            }
        }

    }

}