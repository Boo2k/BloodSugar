package com.example.basekotlin.base

import android.app.Dialog
import android.content.Context
import android.view.Window
import androidx.viewbinding.ViewBinding
import com.example.basekotlin.R
import com.example.basekotlin.util.SystemUtil

abstract class BaseDialog<VB : ViewBinding?>(context: Context, canAble: Boolean) :
    Dialog(context, R.style.BaseDialog) {
    var binding: VB

    protected abstract fun setBinding(): VB
    protected abstract fun initView()
    protected abstract fun bindView()

    init {
        SystemUtil.setLocale(context)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = setBinding()
        setContentView(binding!!.root)
        setCancelable(canAble)
        initView()
        bindView()
    }
}