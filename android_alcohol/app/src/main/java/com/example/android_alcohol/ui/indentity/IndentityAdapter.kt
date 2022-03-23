package com.example.android_alcohol.ui.indentity

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.Toast
import com.example.android_alcohol.BaseActivity
import com.example.android_alcohol.IndentityActivity
import com.example.android_alcohol.R
import com.example.android_alcohol.apiService.HiRetrofit
import com.example.android_alcohol.dataMoudel.UserInfo
import com.example.android_alcohol.dataMoudel.identityInfo
import kotlinx.android.synthetic.main.activity_intentity.*
import kotlinx.android.synthetic.main.indentity_detail.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IndentityAdapter internal constructor( private val mList: List<UserInfo>, internal var contextActivity: IndentityActivity ) : RecyclerView.Adapter<IndentityAdapter.MyHolder>() {
    val ApiService = HiRetrofit.create(com.example.android_alcohol.apiService.ApiService::class.java)
    //创建ViewHolder并返回，后续item布局里控件都是从ViewHolder中取出
    override fun onCreateViewHolder( parent: ViewGroup, viewType: Int ): MyHolder {

        //将我们自定义的item布局R.layout.item_one转换为View
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.indentity_detail, parent, false)
        //将view传递给我们自定义的ViewHolder
        //返回这个MyHolder实体
        return MyHolder(itemView)
    }

    //通过方法提供的ViewHolder，将数据绑定到ViewHolder中
    override fun onBindViewHolder(holder: MyHolder, position: Int) {

        val indentity_list = mList[position]
        contextActivity.progressBar_indent.visibility = View.VISIBLE
        //选择
        holder.itemView.indentity_choosed.setOnClickListener {
            contextActivity.progressBar_indent.visibility = View.VISIBLE
            val intent_base = Intent(it.context, BaseActivity::class.java)
            ApiService.userInfo(indentity_list.userId,indentity_list.accountId).enqueue(object : Callback<identityInfo> {
                override fun onResponse(call: Call<identityInfo>, responseInn: Response<identityInfo>
                ) {
                    if (responseInn.body()!!.status.toString() == "1") {
                        contextActivity.jumpActivity(responseInn.body()!!.data.token,responseInn.body()!!.data.user.id,responseInn.body()!!.data.user.cname)
                    } else {
                        Toast.makeText(it.context, responseInn.body()?.errmsg.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<identityInfo>, t: Throwable) {
                    Toast.makeText(it.context, "网络连接错误，请稍后重试！", Toast.LENGTH_SHORT).show()
                }
            })
            val intent_order = Intent(it.context, BaseActivity::class.java)
            intent_order.putExtra("userId", indentity_list.userId)
        }

        holder.itemView.cname.text = indentity_list.cname
        holder.itemView.cloridgeId.text = indentity_list.cloridgeId
        if(indentity_list.uType == 0){
            holder.itemView.account_type.text = "根账号";
        }else if(indentity_list.uType == 0 && indentity_list.uLevel == 1){
            holder.itemView.account_type.text = "主账号";
        }else if(indentity_list.uType == 0 && indentity_list.uLevel == 2){
            holder.itemView.account_type.text = "代理商账号";
        }else if(indentity_list.uType == 2){
            holder.itemView.account_type.text = "员工账号";
        }else{
            holder.itemView.account_type.text = "分成账号";
        }
        //售出
//        val sold_out = Gson().fromJson(mList[position].packageDetail, packageInfo::class.java)
//        holder.itemView.sales.text = "${sold_out.value}mL"
    }

    //获取数据源总的条数
    override fun getItemCount(): Int {
//        return 20
        return mList.size
    }



    /**
     * 自定义的ViewHolder
     */
    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cname: TextView
        var cloridgeId: TextView
        var account_type: TextView
        init {
            cname = itemView.findViewById(R.id.cname)
            cloridgeId = itemView.findViewById(R.id.cloridgeId)
            account_type = itemView.findViewById(R.id.account_type)
        }
    }

}