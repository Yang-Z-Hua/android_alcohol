package com.example.android_alcohol
import androidx.appcompat.app.AppCompatActivity

class ChooseActivity : AppCompatActivity() {
//    val ApiService = HiRetrofit.create(com.example.android_alcohol.apiService.ApiService::class.java)
//    var mList = ArrayList<Alcohol>()
//    var token:String = ""
//    var userId:String = ""
//    var orderId:String = ""
//    var brandName:String = ""
//    var alcohol_index:Int = -1
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_choose)
//        recyclerView_alcohol.layoutManager = LinearLayoutManager(this)
////        recyclerView_alcohol.adapter = ChooseAlcoholAdapter(mList, this)
//        token = intent.getStringExtra("token")!!
//        userId = intent.getStringExtra("userId")!!
//        orderId = intent.getStringExtra("orderId")!!
//        brandName = intent.getStringExtra("brandName")!!
//        alcohol_brandname.text = brandName
//        ApiService.chooseDevice(token!!,userId!!,orderId).enqueue(object :
//            Callback<AlcoholList> {
//            override fun onResponse(call: Call<AlcoholList>, response: Response<AlcoholList>) {
//                if (response.body()!!.status.toString() == "1"){
//                    mList.addAll(response.body()!!.data)
//                    recyclerView_alcohol.adapter?.notifyDataSetChanged()
//                }else{
//                    Toast.makeText(this@ChooseActivity, response.body()?.errmsg.toString(), Toast.LENGTH_SHORT).show()
//                }
//            }
//            override fun onFailure(call: Call<AlcoholList>, t: Throwable) {
//                Toast.makeText(this@ChooseActivity, "网络连接错误，请稍后重试！", Toast.LENGTH_SHORT).show()
//            }
//        })
//
//        choosed_sure.setOnClickListener {
//
//            if(alcohol_index==-1){
//                Toast.makeText(this, "请选择酒机！", Toast.LENGTH_SHORT).show()
//            }else{
//                var alcoholObj = mList[alcohol_index]
//                //创建一个用来存储返回数据的intent
//                val intentData = Intent()
//                intentData.putExtra("deviceNo",alcoholObj.deviceNo)
//                intentData.putExtra("inventoryLeft",alcoholObj.inventoryLeft.toString())
//                intentData.putExtra("activity_index",alcohol_index)
//                //将存储着数据的Intent对象返回到上级Activity
//                setResult(Activity.RESULT_OK,intentData)
//                //销毁Activity
//                finish()
//            }
//        }
//    }
//
//    override fun onBackPressed() {
//        alcohol_index = -1
//        val intentData = Intent()
//        //将存储着数据的Intent对象返回到上级Activity
//        setResult(Activity.RESULT_OK,intentData)
//        //销毁Activity
//        finish()
//    }
//
//    //改变按钮可点状态
//    fun ButtonStatus(){
//        choosed_sure.isEnabled = true
//    }
}