package com.example.android_alcohol.apiService

import com.example.android_alcohol.dataMoudel.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

object HiRetrofit {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://aapi.cloridge.com/api/pda/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    fun <T> create(clazz: Class<T>) : T {
        return retrofit.create(clazz)
    }
}

interface ApiService {
    @Headers("Content-Type: application/x-www-form-urlencoded")

    //登录
    @FormUrlEncoded
    @POST("user/login/identities/bypassword")
    fun userLogin(
        @Field("phone") phone: String,
        @Field("password") password: String
    ) : Call<UserData>

    //获取用户信息
    @FormUrlEncoded
    @POST("user/login/byuserid")
    fun userInfo(
        @Field("userId") userId: String,
        @Field("accountId") accountId: String
    ) : Call<identityInfo>


    //获取订单信息
    @FormUrlEncoded
    @POST("desktop/order/list")
    fun getRepository(
        @Header("Authorization") Authorization: String,
        @Field("userId") userId : String,
        @Field("action") action : String,
        @Field("payStatus") payStatus : String,
        @Field("currentPage") currentPage : Int,
        @Field("pageSize") pageSize : Int
    ) : Call<OrderList>


    //选择酒机
    @FormUrlEncoded
    @POST("desktop/order/flow/device")
    fun chooseDevice(
        @Header("Authorization") Authorization: String,
        @Field("userId") userId : String,
        @Field("orderId") orderId : String,
    ) : Call<AlcoholList>

    //出酒
    @FormUrlEncoded
    @POST("desktop/order/device/create")
    fun runDevice(
        @Header("Authorization") Authorization: String,
        @Field("userId") userId : String,
        @Field("desktopOrderId") desktopOrderId : String,
        @Field("deviceNo") deviceNo : String,
    ) : Call<DeviceAction>

    //测试接口
//    @FormUrlEncoded
//    @POST("api/pc/v1/buyer/dynamic/type/list")
//    fun getTestData(
//        @Header("Authorization") Authorization: String,
//        @Field("ruleId") ruleId : String,@Field("salesmanId") salesmanId : String
//    ) : Call<testReturnData>

    //登录
//    @GET("api/pc/v1/user/login/identities/bypassword")
//    fun signin(
//
//    ) Call<OrderList>
}