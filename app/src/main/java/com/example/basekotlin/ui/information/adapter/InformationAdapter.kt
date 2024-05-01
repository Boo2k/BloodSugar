package com.example.basekotlin.ui.information.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.basekotlin.R
import com.example.basekotlin.all.model.InformationModel
import com.example.basekotlin.base.tap
import com.makeramen.roundedimageview.RoundedImageView

class InformationAdapter(
    var context: Context,
    private var listInformation: ArrayList<InformationModel>?,
    var onClick: (id: Int) -> Unit
) : RecyclerView.Adapter<InformationAdapter.NoteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_information_short, parent, false)
        return NoteViewHolder(view)
    }

    @SuppressLint("NewApi", "ResourceType")
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val informationModel: InformationModel = listInformation!![position]

        holder.tvTitle.text = informationModel.title
        holder.tvContent.text = informationModel.content
        Glide.with(context).load(informationModel.image).into(holder.ivInformation)

        holder.itemView.tap {
            onClick.invoke(informationModel.id)
        }
    }

    override fun getItemCount(): Int {
        return listInformation?.size ?: 0
    }

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivInformation: RoundedImageView
        var tvTitle: TextView
        var tvContent: TextView

        init {
            ivInformation = itemView.findViewById(R.id.iv_infor_short)
            tvTitle = itemView.findViewById(R.id.tv_title_short)
            tvContent = itemView.findViewById(R.id.tv_content_short)
        }
    }

    fun updateData(listNew: ArrayList<InformationModel>){
        listInformation = ArrayList()
        listInformation!!.addAll(listNew)
        notifyDataSetChanged()
    }

}