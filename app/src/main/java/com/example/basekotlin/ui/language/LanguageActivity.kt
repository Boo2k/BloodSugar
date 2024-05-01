package com.example.basekotlin.ui.language

import com.example.basekotlin.R
import com.example.basekotlin.base.BaseActivity
import com.example.basekotlin.base.tap
import com.example.basekotlin.databinding.ActivityLanguageBinding
import com.example.basekotlin.all.model.LanguageModel
import com.example.basekotlin.ui.language.adapter.LanguageAdapter
import com.example.basekotlin.ui.main.MainActivity
import com.example.basekotlin.util.SystemUtil
import java.util.Locale

class LanguageActivity : BaseActivity<ActivityLanguageBinding>(ActivityLanguageBinding::inflate) {

    private var listLanguage: MutableList<LanguageModel>? = null
    private var codeLang: String? = null

    override fun initView() {
        initData()
        codeLang = Locale.getDefault().language
        binding.viewTop.tvToolBar.text = getString(R.string.language)

        val languageAdapter =
            LanguageAdapter(this, listLanguage, onLanguage = { code -> codeLang = code })
        languageAdapter.setCheck(SystemUtil.getPreLanguage(this))
        binding.rcvLang.adapter = languageAdapter
    }

    override fun bindView() {
        binding.viewTop.ivLeft.tap { onBackPressed() }

        binding.viewTop.ivRight.tap {
            SystemUtil.saveLocale(this, codeLang)
            startNextActivity(MainActivity::class.java, null)
            finishAffinity()
        }

    }

    private fun initData() {
        listLanguage = ArrayList()
        listLanguage!!.add(LanguageModel("English", "en", false))
        listLanguage!!.add(LanguageModel("China", "zh", false))
        listLanguage!!.add(LanguageModel("French", "fr", false))
        listLanguage!!.add(LanguageModel("German", "de", false))
        listLanguage!!.add(LanguageModel("Hindi", "hi", false))
        listLanguage!!.add(LanguageModel("Indonesia", "in", false))
        listLanguage!!.add(LanguageModel("Portuguese", "pt", false))
        listLanguage!!.add(LanguageModel("Spanish", "es", false))
    }

    override fun onBackPressed() {
        finishThisActivity()
    }
}