package com.example.android_alcohol.ui.home

import android.app.Activity
import android.os.Bundle
import android.util.Log.e
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.android_alcohol.BaseActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android_alcohol.apiService.HiRetrofit
import com.example.android_alcohol.dataMoudel.ListDetail
import com.example.android_alcohol.dataMoudel.OrderList
import com.example.android_alcohol.R
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.progressBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

//当前操作的待出酒订单
class HomeFragment : Fragment(R.layout.fragment_home) {

    //数据接收
    var token = ""
    var userId = ""
    var returnedDeviceNo = ""
    var returnedInventoryLeft = ""
    var params_currentPage: Int = 1
    var params_pageSize: Int = 999
    var mList = ArrayList<ListDetail>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        var mCommonAdapter: OrderAdapter? = null
//        progressBar.visibility = View.GONE
        val main = activity as BaseActivity
        token = main!!.token
        userId = main!!.userId
        val activity_index = main!!.activity_index
        //接收选中的酒机
        returnedDeviceNo = main!!.returnedDeviceNo
        returnedInventoryLeft = main!!.returnedInventoryLeft

//        activity.getSystemService(Context.INPUT_METHOD_SERVICE).hi

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = OrderAdapter(mList, main)

        val ApiService = HiRetrofit.create(com.example.android_alcohol.apiService.ApiService::class.java)
        progressBar.visibility = View.VISIBLE
        ApiService.getRepository(token,userId,"no","paid",params_currentPage,params_pageSize).enqueue(object :
            Callback<OrderList> {
            override fun onResponse(call: Call<OrderList>, response: Response<OrderList>) {
                mList = (response.body()!!.data.list as ArrayList<ListDetail>)
                if(activity_index !=-1){
                    mList[activity_index].deviceNo = returnedDeviceNo
                    mList[activity_index].inventoryLeft = returnedInventoryLeft
                }
                mCommonAdapter = OrderAdapter(mList, main)
                recyclerView.adapter = OrderAdapter(mList, main)
                recyclerView.adapter?.notifyDataSetChanged()
                if(activity_index!=-1){
                    recyclerView.scrollToPosition(activity_index)
                }
                progressBar.visibility = View.GONE
                swipeRefresh.isRefreshing = false
            }
            override fun onFailure(call: Call<OrderList>, t: Throwable) {
                progressBar.visibility = View.GONE
            }
        })
        //下拉刷新
        swipeRefresh.setColorSchemeResources(R.color.alcohol)
        swipeRefresh.setOnRefreshListener {
            ApiService.getRepository(token,userId,"no","paid",params_currentPage,params_pageSize).enqueue(object :
                Callback<OrderList> {
                override fun onResponse(call: Call<OrderList>, response: Response<OrderList>) {
                    //下拉刷新重置数据
                    mList = (response.body()!!.data.list as ArrayList<ListDetail>)
                    //重置commonadapter
                    mCommonAdapter = OrderAdapter(mList, main)
                    recyclerView.adapter = OrderAdapter(mList, main)
                    recyclerView.adapter?.notifyDataSetChanged()
                    swipeRefresh.isRefreshing = false
                    Toast.makeText(context, "刷新成功！", Toast.LENGTH_SHORT).show()
                }
                override fun onFailure(call: Call<OrderList>, t: Throwable) {
                }
            })
        }

        mCommonAdapter = OrderAdapter(mList, main)
        recyclerView.adapter = mCommonAdapter
//        initMediaPlayer()


    }



}