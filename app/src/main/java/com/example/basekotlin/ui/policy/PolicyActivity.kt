package com.example.basekotlin.ui.policy

import android.annotation.SuppressLint
import android.view.View
import com.example.basekotlin.R
import com.example.basekotlin.ads.IsNetWork
import com.example.basekotlin.base.BaseActivity
import com.example.basekotlin.base.tap
import com.example.basekotlin.databinding.ActivityPolicyBinding

class PolicyActivity : BaseActivity<ActivityPolicyBinding>(ActivityPolicyBinding::inflate) {

    private var linkPolicy = ""

    @SuppressLint("SetJavaScriptEnabled")
    override fun initView() {
        binding.viewTop.tvToolBar.text = getString(R.string.privacy_policy)
        binding.viewTop.ivRight.visibility = View.INVISIBLE

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.loadUrl(linkPolicy)

        if (IsNetWork.haveNetworkConnection(this)) {
            binding.webView.visibility = View.VISIBLE
            binding.lnNoInternet.visibility = View.GONE
        } else {
            binding.webView.visibility = View.GONE
            binding.lnNoInternet.visibility = View.VISIBLE
        }
    }

    override fun bindView() {
        binding.viewTop.ivLeft.tap { onBackPressed() }
    }

    override fun onBackPressed() {
        finishThisActivity()
    }
}