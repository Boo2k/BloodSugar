package com.example.basekotlin.ui.tracker

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.basekotlin.R
import com.example.basekotlin.base.BaseFragment
import com.example.basekotlin.base.tap
import com.example.basekotlin.customview.MyAxisValueFormatter
import com.example.basekotlin.customview.MyMarkerView
import com.example.basekotlin.data.Condition
import com.example.basekotlin.data.history.History
import com.example.basekotlin.databinding.FragmentTrackerBinding
import com.example.basekotlin.dialog.condition.ConditionDialog
import com.example.basekotlin.ui.main.MainActivity
import com.example.basekotlin.ui.record.EditRecordActivity
import com.example.basekotlin.ui.result.ResultActivity
import com.example.basekotlin.ui.tracker.adapter.TrackerAdapter
import com.example.basekotlin.util.DatabaseManager
import com.example.basekotlin.util.SPUtils
import com.example.basekotlin.util.SugarTargetType
import com.example.basekotlin.util.Utils
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend.LegendForm
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.util.Calendar
import kotlin.math.roundToInt
import kotlin.math.roundToLong


class TrackerFragment : BaseFragment<FragmentTrackerBinding>() {
    override fun setBinding(
        inflater: LayoutInflater?, container: ViewGroup?, saveInstanceState: Bundle?,
    ): FragmentTrackerBinding {
        return FragmentTrackerBinding.inflate(inflater!!, container, false)
    }

    private var TAG = "TrackerFragment"

    private var listCondition: ArrayList<Condition> = ArrayList()

    private var mv: MyMarkerView? = null

    private var recordSelected: History? = null
    private var isMol = false
    private var listHistory = ArrayList<History>()
    private var listByCategory = ArrayList<History>()
    private var listHistoryByDB: ArrayList<History> = ArrayList()

    private var listTracker: ArrayList<History> = ArrayList()
    private var trackerAdapter: TrackerAdapter? = null

    private var max = 10F

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initView() {
        addDataCondition()

        setUpDataRecently()
        addDataObservable()
        setUpChartView()
    }

    override fun bindView() {
        binding.btnCondition.tap {
            onCondition()
        }
    }

