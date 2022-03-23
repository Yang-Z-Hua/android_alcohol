package com.example.android_alcohol.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android_alcohol.BaseActivity
import com.example.android_alcohol.R
import com.example.android_alcohol.apiService.HiRetrofit
import com.example.android_alcohol.dataMoudel.ListDetail
import com.example.android_alcohol.dataMoudel.OrderList
import com.example.android_alcohol.databinding.FragmentNotificationsBinding
import com.example.android_alcohol.ui.dashboard.FinishOrderAdapter
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_dashboard.swipeRefresh_finish
import kotlinx.android.synthetic.main.fragment_notifications.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class NotificationsFragment : Fragment(R.layout.fragment_notifications) {
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

        var mCommonAdapter: RunningOrderAdapter? = null
        val main = activity as BaseActivity
        params_token = main!!.token
        params_userId = main!!.userId


        recyclerView_running.layoutManager = LinearLayoutManager(context)
        //刷新样式
        progressBarThr.visibility = View.VISIBLE
        ApiService.getRepository(params_token,params_userId,"running","paid",params_currentPage,params_pageSize).enqueue(object :
            Callback<OrderList> {
            override fun onResponse(call: Call<OrderList>, response: Response<OrderList>) {
                totalNum = response.body()!!.data.total
                mList.addAll(response.body()!!.data.list)
                recyclerView_running.adapter = RunningOrderAdapter(mList)
                recyclerView_running.adapter?.notifyDataSetChanged()
                progressBarThr.visibility = View.GONE
                swipeRefresh_finish.isRefreshing = false
            }
            override fun onFailure(call: Call<OrderList>, t: Throwable) {
            }
        })
        //下拉刷新
        swipeRefresh_finish.setColorSchemeResources(R.color.alcohol)
        swipeRefresh_finish.setOnRefreshListener {
            ApiService.getRepository(params_token,params_userId,"running","paid",params_currentPage,params_pageSize).enqueue(object :
                Callback<OrderList> {
                override fun onResponse(call: Call<OrderList>, response: Response<OrderList>) {
                    //下拉刷新重置数据
                    mList = (response.body()!!.data.list as ArrayList<ListDetail>)
                    //重置commonadapter
                    mCommonAdapter = RunningOrderAdapter(mList)

                    recyclerView_running.adapter = RunningOrderAdapter(mList)

                    recyclerView_running.adapter?.notifyDataSetChanged()
                    swipeRefresh_finish.isRefreshing = false
                    Toast.makeText(context, "刷新成功！", Toast.LENGTH_SHORT).show()
                }
                override fun onFailure(call: Call<OrderList>, t: Throwable) {
                }
            })
        }
        //  上拉加载
        recyclerView_running.layoutManager = LinearLayoutManager(context)
        recyclerView_running.setOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                //触底判断，同时判断数据是否完全加载
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem!! + 1 == mCommonAdapter?.itemCount && totalNum > mCommonAdapter?.itemCount?.toInt() ?: 1) {
                    addData()
                }else{
//                    Toast.makeText(context, "订单已全部加载！", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView?.layoutManager as LinearLayoutManager
                //最后一个可见的ITEM
                lastVisibleItem = layoutManager.findLastVisibleItemPosition()
            }
        })

        mCommonAdapter = RunningOrderAdapter(mList)
        recyclerView_running.adapter = mCommonAdapter

    }

    private fun addData() {
        var checkpage: Int = ((lastVisibleItem!!.toInt()+1)/5)+1
        progressBarThr.visibility = View.VISIBLE
        ApiService.getRepository(params_token,params_userId,"running","paid",checkpage,params_pageSize).enqueue(object :
            Callback<OrderList> {
            override fun onResponse(call: Call<OrderList>, response: Response<OrderList>) {
                val array_new = (response.body()!!.data.list)
                mList.addAll(array_new)
                recyclerView_running.adapter?.notifyDataSetChanged()
                progressBarThr.visibility = View.GONE
            }
            override fun onFailure(call: Call<OrderList>, t: Throwable) {
            }
        })
    }
}