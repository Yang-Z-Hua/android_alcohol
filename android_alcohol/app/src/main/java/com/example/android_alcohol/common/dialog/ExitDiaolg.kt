package com.example.android_alcohol.common.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.example.android_alcohol.R
import kotlinx.android.synthetic.main.dialog_common.*

class ExitDiaolg : Dialog {
    constructor(context: Context) : this(context,0)
    constructor(context: Context, themeResId: Int) : super(context,R.style.dialog){
        setContentView(R.layout.dialog_common)
        window!!.setGravity(Gravity.CENTER)
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
    }
    fun setOnclickListener(listener: View.OnClickListener){
        if (cancle_btn !== null){
            cancle_btn.setOnClickListener(listener)
        }
        if (sure_btn !== null){
            sure_btn.setOnClickListener(listener)
        }

    }
}