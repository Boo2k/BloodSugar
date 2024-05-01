package com.example.basekotlin.ui.setting

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.example.basekotlin.R
import com.example.basekotlin.base.BaseActivity
import com.example.basekotlin.base.tap
import com.example.basekotlin.databinding.ActivitySettingBinding
import com.example.basekotlin.dialog.rate.RatingDialog
import com.example.basekotlin.dialog.sugartarget.SugarTargetDialog
import com.example.basekotlin.dialog.unit.UnitDialog
import com.example.basekotlin.ui.about.AboutActivity
import com.example.basekotlin.ui.exportfile.ExportFileActivity
import com.example.basekotlin.ui.language.LanguageActivity
import com.example.basekotlin.util.SPUtils
import com.example.basekotlin.util.SharePrefUtils
import com.example.basekotlin.util.SystemUtil
import com.example.basekotlin.util.Utils
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.tasks.Task

class SettingActivity : BaseActivity<ActivitySettingBinding>(ActivitySettingBinding::inflate) {

    private var manager: ReviewManager? = null
    private var reviewInfo: ReviewInfo? = null
    private var ratingDialog: RatingDialog? = null
    private var isUnit = true

    override fun initView() {
        binding.viewTop.ivRight.visibility = View.INVISIBLE
        binding.viewTop.tvToolBar.text = getText(R.string.setting)

        when (SystemUtil.getPreLanguage(this)) {
            "en" -> binding.tvLang.text = resources.getString(R.string.english)
            "pt" -> binding.tvLang.text = resources.getString(R.string.portuguese)
            "es" -> binding.tvLang.text = resources.getString(R.string.spanish)
            "de" -> binding.tvLang.text = resources.getString(R.string.german)
            "fr" -> binding.tvLang.text = resources.getString(R.string.french)
            "zh" -> binding.tvLang.text = resources.getString(R.string.china)
            "hi" -> binding.tvLang.text = resources.getString(R.string.hindi)
            "in" -> binding.tvLang.text = resources.getString(R.string.indonesia)
        }

        if (SharePrefUtils.isRated(this)) {
            binding.btnRateUs.visibility = View.GONE
        }

        isUnit = SPUtils.getBoolean(this, Utils.UNIT, true)
        if (isUnit) {
            binding.tvUnit.text = getString(R.string.mmol_l)
        } else {
            binding.tvUnit.text = getString(R.string.mg_dL)
        }
    }

    override fun bindView() {
        binding.viewTop.ivLeft.tap { onBackPressed() }

        binding.btnSugarTarget.tap { onSugarTarget() }

        binding.btnUnit.tap { onUnit() }

        binding.btnExport.tap { startNextActivity(ExportFileActivity::class.java, null) }

        binding.btnLanguage.tap { startNextActivity(LanguageActivity::class.java, null) }

        binding.btnShare.tap { onShare() }

        binding.btnRateUs.tap { onRateUs() }

        binding.btnFeedback.tap { onFeedback() }

        binding.btnAbout.tap { startNextActivity(AboutActivity::class.java, null) }
    }

    private fun onSugarTarget() {
        val sugarTargetDialog = SugarTargetDialog(this, false)
        sugarTargetDialog.show()
    }

    private fun onUnit() {
        val unitDialog = UnitDialog(this, false, onOk = {
            isUnit = it
            SPUtils.setBoolean(this, Utils.UNIT, it)
        })
        unitDialog.show()
        unitDialog.setOnDismissListener {
            if (isUnit) {
                binding.tvUnit.text = getString(R.string.mmol_l)
            } else {
                binding.tvUnit.text = getString(R.string.mg_dL)
            }
        }
    }

    private fun onShare() {
        val intentShare = Intent(Intent.ACTION_SEND)
        intentShare.type = "text/plain"
        intentShare.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
        intentShare.putExtra(
            Intent.EXTRA_TEXT,
            "Download application :https://play.google.com/store/apps/details?id=$packageName"
        )
        startActivity(Intent.createChooser(intentShare, "Share with"))
    }

    private fun onRateUs() {
        ratingDialog = RatingDialog(this, true, onSend = {
            ratingDialog!!.dismiss()
            val uriText = """
                 mailto:${SharePrefUtils.email}?subject=Review for ${SharePrefUtils.subject}&body=${SharePrefUtils.subject}
                 Rate : ${ratingDialog!!.rating}
                 Content: 
                 """.trimIndent()
            val uri = Uri.parse(uriText)
            val sendIntent = Intent(Intent.ACTION_SENDTO)
            sendIntent.data = uri
            try {
                binding.btnRateUs.visibility = View.GONE
                startActivity(
                    Intent.createChooser(
                        sendIntent, getString(R.string.Send_Email)
                    )
                )
                SharePrefUtils.forceRated(this)
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(
                    this, getString(R.string.There_is_no), Toast.LENGTH_SHORT
                ).show()
            }
        }, onRate = {
            manager = ReviewManagerFactory.create(this)
            val request = manager!!.requestReviewFlow()
            request.addOnCompleteListener { task: Task<ReviewInfo?> ->
                if (task.isSuccessful) {
                    reviewInfo = task.result
                    val flow = manager!!.launchReviewFlow(
                        this, reviewInfo!!
                    )
                    flow.addOnSuccessListener {
                        SharePrefUtils.forceRated(this)
                        ratingDialog!!.dismiss()
                        binding.btnRateUs.visibility = View.GONE
                    }
                } else {
                    ratingDialog!!.dismiss()
                }
            }
        }, onLater = {
            ratingDialog!!.dismiss()
        })
        try {
            ratingDialog!!.show()
        } catch (e: WindowManager.BadTokenException) {
            e.printStackTrace()
        }
    }

    private fun onFeedback() {
        val i = Intent(Intent.ACTION_SEND)
        i.type = "message/rfc822"
        i.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>(SharePrefUtils.email))
        i.putExtra(
            Intent.EXTRA_SUBJECT,
            "[" + resources.getString(R.string.app_name) + "] - " + resources.getString(R.string.feedback)
        )
        try {
            startActivity(Intent.createChooser(i, resources.getString(R.string.send_email)))
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(
                this@SettingActivity,
                getString(R.string.email_clients_not_found),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onBackPressed() {
        setResult(RESULT_OK)
        finishThisActivity()
    }
}