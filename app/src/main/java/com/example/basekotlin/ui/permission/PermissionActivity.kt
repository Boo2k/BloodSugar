package com.example.basekotlin.ui.permission

import android.view.View
import com.example.basekotlin.R
import com.example.basekotlin.base.BaseActivity
import com.example.basekotlin.base.tap
import com.example.basekotlin.databinding.ActivityPermissionBinding
import com.example.basekotlin.ui.main.MainActivity

class PermissionActivity :
    BaseActivity<ActivityPermissionBinding>(ActivityPermissionBinding::inflate) {

    override fun initView() {
        binding.viewTop.ivLeft.visibility = View.INVISIBLE
        binding.viewTop.ivRight.visibility = View.INVISIBLE
        binding.viewTop.tvToolBar.text = getString(R.string.permission)

    }

    override fun bindView() {
        binding.tvContinue.tap {
            nextActivity()
        }
    }

    private fun nextActivity() {
        startNextActivity(MainActivity::class.java, null)
        finish()
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}