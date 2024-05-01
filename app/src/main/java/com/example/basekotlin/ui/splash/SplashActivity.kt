package com.example.basekotlin.ui.splash

import android.os.Handler
import com.example.basekotlin.base.BaseActivity
import com.example.basekotlin.databinding.ActivitySplashBinding
import com.example.basekotlin.ui.language.LanguageStartActivity
import com.example.basekotlin.ui.main.MainActivity
import com.example.basekotlin.util.DatabaseManager
import com.example.basekotlin.util.SharePrefUtils

class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {

    override fun initView() {
        SharePrefUtils.increaseCountOpenApp(this)

        DatabaseManager.initDatabase(this)

        Handler(mainLooper).postDelayed({
            nextActivity()
        }, 3000)
    }

    override fun bindView() {}

    private fun nextActivity() {
        if (SharePrefUtils.getCountOpenApp(this) > 0) {
            startNextActivity(MainActivity::class.java, null)
        } else {
            startNextActivity(LanguageStartActivity::class.java, null)
        }
        finish()
    }

    override fun onBackPressed() {}
}