package com.example.basekotlin.dialog.delete

import android.content.Context
import com.example.basekotlin.base.BaseDialog
import com.example.basekotlin.base.tap
import com.example.basekotlin.databinding.DialogDeleteRecordBinding

class DeleteRecordDialog(
    context: Context, cancelAble: Boolean?, var onClick: () -> Unit
) : BaseDialog<DialogDeleteRecordBinding>(context, cancelAble!!) {


    override fun setBinding(): DialogDeleteRecordBinding {
        return DialogDeleteRecordBinding.inflate(layoutInflater)
    }

    override fun initView() {
    }

    override fun bindView() {
        binding.btnCancel.tap { dismiss() }

        binding.btnOk.tap {
            dismiss()
            onClick.invoke()
        }

    }

}