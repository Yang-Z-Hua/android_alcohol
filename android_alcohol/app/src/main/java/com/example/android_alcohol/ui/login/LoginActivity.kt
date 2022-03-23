package com.example.android_alcohol.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer.*
import android.os.Bundle
import android.os.RemoteException
import android.os.SystemClock
import android.text.TextUtils
import android.util.Log
import android.util.Log.e
import android.view.View
import android.widget.Toast
import com.example.android_alcohol.BaseActivity
import com.example.android_alcohol.IndentityActivity
import com.example.android_alcohol.PrintBeforeActivity
import com.example.android_alcohol.R
import com.example.android_alcohol.apiService.HiRetrofit
import com.example.android_alcohol.common.MessageType
import com.example.android_alcohol.dataMoudel.UserData
import com.example.android_alcohol.dataMoudel.identityInfo
import com.example.android_alcohol.publicMethod.methods
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.order_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.UnsupportedEncodingException
import java.text.SimpleDateFormat
import java.util.*


class LoginActivity : PrintBeforeActivity() {
    val ApiService = HiRetrofit.create(com.example.android_alcohol.apiService.ApiService::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        methods.addActivity(this)
        loading.visibility = View.GONE
        //订单
        val intent_order = Intent(this, BaseActivity::class.java)
        //身份选择
        val intent_indentity = Intent(this, IndentityActivity::class.java)
        login.setOnClickListener {
            val phone = username.text.toString()
            val password = password.text.toString()
            if(phone=="" || password == ""){
                Toast.makeText(this@LoginActivity, "请输入账号或密码", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if(phone.length != 11){
                Toast.makeText(this@LoginActivity, "手机号格式错误", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
//            请求接口
            loading.visibility = View.VISIBLE
            ApiService.userLogin(phone,password).enqueue(object : Callback<UserData> {
                override fun onResponse(call: Call<UserData>, response: Response<UserData>) {
                    loading.visibility = View.GONE
                    if (response.body()!!.status.toString() == "1"){
                        val keyword = getSharedPreferences("keyword", Context.MODE_PRIVATE).edit()
                        keyword.putString("phone", phone)
                        keyword.putString("key", password)
                        keyword.apply()
                        Toast.makeText(this@LoginActivity, "登录成功", Toast.LENGTH_SHORT).show()
                        //如果只有一个身份
                        if(response.body()!!.data.size == 1){
                            ApiService.userInfo("${response.body()!!.data[0].userId}","${response.body()!!.data[0].accountId}").enqueue(object : Callback<identityInfo> {
                                override fun onResponse(
                                    call: Call<identityInfo>, responseInn: Response<identityInfo>,
                                ) {
                                    if (responseInn.body()!!.status.toString() == "1") {
                                        val editor = getSharedPreferences("data", Context.MODE_PRIVATE).edit()
                                        editor.putString("token", responseInn.body()!!.data.token)
                                        editor.putString("userId", responseInn.body()!!.data.user.id)
                                        editor.putString("dynamicname", responseInn.body()!!.data.user.cname)
                                        editor.putInt("OrderSum", 0)
                                        editor.apply()
                                        val statuseditor = getSharedPreferences("timerStatus", Context.MODE_PRIVATE).edit()
                                        statuseditor.putBoolean("status", true)
                                        statuseditor.putBoolean("activityStatus", true)
                                        statuseditor.apply()
                                        startActivity(intent_order)
                                    } else {
                                        Toast.makeText(this@LoginActivity, responseInn.body()?.errmsg.toString(), Toast.LENGTH_SHORT).show()
                                    }
                                }
                                override fun onFailure(call: Call<identityInfo>, t: Throwable) {
                                        Toast.makeText(this@LoginActivity, "网络连接错误，请稍后重试！", Toast.LENGTH_SHORT).show()
                                }
                            })
                        }else{
//                            for(item in response.body()!!.data){
//                                val user = UserInfo(item.accountId, item.cloridgeId, item.cname, item.phone, item.uLevel, item.uType,item.userId)
//                                intent_indentity.putExtra("extraKey", user.toJson())
//                            }
                            intent_indentity.putExtra("phone", phone)
                            intent_indentity.putExtra("password", password)
                            startActivity(intent_indentity)
                        }
//                        finish()
                    }else{
                        Toast.makeText(this@LoginActivity, response.body()?.errmsg.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<UserData>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "网络连接错误，请稍后重试！", Toast.LENGTH_SHORT).show()
                }
            })

        }
    }


}

