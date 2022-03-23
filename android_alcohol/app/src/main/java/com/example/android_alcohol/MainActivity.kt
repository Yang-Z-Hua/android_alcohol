package com.example.android_alcohol

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Log.e
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.android_alcohol.apiService.HiRetrofit
import com.example.android_alcohol.dataMoudel.UserData
import com.example.android_alcohol.dataMoudel.identityInfo
import com.example.android_alcohol.publicMethod.methods
import com.example.android_alcohol.ui.login.LoginActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    val ApiService =
        HiRetrofit.create(com.example.android_alcohol.apiService.ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        methods.addActivity(this)
        val editordel = getSharedPreferences("timerStatus", Context.MODE_PRIVATE).edit()
        val editordata = getSharedPreferences("data", Context.MODE_PRIVATE)
        val getkey = getSharedPreferences("keyword", Context.MODE_PRIVATE)
        var phone = getkey.getString("phone", "")
        var password = getkey.getString("key", "")
        var userId = editordata.getString("userId", "")
        progressBar_main.visibility = View.GONE
        if (phone != "" && password != "" && userId != "") {
            editordel.putBoolean("status", true)
            editordel.putBoolean("activityStatus", true)
            editordel.apply()
            progressBar_main.visibility = View.VISIBLE
            ApiService.userLogin("$phone", "$password").enqueue(object : Callback<UserData> {
                override fun onResponse(call: Call<UserData>, response: Response<UserData>) {
                    if (response.body()!!.status.toString() == "1") {
                        for (i in 0 until response.body()!!.data.size) {
                            if(response.body()!!.data[i].userId == userId){
                                ApiService.userInfo("$userId", "${response.body()!!.data[i].accountId}").enqueue(object :
                                    Callback<identityInfo> {
                                    override fun onResponse(
                                        call: Call<identityInfo>, responseInn: Response<identityInfo>,
                                    ) {
                                        if (responseInn.body()!!.status.toString() == "1") {
                                            val editor =
                                                getSharedPreferences("data", Context.MODE_PRIVATE).edit()
                                            editor.putString("token", responseInn.body()!!.data.token)
                                            editor.putString("userId", responseInn.body()!!.data.user.id)
                                            editor.putString( "dynamicname", responseInn.body()!!.data.user.cname )
                                            editor.putInt("OrderSum", 0)
                                            editor.apply()
                                            val statuseditor = getSharedPreferences( "timerStatus", Context.MODE_PRIVATE ).edit()
                                            statuseditor.putBoolean("status", true)
                                            statuseditor.putBoolean("activityStatus", true)
                                            statuseditor.apply()
                                            val intent = Intent(this@MainActivity, BaseActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        } else {
                                            editordel.clear().apply()
                                            val editordatadel = getSharedPreferences("data", Context.MODE_PRIVATE).edit()
                                            editordatadel.clear().apply()
                                            val intent = Intent(this@MainActivity, LoginActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                            Toast.makeText( this@MainActivity, responseInn.body()?.errmsg.toString(), Toast.LENGTH_SHORT ).show()
                                        }
                                    }
                                    override fun onFailure(call: Call<identityInfo>, t: Throwable) {
                                        Toast.makeText(this@MainActivity, "网络连接错误，请稍后重试！", Toast.LENGTH_SHORT).show()
                                    }
                                })
                            }else{
                                if(i == response.body()!!.data.size - 1){
                                    editordel.clear().apply()
                                    val editordatadel = getSharedPreferences("data", Context.MODE_PRIVATE).edit()
                                    editordatadel.clear().apply()
                                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        }

                    } else {
                        editordel.clear().apply()
                        val editordatadel = getSharedPreferences("data", Context.MODE_PRIVATE).edit()
                        editordatadel.clear().apply()
                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                        Toast.makeText(this@MainActivity, response.body()?.errmsg.toString(), Toast.LENGTH_SHORT ).show()
                    }
                }

                override fun onFailure(call: Call<UserData>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "网络连接错误，请稍后重试！", Toast.LENGTH_SHORT).show()
                }
            })

        } else {
            editordel.clear().apply()
            val editordatadel = getSharedPreferences("data", Context.MODE_PRIVATE).edit()
            editordatadel.clear().apply()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }


    }

}