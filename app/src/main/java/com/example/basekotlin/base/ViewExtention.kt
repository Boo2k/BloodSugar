package com.example.basekotlin.base

import android.view.View
import com.example.basekotlin.listener.TapNoHandleListener
import com.example.basekotlin.listener.TapListener

fun View.tap(action: (view: View?) -> Unit) {
    setOnClickListener(object : TapListener() {
        override fun onTap(v: View?) {
            action(v)
        }
    })
}

fun View.tapNoHandle(action: (view: View?) -> Unit) {
    setOnClickListener(object : TapNoHandleListener() {
        override fun onTap(v: View?) {
            action(v)
        }
    })
}