package com.example.basekotlin.dialog.rate

import android.content.Context
import android.view.View
import com.example.basekotlin.R
import com.example.basekotlin.base.BaseDialog
import com.example.basekotlin.base.tap
import com.example.basekotlin.databinding.DialogRatingAppBinding

class RatingDialog(
    private val context: Context,
    cancelAble: Boolean?,
    private var onSend: () -> Unit,
    private var onRate: () -> Unit,
    private var onLater: () -> Unit
) : BaseDialog<DialogRatingAppBinding>(context, cancelAble!!) {

    override fun setBinding(): DialogRatingAppBinding {
        return DialogRatingAppBinding.inflate(layoutInflater)
    }

    override fun initView() {
        binding.rtb.setOnRatingBarChangeListener { _, _, _ ->
            when (java.lang.String.valueOf(binding.rtb.rating)) {
                "1.0" -> {
                    binding.btnRateUs.text = context.resources.getString(R.string.thank_you)
                    binding.imgIcon.setImageResource(R.drawable.rating_1)
                }

                "2.0" -> {
                    binding.btnRateUs.text = context.resources.getString(R.string.thank_you)
                    binding.imgIcon.setImageResource(R.drawable.rating_2)
                }

                "3.0" -> {
                    binding.btnRateUs.text = context.resources.getString(R.string.thank_you)
                    binding.imgIcon.setImageResource(R.drawable.rating_3)
                }

                "4.0" -> {
                    binding.btnRateUs.text = context.resources.getString(R.string.thank_you)
                    binding.imgIcon.setImageResource(R.drawable.rating_4)
                }

                "5.0" -> {
                    binding.btnRateUs.text = context.resources.getString(R.string.thank_you)
                    binding.imgIcon.setImageResource(R.drawable.rating_5)
                }

                else -> {
                    binding.btnRateUs.text = context.resources.getString(R.string.rate_us)
                    binding.imgIcon.setImageResource(R.drawable.rating_0)
                }
            }
        }
    }

    override fun bindView() {
        binding.btnRateUs.tap {
            if (binding.rtb.rating <= 3.0) {
                binding.imgIcon.visibility = View.GONE
                onSend.invoke()
            } else {
                binding.imgIcon.visibility = View.VISIBLE
                onRate.invoke()
            }
        }
        binding.btnNotNow.tap { onLater.invoke() }
    }


    val rating: String
        get() = java.lang.String.valueOf(this.binding.rtb.rating)
}