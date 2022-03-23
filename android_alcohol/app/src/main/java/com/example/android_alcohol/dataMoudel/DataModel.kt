package com.example.android_alcohol.dataMoudel

import com.example.android_alcohol.ui.login.LoginActivity
import retrofit2.http.Query

//登录
data class UserData(
    val data: List<UserInfo>,
    val errcode: String,
    val errmsg: String,
    val status: String,
    val sucmsg: String
)

data class UserInfo(
    val accountId: String,
    val cloridgeId: String,
    val cname: String,
    val phone: String,
    val uLevel: Int,
    val uType: Int,
    val userId: String
)

//获取用户信息
data class identityInfo(
    val data: userAllInfo,
    val errcode: String,
    val errmsg: String,
    val status: String,
    val sucmsg: String
)

data class userAllInfo(
    val token: String,
    val user: User
)

data class User(
    val accountId: String,
    val brandPath: String,
    val businessManager: Any,
    val cardPrice: Double,
    val cloridgeId: String,
    val cname: String,
    val createtime: Long,
    val id: String,
    val inuse: Int,
    val password: Any,
    val paymentPath: String,
    val phone: String,
    val pid: String,
    val ppid: Any,
    val remark: Any,
    val resetPwd: Int,
    val rootId: String,
    val subMchId: String,
    val subMchType: Int,
    val subMchUpgrade: Int,
    val uLevel: Int,
    val uType: Int,
    val updatetime: Long,
    val userLabel: Any,
    val wechatHeadimgurl: Any,
    val wechatMiniOpenid: Any,
    val wechatMpOpenid: String,
    val wechatMpSubscribe: String,
    val wechatNickname: Any,
    val wechatOpenOpenid: Any,
    val wechatUnionid: String
)

//获取订单参数
data class Data(
    val token: String,
    val userId: String,
    val uType: Int,
    val currentPage: Int,
    val pageSize: Int,
)
//返回订单数据
data class OrderList(
    val data: OrderInfo,
    val errcode: String,
    val errmsg: String,
    val status: String,
    val sucmsg: String
)

data class OrderInfo(
    val list: List<ListDetail>,
    val nextPage: Int,
    val pageNum: Int,
    val pageSize: Int,
    val pages: Int,
    val prePage: Int,
    val rows: Any,
    val size: Int,
    val total: Int
)

data class ListDetail(
    var action: String,
    val actionTime: Any,
    val appid: String,
    val buyerId: String,
    val buyerInfo: String,
    val createtime: Long,
    val deskId: String,
    val deskInfo: String,
    var deviceNo: Any,
    val deviceOrderId: Any,
    val deviceType: String,
    val deviceTypeId: String,
    val finishTime: Any,
    val id: String,
    val inuse: Int,
    val packageDetail: String,
    val packageId: String,
    val payFeeRates: Double,
    val payFinal: Double,
    val payId: String,
    val payPlatform: String,
    val payPrice: Double,
    val payRefund: Double,
    val payStatus: String,
    val payTime: Long,
    val payWay: String,
    val paymentId: String,
    val propType: Int,
    val proportion: Double,
    val remark: Any,
    val rootId: String,
    val ruleId: String,
    val ruleName: Any,
    val updatetime: Any,
    val userId: String,
    val waiter: Any,
    var inventoryLeft: Any,
)

data class buyerInfoObj(
    val phone: String,
    val cname: String,
)

data class deskInfoObj(
    val deskNo: String,
    val deskId: String,
    val download_ico: Boolean,
)


data class packageInfo(
    val price: String,
    val value: String,
    val desc: String,
)

//获取出酒酒机列表
data class AlcoholList(
    val data: List<Alcohol>,
    val errcode: String,
    val errmsg: String,
    val status: String,
    val sucmsg: String
)

data class Alcohol(
    val deviceNo: String,
    val deviceStatus: String,
    val inventoryDefault: Int,
    val inventoryLeft: Int,
    val rssi: String,
    val userId: String
)

//出酒信息
data class DeviceAction(
    val `data`: Any,
    val errcode: String,
    val errmsg: String,
    val status: String,
    val sucmsg: String
)

//选择的出酒酒机信息
data class SaveDevideInfo(
    var orderId: String,
    var deviceNo: String,
    var inventoryLeft: String,
)