package com.example.basekotlin.ui.intro

import android.view.View
import android.widget.ImageView
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.example.basekotlin.R
import com.example.basekotlin.base.BaseActivity
import com.example.basekotlin.base.tapNoHandle
import com.example.basekotlin.databinding.ActivityIntroBinding
import com.example.basekotlin.ui.intro.adapter.IntroAdapter
import com.example.basekotlin.ui.main.MainActivity
import com.example.basekotlin.ui.permission.PermissionActivity
import com.example.basekotlin.util.SharePrefUtils

class IntroActivity : BaseActivity<ActivityIntroBinding>(ActivityIntroBinding::inflate) {

    private var dots: Array<ImageView>? = null
    private var introAdapter: IntroAdapter? = null

    override fun initView() {
        dots = arrayOf(binding.ivCircle01, binding.ivCircle02, binding.ivCircle03)
        introAdapter = IntroAdapter(this)

        binding.viewPager2.adapter = introAdapter

        binding.viewPager2.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int, positionOffset: Float, positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                changeContentInit(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    override fun bindView() {
        binding.btnNext.tapNoHandle {
            binding.viewPager2.currentItem = binding.viewPager2.currentItem + 1
        }
        binding.btnStart.tapNoHandle { nextActivity() }
    }

    private fun changeContentInit(position: Int) {
        for (i in 0..2) {
            if (i == position) dots!![i].setImageResource(R.drawable.ic_intro_s) else dots!![i].setImageResource(
                R.drawable.ic_intro_sn
            )
        }
        when (position) {
            0, 1 -> {
                binding.btnNext.visibility = View.VISIBLE
                binding.btnStart.visibility = View.GONE
            }

            2 -> {
                binding.btnNext.visibility = View.GONE
                binding.btnStart.visibility = View.VISIBLE
            }
        }
    }

    private fun nextActivity() {
        if (SharePrefUtils.getCountOpenApp(this) > 3) {
            startNextActivity(MainActivity::class.java, null)
        } else {
            startNextActivity(PermissionActivity::class.java, null)
        }
        finish()
    }

    override fun onBackPressed() {
        finishAffinity()
    }

    override fun onStart() {
        super.onStart()
        changeContentInit(binding.viewPager2.currentItem)
    }
}