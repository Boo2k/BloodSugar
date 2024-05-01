package com.example.basekotlin.dialog.unit

import android.content.Context
import com.example.basekotlin.base.BaseDialog
import com.example.basekotlin.base.tap
import com.example.basekotlin.databinding.DialogUnitBinding
import com.example.basekotlin.util.SPUtils
import com.example.basekotlin.util.Utils

class UnitDialog(
    context: Context, cancelAble: Boolean?, var onOk: (Boolean) -> Unit
) : BaseDialog<DialogUnitBinding>(context, cancelAble!!) {

    private var isFlag = true

    override fun setBinding(): DialogUnitBinding {
        return DialogUnitBinding.inflate(layoutInflater)
    }

    override fun initView() {

        setUpUnit(SPUtils.getBoolean(context, Utils.UNIT, true))
    }

    override fun bindView() {
        binding.btnCancel.tap { dismiss() }

        binding.btnOk.tap {
            dismiss()
            onOk.invoke(isFlag)
        }

        binding.btnMgDl.tap {
            setUpUnit(false)
        }

        binding.rdbMgDl.tap {
            setUpUnit(false)
        }

        binding.btnMmolL.tap {
            setUpUnit(true)
        }

        binding.rdbMmolL.tap {
            setUpUnit(true)
        }
    }

    private fun setUpUnit(flag: Boolean) {
        isFlag = flag

        if (flag) {
            binding.rdbMgDl.isChecked = false
            binding.rdbMmolL.isChecked = true
        } else {
            binding.rdbMgDl.isChecked = true
            binding.rdbMmolL.isChecked = false
        }
    }

}