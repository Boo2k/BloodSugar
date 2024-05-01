package com.example.basekotlin.dialog.recordexists

import android.content.Context
import com.example.basekotlin.base.BaseDialog
import com.example.basekotlin.base.tap
import com.example.basekotlin.databinding.DialogRecordExistsBinding

class RecordExistsDialog(context: Context, cancelAble: Boolean?, var onClick: () -> Unit) :
    BaseDialog<DialogRecordExistsBinding>(context, cancelAble!!) {


    override fun setBinding(): DialogRecordExistsBinding {
        return DialogRecordExistsBinding.inflate(layoutInflater)
    }

    override fun initView() {}

    override fun bindView() {
        binding.btnNo.tap { dismiss() }

        binding.btnYes.tap {
            onClick.invoke()
            dismiss()
        }
    }

}