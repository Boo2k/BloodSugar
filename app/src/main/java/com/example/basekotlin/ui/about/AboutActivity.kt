package com.example.basekotlin.ui.about

import android.annotation.SuppressLint
import android.view.View
import com.example.basekotlin.BuildConfig
import com.example.basekotlin.R
import com.example.basekotlin.base.BaseActivity
import com.example.basekotlin.base.tap
import com.example.basekotlin.databinding.ActivityAboutBinding
import com.example.basekotlin.ui.policy.PolicyActivity

class AboutActivity : BaseActivity<ActivityAboutBinding>(ActivityAboutBinding::inflate) {

    @SuppressLint("SetTextI18n")
    override fun initView() {
        binding.viewTop.ivRight.visibility = View.INVISIBLE
        binding.viewTop.tvToolBar.text = getText(R.string.about)

        binding.tvVersion.text = getString(R.string.version) + " " + BuildConfig.VERSION_NAME
    }

    override fun bindView() {
        binding.viewTop.ivLeft.tap { onBackPressed() }

        binding.tvPrivacyPolicy.tap { startNextActivity(PolicyActivity::class.java, null) }
    }

    override fun onBackPressed() {
        finishThisActivity()
    }
}