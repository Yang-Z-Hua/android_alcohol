package com.example.android_alcohol.ui.home

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.util.Log.e
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.Toast
import com.example.android_alcohol.BaseActivity
import com.example.android_alcohol.apiService.HiRetrofit
import com.example.android_alcohol.R
import com.example.android_alcohol.dataMoudel.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.dialog_content_normal.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.order_detail.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.timerTask

//private val mList: List<*>
class OrderAdapter internal constructor(
    private val mList: List<ListDetail>,
    internal var contextActivity: BaseActivity,
) : RecyclerView.Adapter<OrderAdapter.MyHolder>() {
    val timer = Timer()
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val ApiService =
        HiRetrofit.create(com.example.android_alcohol.apiService.ApiService::class.java)

    //打印相关
    //创建ViewHolder并返回，后续item布局里控件都是从ViewHolder中取出
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        //将我们自定义的item布局R.layout.item_one转换为View
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_detail, parent, false)
        //将view传递给我们自定义的ViewHolder
        //返回这个MyHolder实体
        return MyHolder(itemView)
    }

    fun scrollToPosition(position: Int) {}

    //通过方法提供的ViewHolder，将数据绑定到ViewHolder中
    override fun onBindViewHolder(holder: MyHolder, @SuppressLint("RecyclerView") position: Int) {
        var order = mList[position]


        //获取保存的设备编号及订单编号
        val OrderIdChoosed = contextActivity.getOrderId()
        val DeviceNoChoosed = contextActivity.getDeviceNo()
        val InventoryLeft = contextActivity.getInventoryLeft()
        holder.itemView.dynamic_name.text = "dynamic_name$position"

        var time: String = format.format(Date(order.createtime))
        holder.itemView.order_time.text = time
        holder.itemView.brand_name.text = order.deviceType
        //售出
        val sold_out = Gson().fromJson(mList[position].packageDetail, packageInfo::class.java)
        holder.itemView.sales.text = "${sold_out.value}mL"
        //桌号信息
        val deskInfo = Gson().fromJson(mList[position].deskInfo, deskInfoObj::class.java)
        holder.itemView.dynamic_name.text = "${deskInfo.deskNo}"
//        用户信息
        val buyerInfo = Gson().fromJson(mList[position].buyerInfo, buyerInfoObj::class.java)
        holder.itemView.user_phone.text = buyerInfo.phone
        //判断是否是未出酒订单,并且有保存的订单id，并且订单id就是当前item的id
        if (OrderIdChoosed != "" && OrderIdChoosed == order.id) {
            if (DeviceNoChoosed != "") {
                holder.itemView.alcohol_choosed.text = DeviceNoChoosed
                holder.itemView.alcohol_inventory.text = InventoryLeft + "mL"
                holder.itemView.alcohol_inventory_desx.text = "库存："
            } else {
                holder.itemView.alcohol_choosed.text = "请选择酒机"
            }
        } else if (order.action == "running") {
            holder.itemView.alcohol_action.text = "出酒中"
            holder.itemView.alcohol_choosed.text = "${order.deviceNo}"
        } else {
            holder.itemView.alcohol_choosed.text = "请选择酒机"
        }

        var payway: String = ""
        if (order.payWay == "wechat") {
            payway = "微信支付"
        } else {
            payway = "支付宝支付"
        }
        var paytime: String = format.format(Date(order.payTime))

        //选择酒机按钮
        holder.itemView.select_alcohol.setOnClickListener {
            if (contextActivity.repetition_click == false) {
                return@setOnClickListener
            }
            contextActivity.ChooseAlcohol(order.id, "")
            contextActivity.repetition_click = false
            timer.schedule(timerTask { contextActivity.repetition_click = true }, 500)
            ApiService.chooseDevice("${contextActivity.token}",
                "${contextActivity.userId}",
                "${order.id}").enqueue(object :
                Callback<AlcoholList> {
                override fun onResponse(call: Call<AlcoholList>, response: Response<AlcoholList>) {
                    e("errmsg",response.body()!!.data.toString())
                    if (response.body()!!.status.toString() == "1") {
                        var alcoholList = response.body()!!.data
                        contextActivity.alcohol_choosed = ""
                        if (OrderIdChoosed != "" && OrderIdChoosed == order.id) {
                            if (DeviceNoChoosed != "") {
                                contextActivity.alcohol_choosed = DeviceNoChoosed.toString()
                            }
                        }
                        contextActivity.deviceList = alcoholList as ArrayList<Alcohol>
                        contextActivity.showDialog(alcoholList as ArrayList<Alcohol>)
                        contextActivity.activity_index = position
                    } else {
                        Toast.makeText(contextActivity,
                            response.body()?.errmsg.toString(),
                            Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<AlcoholList>, t: Throwable) {
                    Toast.makeText(contextActivity, "网络连接错误，请稍后重试！", Toast.LENGTH_SHORT).show()
                }
            })

        }
        //出酒按钮
        holder.itemView.alcohol_action.setOnClickListener {
            if (!(OrderIdChoosed != "" && OrderIdChoosed == order.id && DeviceNoChoosed != "")) {
                Toast.makeText(contextActivity, "请选择出酒酒机", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (order.action == "running") {
                Toast.makeText(contextActivity, "正在出酒，请勿重复点击", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            contextActivity.showExit("${order.ruleName}",
                "${deskInfo.deskNo}",
                "${order.id}",
                "${order.deviceType}",
                "${paytime}",
                "${sold_out.price}",
                "${sold_out.value}",
                "${payway}",
                "${DeviceNoChoosed}",
                "$position")

        }

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