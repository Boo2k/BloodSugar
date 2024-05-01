package com.example.basekotlin.dialog.exit

import android.content.Context
import com.example.basekotlin.base.BaseDialog
import com.example.basekotlin.base.tap
import com.example.basekotlin.databinding.DialogExitAppBinding

class ExitAppDialog(context: Context, cancelAble: Boolean?, private var onQuit: () -> Unit) :
    BaseDialog<DialogExitAppBinding>(context, cancelAble!!) {


    override fun setBinding(): DialogExitAppBinding {
        return DialogExitAppBinding.inflate(layoutInflater)
    }

    override fun initView() {}

    override fun bindView() {
        binding.btnCancelQuitApp.tap { dismiss() }

        binding.btnQuitApp.tap {
            dismiss()
            onQuit.invoke()
        }
    }

}