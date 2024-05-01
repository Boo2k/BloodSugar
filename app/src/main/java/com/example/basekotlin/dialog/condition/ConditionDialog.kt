package com.example.basekotlin.dialog.condition

import android.content.Context
import com.example.basekotlin.R
import com.example.basekotlin.base.BaseDialog
import com.example.basekotlin.base.tap
import com.example.basekotlin.databinding.DialogConditionBinding
import com.example.basekotlin.util.SPUtils


class ConditionDialog(
    context: Context, cancelAble: Boolean?, var onClick: (Int) -> Unit
) : BaseDialog<DialogConditionBinding>(context, cancelAble!!) {

    override fun setBinding(): DialogConditionBinding {
        return DialogConditionBinding.inflate(layoutInflater)
    }

    override fun initView() {
        val isAllType = SPUtils.getBoolean(context, "all_type", false)
        val conditions = if (isAllType) {
            arrayOf(
                context.getString(R.string.All_type),
                context.getString(R.string.Default),
                context.getString(R.string.Fasting),
                context.getString(R.string.Before_a_meal),
                context.getString(R.string.After_a_meal_1_2h),
                context.getString(R.string.Asleep),
                context.getString(R.string.Before_exercise),
                context.getString(R.string.After_exercise)
            )
        } else {
            arrayOf(
                context.getString(R.string.Default),
                context.getString(R.string.Fasting),
                context.getString(R.string.Before_a_meal),
                context.getString(R.string.After_a_meal_1_2h),
                context.getString(R.string.Asleep),
                context.getString(R.string.Before_exercise),
                context.getString(R.string.After_exercise)
            )
        }

        if (isAllType){
            binding.numberPicker.maxValue = 7
        }else{
            binding.numberPicker.maxValue = 6
        }

        binding.numberPicker.displayedValues = conditions
    }

    override fun bindView() {
        binding.btnCancel.tap { dismiss() }

        binding.btnOk.tap {
            dismiss()
            onClick.invoke(binding.numberPicker.value)
        }

    }


}