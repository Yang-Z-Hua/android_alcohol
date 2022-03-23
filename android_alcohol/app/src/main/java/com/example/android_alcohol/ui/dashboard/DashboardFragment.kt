package com.example.android_alcohol.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android_alcohol.BaseActivity
import com.example.android_alcohol.apiService.HiRetrofit
import com.example.android_alcohol.dataMoudel.ListDetail
import com.example.android_alcohol.dataMoudel.OrderList
import com.example.android_alcohol.R
import kotlinx.android.synthetic.main.fragment_dashboard.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {
    var mList = ArrayList<ListDetail>()
    var totalNum : Int = 0
    var lastVisibleItem: Int? = 0
    var params_token: String = ""
    var params_userId: String = ""
    var params_currentPage: Int = 1
    var params_pageSize: Int = 5
    val ApiService = HiRetrofit.create(com.example.android_alcohol.apiService.ApiService::class.java)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var mCommonAdapter: FinishOrderAdapter? = null
        val main = activity as BaseActivity
        params_token = main!!.token
        params_userId = main!!.userId


        recyclerView_finish.layoutManager = LinearLayoutManager(context)
        //刷新样式
        progressBarSec.visibility = View.VISIBLE
        ApiService.getRepository(params_token,params_userId,"finish","paid",params_currentPage,params_pageSize).enqueue(object :
            Callback<OrderList> {
            override fun onResponse(call: Call<OrderList>, response: Response<OrderList>) {
                totalNum = response.body()!!.data.total
                mList.addAll(response.body()!!.data.list)
                recyclerView_finish.adapter = FinishOrderAdapter(mList)
                recyclerView_finish.adapter?.notifyDataSetChanged()
                progressBarSec.visibility = View.GONE
                swipeRefresh_finish.isRefreshing = false
            }
            override fun onFailure(call: Call<OrderList>, t: Throwable) {
            }
        })
        //下拉刷新
        swipeRefresh_finish.setColorSchemeResources(R.color.alcohol)
        swipeRefresh_finish.setOnRefreshListener {
            ApiService.getRepository(params_token,params_userId,"finish","paid",params_currentPage,params_pageSize).enqueue(object :
                Callback<OrderList> {
                override fun onResponse(call: Call<OrderList>, response: Response<OrderList>) {
                    //下拉刷新重置数据
                    mList = (response.body()!!.data.list as ArrayList<ListDetail>)
                    //重置commonadapter
                    mCommonAdapter = FinishOrderAdapter(mList)

                    recyclerView_finish.adapter = FinishOrderAdapter(mList)

                    recyclerView_finish.adapter?.notifyDataSetChanged()
                    swipeRefresh_finish.isRefreshing = false
                    Toast.makeText(context, "刷新成功！", Toast.LENGTH_SHORT).show()
                }
                override fun onFailure(call: Call<OrderList>, t: Throwable) {
                }
            })
        }
        //  上拉加载
        recyclerView_finish.layoutManager = LinearLayoutManager(context)
        recyclerView_finish.setOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                //触底判断，同时判断数据是否完全加载
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem!! + 1 == mCommonAdapter?.itemCount && totalNum > mCommonAdapter?.itemCount?.toInt() ?: 1) {
                    addData()
                }else{

                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView?.layoutManager as LinearLayoutManager
                //最后一个可见的ITEM
                lastVisibleItem = layoutManager.findLastVisibleItemPosition()
            }
        })

        mCommonAdapter = FinishOrderAdapter(mList)
        recyclerView_finish.adapter = mCommonAdapter

    }

    private fun addData() {
        var checkpage: Int = ((lastVisibleItem!!.toInt()+1)/5)+1
        progressBarSec.visibility = View.VISIBLE
        ApiService.getRepository(params_token,params_userId,"finish","paid",checkpage,params_pageSize).enqueue(object :
            Callback<OrderList> {
            override fun onResponse(call: Call<OrderList>, response: Response<OrderList>) {
                val array_new = (response.body()!!.data.list)
                mList.addAll(array_new)
                recyclerView_finish.adapter?.notifyDataSetChanged()
                progressBarSec.visibility = View.GONE
            }
            override fun onFailure(call: Call<OrderList>, t: Throwable) {
            }
        })
    }
}