    private fun onCondition() {
        SPUtils.setBoolean(requireActivity(), "all_type", true)
        val conditionDialog = ConditionDialog(requireActivity(), false, onClick = {
            binding.tvCondition.text = listCondition[it].name
            getRecordByCategory(listCondition[it].name.toString())
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

    private fun setUpDataRecently() {
        listTracker = ArrayList()

        binding.rcvRecently.apply {
            trackerAdapter = TrackerAdapter(requireActivity(), listTracker, onResult = {onReloadAds(it,true)}, onEdit = {
                onReloadAds(it,false)
            })
            adapter = trackerAdapter
        }

    }

    private fun addDataObservable() {
        listHistoryByDB = ArrayList()
        listHistoryByDB.addAll(DatabaseManager.getAllHistory())

        listTracker = ArrayList()
        listHistory = ArrayList()

        isMol = SPUtils.getBoolean(requireActivity(), Utils.UNIT, true)
        if (listHistoryByDB.isNotEmpty()) {
            if (isMol) {
                max = listHistoryByDB[0].valueInput!!
                for (y in listHistoryByDB) {
                    if (y.valueInput!! > max) {
                        max = y.valueInput!!
                    }
                }
            } else {
                max = listHistoryByDB[0].valueInput!! * 18
                for (y in listHistoryByDB) {
                    if (y.valueInput!! * 18 > max) {
                        max = y.valueInput!! * 18
                    }
                }
            }
            max = (max / 10 + 1) * 10 + 10
            Log.e("max", "max: $max")
            listHistory.addAll(listHistoryByDB)
            listHistory.sortWith(Comparator { (_, _, _, _, timeDate): History, (_, _, _, _, timeDate1): History ->
                if (timeDate!! > timeDate1!!) {
                    return@Comparator 1
                } else return@Comparator -1
            })
            val number: String = if (isMol) {
                ((listHistory[listHistory.size - 1].valueInput!! * 100).roundToLong()
                    .toDouble() / 100).toString()
            } else {
                ((listHistory[listHistory.size - 1].valueInput!! * 18f * 100).roundToLong()
                    .toDouble() / 100).toString()
            }
            val time = listHistory[listHistory.size - 1].timeDate
            setSelectedTracker(number, time!!, calculateStatus(listHistory[listHistory.size - 1]))
            recordSelected = listHistory[listHistory.size - 1]

            resetYAxis(max)
            //data recently
            if (binding.tvCondition.text == getText(R.string.All_type)) {
                setData(listHistory)
            } else {
                getRecordByCategory(binding.tvCondition.text.toString())
            }
            //notifidata recently
        }
    }

    private fun setData(histories: List<History>) {
        val values = ArrayList<Entry>()
        val calendar: Calendar = Calendar.getInstance()
        var xValue = 0f
        var yValue: Float

        var listNow: ArrayList<History> = ArrayList()
        listTracker.clear()

        for (data in histories) {
            data.timeDate?.let { calendar.timeInMillis = it }
            yValue = if (SPUtils.getBoolean(requireActivity(), Utils.UNIT, true)) {
                data.valueInput!!
            } else data.valueInput!! * 18
            val current: Int = Calendar.getInstance().get(Calendar.MONTH)
            if (current - calendar.get(Calendar.MONTH) == 0) {
                xValue = data.valueChart!! + 60
            } else if (current - calendar.get(Calendar.MONTH) == 1) {
                xValue = data.valueChart!! + 30
            } else if (current - calendar.get(Calendar.MONTH) == 2) {
                xValue = data.valueChart!!
            }
            values.add(Entry(xValue, yValue))

            listNow.add(data)
        }

        listNow.sortWith(Comparator { (_, _, _, _, timeDate): History, (_, _, _, _, timeDate1): History ->
            if (timeDate!! < timeDate1!!) {
                return@Comparator 1
            } else return@Comparator -1
        })

        for (items in listNow) {
            if (listTracker.size < 3) {
                listTracker.add(items)
            } else {
                break
            }
        }

        Log.e("llll", "trackerModel: $listTracker")
        trackerAdapter!!.updateData(listTracker)
        if (listTracker.size > 0) {
            binding.lnNoData.visibility = View.GONE
        } else {
            binding.lnNoData.visibility = View.VISIBLE
        }

        if (values.isNotEmpty()) binding.chartView.moveViewToX(values[values.size - 1].x - 4f)

        val set1: LineDataSet

        if (binding.chartView.data != null && binding.chartView.data.dataSetCount > 0) {
            set1 = binding.chartView.data.getDataSetByIndex(0) as LineDataSet
            set1.values = values
            set1.notifyDataSetChanged()
            binding.chartView.invalidate()
            binding.chartView.data.notifyDataChanged()
            binding.chartView.notifyDataSetChanged()
        } else {
            // create a dataset and give it a type
            set1 = LineDataSet(values, "")
            set1.setDrawHorizontalHighlightIndicator(false)
            set1.setDrawIcons(false)

            // draw dashed line
            set1.enableDashedLine(10f, 0f, 0f)

            // black lines and points
            set1.color = resources.getColor(R.color.color_27A453)
            set1.setCircleColor(resources.getColor(R.color.color_27A453))
            // line thickness and point size
            set1.lineWidth = 3f
            set1.circleRadius = 5f

            // draw points as solid circles
            set1.setDrawCircleHole(false)

            // customize legend entry
            set1.formLineWidth = 1f
            set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            set1.formSize = 0f
            // text size of values
            set1.valueTextSize = 0f
            // draw selection line as dashed
            set1.enableDashedLine(10f, 0f, 0f)

            // set the filled area
            set1.setDrawFilled(true)
            set1.fillFormatter = IFillFormatter { _, _ -> binding.chartView.axisLeft.axisMinimum }

            // set color of filled area
            val drawable = ContextCompat.getDrawable(requireActivity(), R.drawable.bg_chart)
            set1.fillDrawable = drawable
            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(set1) // add the data sets

            // create a data object with the data sets
            val data = LineData(dataSets)

            // set data
            binding.chartView.data = data
            binding.chartView.data.notifyDataChanged()
            binding.chartView.notifyDataSetChanged()
        }
    }

    private fun getRecordByCategory(name: String) {
        listByCategory = ArrayList()
        if (name == getString(R.string.All_type)) {
            listByCategory.addAll(listHistory)
        } else {
            for (record in listHistory) {
                Log.e("aaaa", "record: $record")
                if (record.targetRange!!.condition!!.name == binding.tvCondition.text) {
                    listByCategory.add(record)
                }
            }
        }
        if (listByCategory.isNotEmpty()) {
            val number: String = if (isMol) {
                ((listByCategory[listByCategory.size - 1].valueInput!! * 100).roundToInt()
                    .toDouble() / 100).toString()
            } else {
                ((listByCategory[listByCategory.size - 1].valueInput!! * 18f * 100).roundToInt()
                    .toDouble() / 100).toString()
            }
            val time: Long = listByCategory[listByCategory.size - 1].timeDate!!
            setSelectedTracker(
                number, time, calculateStatus(listByCategory[listByCategory.size - 1])
            )
            recordSelected = listByCategory[listByCategory.size - 1]
        }
        if (listByCategory.isEmpty() && name == getString(R.string.All_type)) {
            val number: String = if (isMol) {
                ((listHistory[listHistory.size - 1].valueInput!! * 100).roundToInt()
                    .toDouble() / 100).toString()
            } else {
                ((listHistory[listHistory.size - 1].valueInput!! * 18f * 100).roundToInt()
                    .toDouble() / 100).toString()
            }
            val time: Long = listHistory[listHistory.size - 1].timeDate!!
            setSelectedTracker(number, time, calculateStatus(listHistory[listHistory.size - 1]))
            recordSelected = listHistory[listHistory.size - 1]
            setData(listHistory)
        } else setData(listByCategory)
    }

    @SuppressLint("NewApi")
    private fun setUpChartView() {
        run {
            // background color
            binding.chartView.setBackgroundColor(Color.WHITE)

            // disable description text
            binding.chartView.description.isEnabled = false

            // enable touch gestures
            binding.chartView.setTouchEnabled(true)
            binding.chartView.setDrawMarkers(false)

            // set listeners
            binding.chartView.setDrawGridBackground(false)
        }
        run {

            //hiển thị điểm click và zoom
            // create marker to display box when values are selected
            mv = MyMarkerView(requireActivity(), R.layout.item_chart)

            // Set the marker to the chart
            mv!!.chartView = binding.chartView
            binding.chartView.marker = mv

            // enable scaling and dragging
            binding.chartView.isDragEnabled = true
            binding.chartView.setScaleEnabled(true)

            // force pinch zoom along both axis
            binding.chartView.setPinchZoom(false)
            binding.chartView.isScaleYEnabled = false
            binding.chartView.isDragEnabled = true
            binding.chartView.isScaleXEnabled = false
            binding.chartView.isHorizontalScrollBarEnabled = false
            //lắng nghe sự kiện click các điểm trong line chart
            binding.chartView.setOnChartValueSelectedListener(object :
                OnChartValueSelectedListener {
                override fun onValueSelected(
                    e: Entry,
                    h: Highlight,
                ) {
                    for (data in listHistory) {
                        var x = 0f
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = data.timeDate!!
                        val current = Calendar.getInstance()[Calendar.MONTH]
                        if (current - calendar[Calendar.MONTH] == 0) {
                            x = data.valueChart!! + 60
                        } else if (current - calendar[Calendar.MONTH] == 1) {
                            x = data.valueChart!! + 30
                        } else if (current - calendar[Calendar.MONTH] == 2) {
                            x = data.valueChart!!
                        }
                        val y: Float = if (isMol) {
                            data.valueInput!!
                        } else data.valueInput!! * 18
                        if (x == e.x && y == e.y) {
                            setSelectedTracker(
                                ((e.y * 100).roundToInt().toDouble() / 100).toString(),
                                data.timeDate!!,
                                calculateStatus(data)
                            )
                            recordSelected = data
                        }
                    }
                    //recently
                }

                override fun onNothingSelected() {}
            })
        }
        var xAxis: XAxis
        run {
            // // X-Axis Style // //
            xAxis = binding.chartView.xAxis

            // vertical grid lines
            val xValue: Float
            val current = Calendar.getInstance()
            val year = current[Calendar.YEAR]
            val month = current[Calendar.MONTH] + 1
            getMonthLength(year, month)
            xValue = when (month) {
                1 -> {
                    getMonthLength(year - 1, 11) + getMonthLength(year - 1, 12) + getMonthLength(
                        year, month
                    )
                }

                2 -> {
                    getMonthLength(year - 1, 12) + getMonthLength(
                        year, month - 1
                    ) + getMonthLength(year, month)
                }

                else -> {
                    getMonthLength(year, month - 2) + getMonthLength(
                        year, month - 1
                    ) + getMonthLength(
                        year, month
                    )
                }
            }
            xAxis.enableGridDashedLine(10f, 0f, 0f)
            xAxis.axisMaximum = xValue
            xAxis.axisMinimum = 1f
            xAxis.labelCount = 7
            binding.chartView.zoom(12f, 0f, 9.5f, 0f)
            xAxis.textColor = R.color.color_us
        }
        resetYAxis(max)

        // draw limit lines behind data instead of on top
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        // get the legend (only possible after setting data)
        val l = binding.chartView.legend

        // draw legend entries as lines
        l.form = LegendForm.LINE
        xAxis.valueFormatter = MyAxisValueFormatter()
    }

    @SuppressLint("SimpleDateFormat")
    private fun setSelectedTracker(number: String, time: Long, type: SugarTargetType) {
        val unit: String = if (SPUtils.getBoolean(requireActivity(), Utils.UNIT, true)) {
            getString(R.string.mmol_l)
        } else getString(R.string.mg_dL)

        val spannable = SpannableStringBuilder("$number $unit")
        spannable.setSpan(
            StyleSpan(Typeface.BOLD), 0, number.length, SpannableString.SPAN_INCLUSIVE_INCLUSIVE
        )
        spannable.setSpan(
            RelativeSizeSpan(1.8f), 0, number.length, SpannableString.SPAN_INCLUSIVE_INCLUSIVE
        )
        val format = SimpleDateFormat("MMM d HH:mm")
        val dateTime = format.format(time)
        binding.tvGlycemicIndex.text = number
        binding.tvUnit.text = unit
    }

    private fun calculateStatus(data: History): SugarTargetType {
        var type: SugarTargetType = SugarTargetType.NORMAL
        if (data.valueInput!! < data.targetRange!!.low!!) {
            type = SugarTargetType.LOW
        } else if (data.valueInput!! < data.targetRange!!.normal!!) {
            type = SugarTargetType.NORMAL
        } else if (data.valueInput!! < data.targetRange!!.preDiabetes!!) {
            type = SugarTargetType.PRE_DIABETES
        } else if (data.valueInput!! > data.targetRange!!.preDiabetes!!) {
            type = SugarTargetType.DIABETES
        }
        return type
    }

    private fun resetYAxis(maxY: Float) {
        Log.e("max", "max: run")
        val yAxis: YAxis = binding.chartView.axisLeft

        binding.chartView.axisRight.isEnabled = false
        yAxis.axisMaximum = maxY
        yAxis.axisMinimum = 0f
        yAxis.setDrawGridLinesBehindData(false)
        yAxis.setDrawGridLines(false)
        yAxis.textColor = R.color.color_us
        yAxis.valueFormatter = IAxisValueFormatter { value: Float, _: AxisBase? ->
            if (value == 0f) return@IAxisValueFormatter "0"
            value.toString()
        }
        setData(listHistory)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getMonthLength(year: Int, month: Int): Float {
        return YearMonth.of(year, month).lengthOfMonth().toFloat()
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