package com.example.basekotlin.dialog.datetime

import android.content.Context
import android.widget.Toast
import com.example.basekotlin.R
import com.example.basekotlin.base.BaseDialog
import com.example.basekotlin.base.tap
import com.example.basekotlin.databinding.DialogDateTimeBinding
import java.util.Calendar
import java.util.Date

class DateTimeDialog(
    context: Context, cancelAble: Boolean?, var onClick: (Date) -> Unit
) : BaseDialog<DialogDateTimeBinding>(context, cancelAble!!) {

    override fun setBinding(): DialogDateTimeBinding {
        return DialogDateTimeBinding.inflate(layoutInflater)
    }

    override fun initView() {

    }

    override fun bindView() {
        binding.btnCancel.tap { dismiss() }

        binding.btnOk.tap {
            if (binding.dateTime.date <= Calendar.getInstance().time) {
                onClick.invoke(
                    binding.dateTime.date
                )
                dismiss()
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.you_cannot_choose_a_date_in_the_future),
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
    }
}