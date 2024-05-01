package com.example.basekotlin.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.example.basekotlin.R
import com.example.basekotlin.util.SystemUtil

abstract class BaseActivity<VB : ViewBinding>(val bindingFactory: (LayoutInflater) -> VB) :
    AppCompatActivity() {

    protected val binding: VB by lazy { bindingFactory(layoutInflater) }
    abstract fun initView()
    abstract fun bindView()

    override fun onCreate(savedInstanceState: Bundle?) {
        SystemUtil.setLocale(this)
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        hideFullNavigation()

        initView()
        bindView()
    }

    fun startNextActivity(activity: Class<*>?, bundle: Bundle?) {
        var bundle = bundle
        val intent = Intent(this, activity)
        if (bundle == null) {
            bundle = Bundle()
        }
        intent.putExtras(bundle)
        startActivity(intent)
        overridePendingTransition(R.anim.in_right, R.anim.out_left)
    }

    fun finishThisActivity() {
        finish()
        overridePendingTransition(R.anim.in_left, R.anim.out_right)
    }

    open fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity.currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun hideFullNavigation() {
        try {
            val flags =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            window.decorView.systemUiVisibility = flags
            val decorView = window.decorView
            decorView.setOnSystemUiVisibilityChangeListener { visibility: Int ->
                if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                    decorView.systemUiVisibility = flags
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}