package com.example.basekotlin.ui.history.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.basekotlin.R
import com.example.basekotlin.base.tap
import com.example.basekotlin.data.history.History
import com.example.basekotlin.util.SPUtils
import com.example.basekotlin.util.Utils
import java.text.SimpleDateFormat


class HistoryAdapter(
    var context: Context,
    private var listHistory: ArrayList<History>?,
    var onResult: (History) -> Unit,
    var onEdit: (History) -> Unit,
    var onDelete: (History) -> Unit,
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    @SuppressLint("NewApi", "ResourceType", "SimpleDateFormat")
    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val historyModel: History = listHistory!![position]

        var valueUnit = 1

        if (SPUtils.getBoolean(context, Utils.UNIT, true)) {
            holder.tvUnit.text = context.getString(R.string.mmol_l)

            valueUnit *= 1
        } else {
            holder.tvUnit.text = context.getString(R.string.mg_dL)
            valueUnit *= 18
        }
        val valueInput = (historyModel.valueInput!! * valueUnit * 100f).toInt().toFloat() / 100f

        holder.tvValue.text = "$valueInput"

        holder.tvCondition.text = historyModel.targetRange!!.condition!!.name

        Log.e("timeDate", "timeDate $position: ${historyModel.timeDate}")
        holder.tvTime.text = SimpleDateFormat("dd/MM/yyyy ꞏ HH:mm").format(historyModel.timeDate)

        when (historyModel.sugarTarget) {
            context.getString(R.string.low) -> {
                holder.tvSugarTarget.text = context.getString(R.string.low)
                holder.bgValue.background =
                    ContextCompat.getDrawable(context, R.drawable.bg_radius_history_low)
                holder.tvValue.setTextColor(ContextCompat.getColor(context, R.color.color_121268))
                holder.tvUnit.setTextColor(ContextCompat.getColor(context, R.color.color_121268))
            }

            context.getString(R.string.normal) -> {
                holder.tvSugarTarget.text = context.getString(R.string.normal)
                holder.bgValue.background =
                    ContextCompat.getDrawable(context, R.drawable.bg_radius_history_normal)
                holder.tvValue.setTextColor(ContextCompat.getColor(context, R.color.color_001705))
                holder.tvUnit.setTextColor(ContextCompat.getColor(context, R.color.color_001705))
            }

            context.getString(R.string.pre_diabetes) -> {
                holder.tvSugarTarget.text = context.getString(R.string.pre_diabetes)
                holder.bgValue.background =
                    ContextCompat.getDrawable(context, R.drawable.bg_radius_history_pre_diabetes)
                holder.tvValue.setTextColor(ContextCompat.getColor(context, R.color.color_353000))
                holder.tvUnit.setTextColor(ContextCompat.getColor(context, R.color.color_353000))
            }

            context.getString(R.string.diabetes) -> {
                holder.tvSugarTarget.text = context.getString(R.string.diabetes)
                holder.bgValue.background =
                    ContextCompat.getDrawable(context, R.drawable.bg_radius_history_diabetes)
                holder.tvValue.setTextColor(ContextCompat.getColor(context, R.color.color_330000))
                holder.tvUnit.setTextColor(ContextCompat.getColor(context, R.color.color_330000))
            }
        }

        /*if (historyModel.valueInput!! < historyModel.targetRange!!.low!!) {

        } else if (historyModel.valueInput!! >= historyModel.targetRange!!.low!! && historyModel.valueInput!! < historyModel.targetRange!!.normal!!) {

        } else if (historyModel.valueInput!! >= historyModel.targetRange!!.normal!! && historyModel.valueInput!! < historyModel.targetRange!!.preDiabetes!!) {

        } else {

        }*/

        holder.itemView.tap { onResult.invoke(historyModel) }

        holder.btnMore.tap {
            popupMenu(holder.btnMore, historyModel)
        }
    }

    private fun popupMenu(btnClick: View, history: History) {
        val location = IntArray(2)
        btnClick.getLocationOnScreen(location)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = inflater.inflate(R.layout.popup_history_more, null)
        popupView.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val popupWindow = PopupWindow(context)
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow.contentView = popupView
        popupWindow.isFocusable = true
        val x = location[0] - ((popupView.width - btnClick.width) / 2)
        val y = location[1] + btnClick.height - 10
        popupWindow.showAtLocation(btnClick, Gravity.NO_GRAVITY, x, y)


        popupView.findViewById<View>(R.id.btn_edit).setOnClickListener {
            popupWindow.dismiss()
            onEdit.invoke(history)
        }
        popupView.findViewById<View>(R.id.btn_delete).setOnClickListener {
            popupWindow.dismiss()
            onDelete.invoke(history)
        }

    }

    override fun getItemCount(): Int {
        return listHistory?.size ?: 0
    }

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var bgValue: LinearLayout

        var tvValue: TextView
        var tvUnit: TextView
        var tvSugarTarget: TextView
        var tvCondition: TextView
        var tvTime: TextView
        var btnMore: RelativeLayout

        init {
            bgValue = itemView.findViewById(R.id.bg_value)
            tvValue = itemView.findViewById(R.id.tv_value)
            tvUnit = itemView.findViewById(R.id.tv_unit)
            tvSugarTarget = itemView.findViewById(R.id.tv_sugar_target)
            tvCondition = itemView.findViewById(R.id.tv_condition)
            tvTime = itemView.findViewById(R.id.tv_time)
            btnMore = itemView.findViewById(R.id.btn_more)
        }
    }

    fun updateData(listNew: ArrayList<History>) {
        listHistory = ArrayList()
        listHistory!!.clear()
        listHistory!!.addAll(listNew)
        notifyDataSetChanged()
    }

}