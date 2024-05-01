package com.example.basekotlin.dialog.sugartarget

import android.annotation.SuppressLint
import android.content.Context
import com.example.basekotlin.base.BaseDialog
import com.example.basekotlin.base.tap
import com.example.basekotlin.databinding.DialogSugarTargetBinding
import com.example.basekotlin.util.SPUtils
import com.example.basekotlin.util.Utils

class SugarTargetDialog(context: Context, cancelAble: Boolean?) :
    BaseDialog<DialogSugarTargetBinding>(context, cancelAble!!) {

    private var low = 0.0F
    private var normal = 0.0F
    private var preDiabetes = 0.0F

    override fun setBinding(): DialogSugarTargetBinding {
        return DialogSugarTargetBinding.inflate(layoutInflater)
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        if (SPUtils.getBoolean(context, Utils.UNIT, true)) {
            low = 4.0F
            normal = 5.5F
            preDiabetes = 7.0F
        } else {
            low = 72.0F
            normal = 99.0F
            preDiabetes = 126.0F
        }

        binding.tvLow.text = "<$low"
        binding.tvNormal.text = "$low - $normal"
        binding.tvPreDiabetes.text = "$normal - $preDiabetes"
        binding.tvNormal.text = ">$preDiabetes"
    }

    override fun bindView() {
        binding.ivClose.tap { dismiss() }

    }

}