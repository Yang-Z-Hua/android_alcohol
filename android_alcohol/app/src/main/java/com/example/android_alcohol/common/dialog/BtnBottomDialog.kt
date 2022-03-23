package com.example.android_alcohol.common.dialog

import com.google.android.material.bottomsheet.BottomSheetDialog

import android.os.Bundle

import android.app.Dialog
import android.util.Log.e

import com.google.android.material.bottomsheet.BottomSheetDialogFragment

import android.view.LayoutInflater

import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.FragmentManager
import com.example.android_alcohol.R

import com.example.android_alcohol.dataMoudel.Alcohol

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager

import androidx.recyclerview.widget.DividerItemDecoration
import com.example.android_alcohol.ui.chooseAlcohol.ChooseAlcoholAdapter
//import kotlinx.android.synthetic.main.activity_choose.*
import android.view.animation.Animation
import android.widget.FrameLayout
import android.widget.Toast
import com.example.android_alcohol.BaseActivity
import com.example.android_alcohol.apiService.HiRetrofit
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.fragment_alcohol.view.*
import java.util.*


class BtnBottomDialog : BottomSheetDialogFragment() {
    internal var view: View? = null
    private var alcoholList = ArrayList<Alcohol>()
    private var code: String? = null
    var choosedItemDevice: String? = null
    var choosedItemInventory: Int? = null

    //    private var choosedItemDevice: String? = null
    val ApiService =
        HiRetrofit.create(com.example.android_alcohol.apiService.ApiService::class.java)
    val timer = Timer()
    override fun onStart() {
        super.onStart()
        // 获取dialog对象
        val dialog = dialog
        // 获取dialog的根布局
        val bottomSheet = dialog?.window?.findViewById<FrameLayout>(R.id.design_bottom_sheet)
        // 把windows的默认背景颜色去掉，不然圆角看不见
//        bottomSheet?.background = ColorDrawable(Color.TRANSPARENT)
        val height =
            (resources.displayMetrics.heightPixels * 0.6).toInt() //屏幕高的60%
        // 获取根布局的LayoutParams对象
        val layoutParams = bottomSheet?.layoutParams
        // 修改弹窗的最大高度，不允许上滑
        layoutParams?.height = height
        bottomSheet?.layoutParams = layoutParams
        val behavior = BottomSheetBehavior.from(bottomSheet!!)
        behavior.peekHeight = height
        // 初始化为展开状态(默认为展开状态)
        // BottomSheetBehavior.STATE_HIDDEN：对应为隐藏状态
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (activity == null) return super.onCreateDialog(savedInstanceState)
        val dialog =
            BottomSheetDialog(requireActivity(), R.style.Theme_MaterialComponents_BottomSheetDialog)

        val root: View = LayoutInflater.from(activity).inflate(R.layout.fragment_alcohol, null)
        dialog.setContentView(root)
        val height = (resources.displayMetrics.heightPixels * 0.6).toInt() //屏幕高的60%
        //设置宽度
        val params = root.layoutParams
        params.height = height
        root.layoutParams = params
        val window = dialog.window
//        window?.setWindowAnimations(R.style.BottomSheet)
        return dialog
    }

    fun setData(codes: ArrayList<Alcohol>) {
        alcoholList = codes
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_alcohol, null)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView_alcohol)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        var contextActivity = activity as BaseActivity
        alcoholList = contextActivity.deviceList
        var deviceNo = contextActivity.alcohol_choosed

        if (alcoholList != null && alcoholList.size > 0) {
            recyclerView.adapter = ChooseAlcoholAdapter(alcoholList, this,deviceNo)
        }

//        view.choosed_sure.setOnClickListener {
//            if(choosedItemDevice==null||choosedItemDevice==""){
//                Toast.makeText(context,"请选择出酒酒机",Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
////            contextActivity.returnedDeviceNo = choosedItemDevice.toString()
////            contextActivity.returnedInventoryLeft = choosedItemInventory.toString()
//            contextActivity.getDeviceData(choosedItemDevice.toString(),choosedItemInventory.toString())
//            dismiss()
//        }
//        view.choosed_cancel.setOnClickListener {
//            dismiss()
//        }
        return view
    }

    fun choosedSure() {
        if (choosedItemDevice == null || choosedItemDevice == "") {
            Toast.makeText(context, "请选择出酒酒机", Toast.LENGTH_SHORT).show()
            return
        }
        var contextActivity = activity as BaseActivity
        contextActivity.getDeviceData(choosedItemDevice.toString(), choosedItemInventory.toString())
        dismiss()
    }

    fun showTosast(msg:String){
        Toast.makeText(context, "$msg", Toast.LENGTH_SHORT).show()
    }

//    override fun onResume() {
//        super.onResume()
//        //动态设置宽高
//        dialog!!.window!!.setLayout(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.MATCH_PARENT
//        )
//    }


    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return super.onCreateAnimation(transit, enter, nextAnim)
    }


    interface ClickCallBack {
        fun onItemClick(code: String?)
    }

    override fun show(manager: FragmentManager, @Nullable tag: String?) {
        //在show之前设置弹出动画
        super.show(manager, tag)
        //在show之后设置关闭动画
    }

    override fun dismiss() {
        super.dismiss()
    }

}

