package com.example.android_alcohol

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android_alcohol.apiService.HiRetrofit
import com.example.android_alcohol.dataMoudel.UserData
import com.example.android_alcohol.dataMoudel.UserInfo
import com.example.android_alcohol.publicMethod.methods
import com.example.android_alcohol.ui.indentity.IndentityAdapter
import kotlinx.android.synthetic.main.activity_intentity.*
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class IndentityActivity : AppCompatActivity() {
    val ApiService = HiRetrofit.create(com.example.android_alcohol.apiService.ApiService::class.java)
    var mList = ArrayList<UserInfo>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intentity)
        methods.addActivity(this)
        progressBar_indent.visibility = View.GONE
        val editordel = getSharedPreferences("timerStatus", Context.MODE_PRIVATE).edit()
        val editordata = getSharedPreferences("data", Context.MODE_PRIVATE).edit()
        editordel.clear().apply()
        editordata.clear().apply()

        recyclerView_indent.layoutManager = LinearLayoutManager(this)

        recyclerView_indent.adapter = IndentityAdapter(mList,this)
        val phone = intent.getStringExtra("phone")
        val password = intent.getStringExtra("password")

        ApiService.userLogin(phone!!,password!!).enqueue(object : Callback<UserData> {
            override fun onResponse(call: Call<UserData>, response: Response<UserData>) {
                if (response.body()!!.status.toString() == "1"){
                    mList.addAll(response.body()!!.data)
                    recyclerView_indent.adapter?.notifyDataSetChanged()
                    progressBar_indent.visibility = View.GONE
//                        finish()
                }else{
                    Toast.makeText(this@IndentityActivity, response.body()?.errmsg.toString(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserData>, t: Throwable) {
                Toast.makeText(this@IndentityActivity, "网络连接错误，请稍后重试！", Toast.LENGTH_SHORT).show()
            }
        })
    }
    open fun jumpActivity(token:String,userId:String,cname:String){
        progressBar_indent.visibility = View.VISIBLE
        val intent_order = Intent(this, BaseActivity::class.java)
        val editor = getSharedPreferences("data", Context.MODE_PRIVATE).edit()
        editor.putString("token", token)
        editor.putString("userId", userId)
        editor.putString("dynamicname", cname)
        editor.putInt("OrderSum", 0)
        editor.apply()
        val statuseditor = getSharedPreferences("timerStatus", Context.MODE_PRIVATE).edit()
        statuseditor.putBoolean("status", true)
        statuseditor.putBoolean("activityStatus", true)
        statuseditor.apply()
        startActivity(intent_order)
        finish()
    }

}