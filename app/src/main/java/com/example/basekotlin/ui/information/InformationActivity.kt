package com.example.basekotlin.ui.information

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.example.basekotlin.R
import com.example.basekotlin.all.model.InformationModel
import com.example.basekotlin.base.BaseActivity
import com.example.basekotlin.base.tap
import com.example.basekotlin.databinding.ActivityInformationBinding
import com.example.basekotlin.ui.information.adapter.InformationAdapter
import com.example.basekotlin.util.Utils

class InformationActivity :
    BaseActivity<ActivityInformationBinding>(ActivityInformationBinding::inflate) {

    private var informationAdapter: InformationAdapter? = null
    private var listInformation: ArrayList<InformationModel> = ArrayList()

    override fun initView() {
        binding.viewTop.ivRight.visibility = View.INVISIBLE
        binding.viewTop.tvToolBar.text = getText(R.string.information)

        addData()
    }

    override fun bindView() {
        binding.viewTop.ivLeft.tap { onBackPressed() }
    }

    private fun addData() {
        listInformation = ArrayList()
        listInformation.add(
            InformationModel(
                0,
                R.drawable.information_0,
                getString(R.string.information_0),
                getString(R.string.content_infor_0),
                getString(R.string.link_infor_0)
            )
        )
        listInformation.add(
            InformationModel(
                1,
                R.drawable.information_1,
                getString(R.string.information_1),
                getString(R.string.content_infor_1),
                getString(R.string.link_infor_1)
            )
        )
        listInformation.add(
            InformationModel(
                2,
                R.drawable.information_2,
                getString(R.string.information_2),
                getString(R.string.content_infor_2),
                getString(R.string.link_infor_2)
            )
        )
        listInformation.add(
            InformationModel(
                3,
                R.drawable.information_3,
                getString(R.string.information_3),
                getString(R.string.content_infor_3),
                getString(R.string.link_infor_3)
            )
        )
        listInformation.add(
            InformationModel(
                4,
                R.drawable.information_4,
                getString(R.string.information_4),
                getString(R.string.content_infor_4),
                getString(R.string.link_infor_4)
            )
        )
        listInformation.add(
            InformationModel(
                5,
                R.drawable.information_5,
                getString(R.string.information_5),
                getString(R.string.content_infor_5),
                getString(R.string.link_infor_5)
            )
        )

        binding.rcvInformation.apply {
            informationAdapter =
                InformationAdapter(this@InformationActivity, listInformation, onClick = {
                    val bundle = Bundle()
                    bundle.putInt(Utils.KEY_INFORMATION, it)
                    reLoadAds(InformationShowActivity::class.java, bundle)
                })
            adapter = informationAdapter
        }
    }

    private fun reLoadAds(activity: Class<*>?, bundle: Bundle?) {
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

            }
        }

    override fun onBackPressed() {
        setResult(RESULT_OK)
        finishThisActivity()
    }
}