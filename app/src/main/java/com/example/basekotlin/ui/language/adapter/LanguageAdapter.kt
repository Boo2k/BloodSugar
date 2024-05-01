package com.example.basekotlin.ui.language.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.basekotlin.R
import com.example.basekotlin.base.tap
import com.example.basekotlin.all.model.LanguageModel

class LanguageAdapter(
    var context: Context,
    private var languageModelList: List<LanguageModel>?,
    var onLanguage: (String) -> Unit
) : RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_language, parent, false)
        return LanguageViewHolder(view)
    }

    @SuppressLint("NewApi")
    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        val languageModel: LanguageModel = languageModelList!![position]

        holder.rdbCheck.isChecked = languageModel.active
        holder.tvLang.text = languageModel.name

        if (languageModel.active) {
            holder.layoutItem.setBackgroundResource(R.drawable.bg_lang_item_s)
        } else {
            holder.layoutItem.setBackgroundResource(R.drawable.bg_lang_item_sn)
        }

        when (languageModel.code) {
            "fr" -> Glide.with(context).asBitmap().load(R.drawable.ic_lang_fr).into(holder.icLang)
            "es" -> Glide.with(context).asBitmap().load(R.drawable.ic_lang_es).into(holder.icLang)
            "zh" -> Glide.with(context).asBitmap().load(R.drawable.ic_lang_zh).into(holder.icLang)
            "in" -> Glide.with(context).asBitmap().load(R.drawable.ic_lang_in).into(holder.icLang)
            "hi" -> Glide.with(context).asBitmap().load(R.drawable.ic_lang_hi).into(holder.icLang)
            "de" -> Glide.with(context).asBitmap().load(R.drawable.ic_lang_ge).into(holder.icLang)
            "pt" -> Glide.with(context).asBitmap().load(R.drawable.ic_lang_pt).into(holder.icLang)
            "en" -> Glide.with(context).asBitmap().load(R.drawable.ic_lang_en).into(holder.icLang)
        }

        holder.layoutItem.tap {
            setCheck(languageModel.code)
            onLanguage.invoke(languageModel.code)
            notifyDataSetChanged()
        }

        holder.rdbCheck.tap {
            setCheck(languageModel.code)
            onLanguage.invoke(languageModel.code)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return languageModelList?.size ?: 0
    }

    class LanguageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rdbCheck: RadioButton
        val tvLang: TextView
        val layoutItem: LinearLayout
        val icLang: ImageView

        init {
            rdbCheck = itemView.findViewById(R.id.rdbCheck)
            icLang = itemView.findViewById(R.id.icLang)
            tvLang = itemView.findViewById(R.id.tvLang)
            layoutItem = itemView.findViewById(R.id.layoutItem)
        }
    }

     fun setCheck(code: String?) {
        for (item in languageModelList!!) {
            item.active = item.code == code
        }
        notifyDataSetChanged()
    }
}