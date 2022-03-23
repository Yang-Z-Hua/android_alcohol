package com.example.android_alcohol.ui.notifications

import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android_alcohol.R
import com.example.android_alcohol.dataMoudel.ListDetail
import com.example.android_alcohol.dataMoudel.buyerInfoObj
import com.example.android_alcohol.dataMoudel.deskInfoObj
import com.example.android_alcohol.dataMoudel.packageInfo
import com.google.gson.Gson
import kotlinx.android.synthetic.main.order_detail.view.alcohol_choosed
import kotlinx.android.synthetic.main.order_detail.view.brand_name
import kotlinx.android.synthetic.main.order_detail.view.dynamic_name
import kotlinx.android.synthetic.main.order_detail.view.order_time
import kotlinx.android.synthetic.main.order_detail.view.sales
import kotlinx.android.synthetic.main.order_detail.view.user_phone
import java.text.SimpleDateFormat
import java.util.*

class RunningOrderAdapter  internal constructor( private val mList: List<ListDetail> ) : RecyclerView.Adapter<RunningOrderAdapter.MyHolder>() {
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    //创建ViewHolder并返回，后续item布局里控件都是从ViewHolder中取出
    override fun onCreateViewHolder( parent: ViewGroup, viewType: Int ): MyHolder {
        //将我们自定义的item布局R.layout.item_one转换为View
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.running_order_detail, parent, false)
        //将view传递给我们自定义的ViewHolder
        //返回这个MyHolder实体
        return MyHolder(itemView)
    }

    //通过方法提供的ViewHolder，将数据绑定到ViewHolder中
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        //点击事件
//        holder.itemView.setOnClickListener {
//        }

        val order = mList[position]
        holder.itemView.dynamic_name.text = "dynamic_name$position"

        var time: String = format.format(Date(order.payTime))
        holder.itemView.order_time.text = time
        holder.itemView.brand_name.text = order.deviceType
        //桌号信息
        val deskInfo = Gson().fromJson(mList[position].deskInfo, deskInfoObj::class.java)
        holder.itemView.dynamic_name.text = "${deskInfo.deskNo}"
        //售出
        val sold_out = Gson().fromJson(mList[position].packageDetail, packageInfo::class.java)
        holder.itemView.sales.text = "${sold_out.value}mL"
        //用户信息
        val buyerInfo = Gson().fromJson(mList[position].buyerInfo, buyerInfoObj::class.java)
        holder.itemView.user_phone.text = buyerInfo.phone
        holder.itemView.alcohol_choosed.text = order.deviceNo.toString()
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
        var dynamic_name: TextView
        var order_time: TextView
        var brand_name: TextView
        var sales: TextView
        var user_phone: TextView
        var alcohol_choosed: TextView
        var alcohol_inventory: TextView

        init {
            dynamic_name = itemView.findViewById(R.id.dynamic_name)
            order_time = itemView.findViewById(R.id.order_time)
            brand_name = itemView.findViewById(R.id.brand_name)
            sales = itemView.findViewById(R.id.sales)
            user_phone = itemView.findViewById(R.id.user_phone)
            alcohol_choosed = itemView.findViewById(R.id.alcohol_choosed)
            alcohol_inventory = itemView.findViewById(R.id.alcohol_inventory)
        }
    }
}