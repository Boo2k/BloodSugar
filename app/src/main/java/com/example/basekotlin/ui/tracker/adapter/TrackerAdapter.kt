package com.example.basekotlin.ui.tracker.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.basekotlin.R
import com.example.basekotlin.base.tap
import com.example.basekotlin.data.history.History
import com.example.basekotlin.util.SPUtils
import com.example.basekotlin.util.Utils
import java.text.SimpleDateFormat


class TrackerAdapter(
    var context: Context,
    private var listTracker: ArrayList<History>?,
    var onResult: (History) -> Unit,
    var onEdit: (History) -> Unit,
) : RecyclerView.Adapter<TrackerAdapter.TrackerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackerViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return TrackerViewHolder(view)
    }

    @SuppressLint("NewApi", "ResourceType", "SimpleDateFormat")
    override fun onBindViewHolder(holder: TrackerViewHolder, position: Int) {
        val trackerModel: History = listTracker!![position]

        Glide.with(context).asBitmap().load(R.drawable.ic_more_edit).into(holder.ivMore)
        holder.ivMore.setColorFilter(R.color.black)

        var valueUnit = 1

        if (SPUtils.getBoolean(context, Utils.UNIT, true)) {
            holder.tvUnit.text = context.getString(R.string.mmol_l)

            valueUnit *= 1
        } else {
            holder.tvUnit.text = context.getString(R.string.mg_dL)
            valueUnit *= 18
        }

        val valueInput = (trackerModel.valueInput!! * valueUnit * 100f).toInt().toFloat() / 100f

        holder.tvValue.text = "$valueInput"

        holder.tvCondition.text = trackerModel.targetRange!!.condition!!.name

        Log.e("timeDate", "timeDate $position: ${trackerModel.timeDate}")
        holder.tvTime.text = SimpleDateFormat("dd/MM/yyyy ꞏ HH:mm").format(trackerModel.timeDate)

        when (trackerModel.sugarTarget) {
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

        /*if (trackerModel.valueInput!! < trackerModel.targetRange!!.low!!) {
            holder.tvSugarTarget.text = context.getString(R.string.low)
            holder.bgValue.background =
                ContextCompat.getDrawable(context, R.drawable.bg_radius_history_low)
            holder.tvValue.setTextColor(ContextCompat.getColor(context, R.color.color_121268))
            holder.tvUnit.setTextColor(ContextCompat.getColor(context, R.color.color_121268))
        } else if (trackerModel.valueInput!! >= trackerModel.targetRange!!.low!! && trackerModel.valueInput!! < trackerModel.targetRange!!.normal!!) {
            holder.tvSugarTarget.text = context.getString(R.string.normal)
            holder.bgValue.background =
                ContextCompat.getDrawable(context, R.drawable.bg_radius_history_normal)
            holder.tvValue.setTextColor(ContextCompat.getColor(context, R.color.color_001705))
            holder.tvUnit.setTextColor(ContextCompat.getColor(context, R.color.color_001705))
        } else if (trackerModel.valueInput!! >= trackerModel.targetRange!!.normal!! && trackerModel.valueInput!! < trackerModel.targetRange!!.preDiabetes!!) {
            holder.tvSugarTarget.text = context.getString(R.string.pre_diabetes)
            holder.bgValue.background =
                ContextCompat.getDrawable(context, R.drawable.bg_radius_history_pre_diabetes)
            holder.tvValue.setTextColor(ContextCompat.getColor(context, R.color.color_353000))
            holder.tvUnit.setTextColor(ContextCompat.getColor(context, R.color.color_353000))
        } else {
            holder.tvSugarTarget.text = context.getString(R.string.diabetes)
            holder.bgValue.background =
                ContextCompat.getDrawable(context, R.drawable.bg_radius_history_diabetes)
            holder.tvValue.setTextColor(ContextCompat.getColor(context, R.color.color_330000))
            holder.tvUnit.setTextColor(ContextCompat.getColor(context, R.color.color_330000))
        }*/

        holder.itemView.tap { onResult.invoke(trackerModel) }

        holder.btnMore.tap {
            onEdit.invoke(trackerModel)
        }
    }

    override fun getItemCount(): Int {
        return listTracker?.size ?: 0
    }

    class TrackerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var bgValue: LinearLayout

        var tvValue: TextView
        var tvUnit: TextView
        var tvSugarTarget: TextView
        var tvCondition: TextView
        var tvTime: TextView
        var btnMore: RelativeLayout
        var ivMore: ImageView

        init {
            bgValue = itemView.findViewById(R.id.bg_value)
            tvValue = itemView.findViewById(R.id.tv_value)
            tvUnit = itemView.findViewById(R.id.tv_unit)
            tvSugarTarget = itemView.findViewById(R.id.tv_sugar_target)
            tvCondition = itemView.findViewById(R.id.tv_condition)
            tvTime = itemView.findViewById(R.id.tv_time)
            btnMore = itemView.findViewById(R.id.btn_more)
            ivMore = itemView.findViewById(R.id.iv_more)
        }
    }

    fun updateData(listNew: ArrayList<History>) {
        listTracker = ArrayList()
        listTracker!!.clear()
        listTracker!!.addAll(listNew)
        notifyDataSetChanged()
    }

}