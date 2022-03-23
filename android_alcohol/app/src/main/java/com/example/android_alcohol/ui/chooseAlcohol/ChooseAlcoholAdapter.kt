package com.example.android_alcohol.ui.chooseAlcohol

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android_alcohol.R
import com.example.android_alcohol.dataMoudel.Alcohol
import kotlinx.android.synthetic.main.alcohol_detail.view.*
import java.util.HashMap
import android.graphics.Color
import android.util.Log.e
import com.example.android_alcohol.common.dialog.BtnBottomDialog
import kotlinx.android.synthetic.main.fragment_alcohol.view.*


class ChooseAlcoholAdapter(private val mList: List<Alcohol>, private val btnBottomDialog: BtnBottomDialog, private val deviceNo:String) : RecyclerView.Adapter<ChooseAlcoholAdapter.MyHolder>() {
    private val states = HashMap<Int, Boolean>()

    //创建ViewHolder并返回，后续item布局里控件都是从ViewHolder中取出
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int ): MyHolder {
        //将我们自定义的item布局R.layout.item_one转换为View
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.alcohol_detail, parent, false)
        //将view传递给我们自定义的ViewHolder
        //返回这个MyHolder实体

        return MyHolder(itemView)
    }

    //通过方法提供的ViewHolder，将数据绑定到ViewHolder中
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val alcohol_detail = mList[position]

//        //选择
        holder.itemView.action_alcohol.setOnClickListener {
            if(alcohol_detail.deviceStatus != "在线"){
                btnBottomDialog.showTosast("酒机暂不可用，请选择其他酒机")
                return@setOnClickListener
            }
            btnBottomDialog.choosedItemDevice = alcohol_detail.deviceNo
            btnBottomDialog.choosedItemInventory = alcohol_detail.inventoryLeft
            holder.itemView.alcohol_checked.isChecked = true
            btnBottomDialog.choosedSure()
        }
        holder.itemView.alcohol_checked.setOnClickListener {
            btnBottomDialog.choosedItemDevice = alcohol_detail.deviceNo
            btnBottomDialog.choosedItemInventory = alcohol_detail.inventoryLeft
            btnBottomDialog.choosedSure()
        }
        if (alcohol_detail.deviceStatus == "在线"){
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFFFF"))
            holder.itemView.alcohol_checked.visibility = View.VISIBLE
            if(position == 0){
                holder.itemView.recommend.visibility = View.VISIBLE
            }
            if(deviceNo!=null&&alcohol_detail.deviceNo==deviceNo){
                holder.itemView.alcohol_checked.isChecked = true
            }
        }else{
            holder.itemView.alcohol_name.setTextColor(Color.parseColor("#FFB0B0B0"))
            holder.itemView.alcohol_deviceno.setTextColor(Color.parseColor("#FFB0B0B0"))
            holder.itemView.inventory_name.setTextColor(Color.parseColor("#FFB0B0B0"))
            holder.itemView.alcohol_inventory.setTextColor(Color.parseColor("#FFB0B0B0"))
            holder.itemView.alcohol_unchecked.visibility = View.VISIBLE
            holder.itemView.unusable.visibility = View.VISIBLE
        }
        holder.itemView.alcohol_deviceno.text = alcohol_detail.deviceNo
        holder.itemView.alcohol_inventory.text = "${alcohol_detail.inventoryLeft}mL"
    }

    //获取数据源总的条数
    override fun getItemCount(): Int {
//        return 20
        return mList.size
    }

    private fun clearState() {
        for (i in 0 until itemCount) {
            states.put(i, false)
        }
    }

    private fun setCheckedState(position: Int) {
        states.put(position, true)
    }

    private fun setUnCheckedState(position: Int) {
        states.put(position, false)
    }

    /**
     * 自定义的ViewHolder
     */
    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var alcohol_deviceno: TextView
        var alcohol_inventory: TextView
        init {
            alcohol_deviceno = itemView.findViewById(R.id.alcohol_deviceno)
            alcohol_inventory = itemView.findViewById(R.id.alcohol_deviceno)
        }
    }

}