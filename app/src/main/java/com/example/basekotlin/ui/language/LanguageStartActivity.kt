package com.example.basekotlin.ui.language

import android.view.View
import android.widget.Toast
import com.example.basekotlin.R
import com.example.basekotlin.base.BaseActivity
import com.example.basekotlin.base.tap
import com.example.basekotlin.databinding.ActivityLanguageStartBinding
import com.example.basekotlin.all.model.LanguageModel
import com.example.basekotlin.ui.intro.IntroActivity
import com.example.basekotlin.ui.language.adapter.LanguageStartAdapter
import com.example.basekotlin.ui.main.MainActivity
import com.example.basekotlin.ui.permission.PermissionActivity
import com.example.basekotlin.util.SharePrefUtils
import com.example.basekotlin.util.SystemUtil
import java.util.Locale

class LanguageStartActivity :
    BaseActivity<ActivityLanguageStartBinding>(ActivityLanguageStartBinding::inflate) {

    private var listLanguage: MutableList<LanguageModel>? = null
    private var codeLang: String? = null
    private var toast: Toast? = null

    override fun initView() {
        initData()
        codeLang = Locale.getDefault().language

        val languageStartAdapter =
            LanguageStartAdapter(this, listLanguage, onLanguage = { code -> codeLang = code })
//        languageStartAdapter.setCheck(SystemUtil.getPreLanguage(this))
        binding.rcvLangStart.adapter = languageStartAdapter
    }

    override fun bindView() {
        binding.ivCheck.tap {
            nextActivity()
        }
    }

    private fun nextActivity() {
        var count = 0
        for (i in 0 until listLanguage!!.size) {
            if (listLanguage!![i].active) {
                count++
                break
            }
        }
        if (count > 0) {
            SystemUtil.saveLocale(this, codeLang)
            startNextActivity(IntroActivity::class.java, null)
            finishAffinity()
        } else {
            if (toast != null) toast!!.cancel()
            toast = Toast.makeText(
                this,
                getString(R.string.please_choose_a_language),
                Toast.LENGTH_SHORT
            )
            toast!!.show()
        }
    }

    private fun initData() {
        listLanguage = ArrayList()
        val lang = Locale.getDefault().language
        listLanguage!!.add(LanguageModel("English", "en", false))
        listLanguage!!.add(LanguageModel("China", "zh", false))
        listLanguage!!.add(LanguageModel("French", "fr", false))
        listLanguage!!.add(LanguageModel("German", "de", false))
        listLanguage!!.add(LanguageModel("Hindi", "hi", false))
        listLanguage!!.add(LanguageModel("Indonesia", "in", false))
        listLanguage!!.add(LanguageModel("Portuguese", "pt", false))
        listLanguage!!.add(LanguageModel("Spanish", "es", false))

        for (i in listLanguage!!.indices) {
            if (listLanguage!![i].code == lang) {
                listLanguage!!.add(0, listLanguage!![i])
                listLanguage!!.removeAt(i + 1)
            }
        }
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}
