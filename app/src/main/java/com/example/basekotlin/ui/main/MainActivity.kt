package com.example.basekotlin.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.basekotlin.R
import com.example.basekotlin.base.BaseActivity
import com.example.basekotlin.base.tap
import com.example.basekotlin.data.history.History
import com.example.basekotlin.databinding.ActivityMainBinding
import com.example.basekotlin.dialog.exit.ExitAppDialog
import com.example.basekotlin.ui.information.InformationActivity
import com.example.basekotlin.ui.main.adapter.MainAdapter
import com.example.basekotlin.ui.record.NewRecordActivity
import com.example.basekotlin.ui.setting.SettingActivity
import com.example.basekotlin.util.DatabaseManager
import com.example.basekotlin.util.SharePrefUtils

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private var position = 0

    override fun initView() {
        checkFirstOpen()

        setUpToolBar()
        setUpViewPager()
        setPage(0)
    }

    override fun bindView() {
        binding.viewTop.ivLeft.tap { reLoadAds(SettingActivity::class.java, null) }

        binding.viewTop.ivRight.tap { reLoadAds(InformationActivity::class.java, null) }

        binding.btnAddData.tap { reLoadAds(NewRecordActivity::class.java, null) }

        binding.btnTracker.tap {
            if (position != 0) {
                setUpViewPager()
                setPage(0)
                binding.viewFirstOpen.visibility = View.GONE
            }
        }

        binding.btnHistory.tap {
            if (position != 1) {
                setUpViewPager()
                setPage(1)
                binding.viewFirstOpen.visibility = View.GONE
            }
        }

        binding.viewFirstOpen.tap {
            binding.viewFirstOpen.visibility = View.GONE
        }

    }

    private fun setPage(pos: Int) {
        position = pos

        binding.viewPager.setCurrentItem(pos, false)

        binding.ivTracker.setColorFilter(
            ContextCompat.getColor(this, R.color.color_us)
        )
        binding.ivHistory.setColorFilter(
            ContextCompat.getColor(this, R.color.color_us)
        )

        when (pos) {
            0 -> {
                binding.viewTop.tvToolBar.text = getString(R.string.blood_sugar)

                binding.ivTracker.setColorFilter(
                    ContextCompat.getColor(
                        this, R.color.color_s
                    )
                )
            }

            1 -> {
                binding.viewTop.tvToolBar.text = getString(R.string.history)

                binding.ivHistory.setColorFilter(
                    ContextCompat.getColor(
                        this, R.color.color_s
                    )
                )
            }
        }
    }

    private fun setUpToolBar() {
        binding.viewTop.ivLeft.background = ContextCompat.getDrawable(this, R.drawable.ic_setting)
        binding.viewTop.ivRight.background =
            ContextCompat.getDrawable(this, R.drawable.ic_infomation)
        binding.viewTop.tvToolBar.text = getString(R.string.blood_sugar)
    }

    private fun setUpViewPager() {
        binding.viewPager.offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT

        val mainAdapter = MainAdapter(supportFragmentManager, lifecycle)
        binding.viewPager.adapter = mainAdapter
        binding.viewPager.isUserInputEnabled = false
    }

    private fun checkFirstOpen() {
        val listHistory: ArrayList<History> = ArrayList()
        listHistory.addAll(DatabaseManager.getAllHistory())
        if (listHistory.size == 0 && SharePrefUtils.getCountOpenApp(this) == 1) {
            binding.viewFirstOpen.visibility = View.VISIBLE
        } else {
            binding.viewFirstOpen.visibility = View.GONE
        }
    }

    fun reLoadAds(activity: Class<*>?, bundle: Bundle?) {
        var bundle = bundle
        val intent = Intent(this, activity)
        if (bundle == null) {
            bundle = Bundle()
        }
        intent.putExtras(bundle)
        resultLauncher.launch(intent)
        overridePendingTransition(R.anim.in_right, R.anim.out_left)
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                //load ads here

                setUpViewPager()
                setPage(position)
            }
        }

    override fun onBackPressed() {
        val exitAppDialog = ExitAppDialog(this,false, onQuit = {
            finishAffinity()
        })
        exitAppDialog.show()
    }

}
