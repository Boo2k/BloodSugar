package com.example.basekotlin.ui.information

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.example.basekotlin.R
import com.example.basekotlin.all.model.InformationModel
import com.example.basekotlin.base.BaseActivity
import com.example.basekotlin.base.tap
import com.example.basekotlin.databinding.ActivityInformationShowBinding
import com.example.basekotlin.ui.information.adapter.InformationAdapter
import com.example.basekotlin.util.Utils

class InformationShowActivity :
    BaseActivity<ActivityInformationShowBinding>(ActivityInformationShowBinding::inflate) {

    private var informationAdapter: InformationAdapter? = null
    private var listInformation: ArrayList<InformationModel> = ArrayList()
    private var idItem = 0

    override fun initView() {
        binding.viewTop.ivRight.visibility = View.INVISIBLE
        binding.viewTop.tvToolBar.visibility = View.INVISIBLE
        binding.viewTop.tvToolBar.text = getText(R.string.information)

        idItem = intent.getIntExtra(Utils.KEY_INFORMATION, 0)


        listInformation = ArrayList()
        binding.rcvInformation.apply {
            informationAdapter =
                InformationAdapter(this@InformationShowActivity, listInformation, onClick = {
                    idItem = it
                    addData()

                    /*setResult(RESULT_OK)
                    val bundle = Bundle()
                    bundle.putInt(Utils.KEY_INFORMATION, it)
                    startNextActivity(InformationShowActivity::class.java, bundle)
                    finish()*/
                })
            adapter = informationAdapter
        }

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

        binding.ivInformation.background =
            ContextCompat.getDrawable(this, listInformation[idItem].image)
        binding.itemFull.tvTitle.text = listInformation[idItem].title
        binding.itemFull.tvContent.text = listInformation[idItem].content
        binding.itemFull.tvLink.text = listInformation[idItem].link
        listInformation.removeAt(idItem)

        informationAdapter!!.updateData(listInformation)

        binding.nestedScrollView.scrollTo(0, 0)
    }

    override fun onBackPressed() {
        setResult(RESULT_OK)
        finishThisActivity()
    }
}