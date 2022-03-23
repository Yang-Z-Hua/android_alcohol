package com.example.android_alcohol

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.*
import android.text.TextUtils
import android.util.Log.*
import android.view.*
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.android_alcohol.apiService.HiRetrofit
import com.example.android_alcohol.common.MessageType
import com.example.android_alcohol.dataMoudel.DeviceAction
import com.example.android_alcohol.dataMoudel.OrderList
import com.example.android_alcohol.ui.home.HomeFragment
import com.example.android_alcohol.ui.login.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.UnsupportedEncodingException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.timerTask
import com.example.android_alcohol.common.dialog.BtnBottomDialog
import com.example.android_alcohol.dataMoudel.Alcohol
import kotlin.collections.ArrayList
import kotlinx.android.synthetic.main.dialog_content_normal.*
import kotlinx.android.synthetic.main.fragment_alcohol.*

import com.example.android_alcohol.databinding.ActivityBaseBinding
import kotlinx.android.synthetic.main.fragment_home.*
import android.util.Log

import java.util.Timer
import android.view.View
import com.example.android_alcohol.common.dialog.ExitDiaolg
import com.example.android_alcohol.publicMethod.methods
import kotlinx.android.synthetic.main.dialog_common.*





class BaseActivity : PrintBeforeActivity() {
    //保持CPU长时间唤醒========================
    lateinit var wakeLock: PowerManager.WakeLock
    //订单打印相关=========================================
    //线程运行标志 the running flag of thread
    private var runFlag = true
    //打印机检测标志 the detect flag of printer
    private var detectFlag = false
    //打印机连接超时时间 link timeout of printer
    private val PINTER_LINK_TIMEOUT_MAX = (30 * 1000L).toFloat()
    var mDetectPrinterThread: BaseActivity.DetectPrinterThread? = null


    private lateinit var binding: ActivityBaseBinding
    //弹窗===========================
    private val edialog: ExitDiaolg by lazy {
        ExitDiaolg(this)
    }
    //循环执行=====================================
    val timer = Timer()
    val cyclerTimer = Timer()
    //判断息屏======================================
    private var screenState = true
    //请求接口相关=====================================
    val ApiService =
        HiRetrofit.create(com.example.android_alcohol.apiService.ApiService::class.java)
    var deviceList = ArrayList<Alcohol>()
    var token: String = ""
    var userId: String = ""
    var returnedDeviceNo: String = ""
    var returnedInventoryLeft: String = ""
    var activity_index: Int = -1 //选中的是哪个订单
    //点击订单选择酒机时，判断是否有已选酒机===================
    var alcohol_choosed: String = ""
    var repetition_click = true //判断短时间内重复点击
    //保存当前订单数，标识是否有新订单===================================
    var OrderSum: Int = 0

    //双击退出========================================
    private var mDoubleClickExit: Boolean = false
    private var firstExitTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        methods.addActivity(this)
        //在当前activity保持cup持续唤醒============================
        var powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
            "MyApp::MyWakelockTag")
        wakeLock.acquire()
        //打印空格被动唤醒打印机==========================================
//        prevPrint()
        //清除通知
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(1)

        //保存数据=========================================================
        val editorget = getSharedPreferences("data", Context.MODE_PRIVATE)
        userId = editorget.getString("userId", "").toString()
        token = editorget.getString("token", "").toString()
        //执行轮询，判断消息通知时机====================
        cyclerTimer.schedule(object : TimerTask() {
            override fun run() {
                //需要执行的任务
                GetOrderInfo("no")
            }
        }, 0,10000)

    }
    //显示准备借酒弹窗提示=========================================
    fun showExit(
        ruleName: String,
        deskTop: String,
        orderId: String,
        deviceType: String,
        paytime: String,
        price: String,
        value: String,
        payway: String,
        DeviceNoChoosed: String,
        position: String,
    ) {
        edialog.action_desc.text = "请确定套餐酒量，准备分酒器后，点击订单出酒按钮开始出酒"
        edialog.btn_list.visibility = View.VISIBLE
        edialog.show()
        edialog.setOnclickListener { v ->
            if (v.id == R.id.cancle_btn) {
                edialog.dismiss()
            }
            if (v.id == R.id.sure_btn) {
                timer.schedule(timerTask { edialog.dismiss() }, 4000)
                activity_index = position.toInt()
                edialog.action_desc.text = "订单打印中，请勿操作......"
                edialog.btn_list.visibility = View.GONE
                PrintText("$orderId", "$DeviceNoChoosed","$ruleName","$deskTop","$deviceType","$paytime","$price","$value","$payway")
            }
        }
    }
    //通知出酒============================
    fun PrintText(
        desktopOrderId: String,
        deviceNo: String,
        ruleName: String,
        deskTop: String,
        deviceType: String,
        paytime: String,
        price: String,
        value: String,
        payway: String,
    ) {
        ApiService.runDevice(token!!, userId!!, desktopOrderId, deviceNo).enqueue(object :
            Callback<DeviceAction> {
            override fun onResponse(call: Call<DeviceAction>, response: Response<DeviceAction>) {
                if (response.body()!!.status.toString() == "1") {
                    Toast.makeText(this@BaseActivity, "出酒成功", Toast.LENGTH_SHORT).show()
                    activity_index = -1
                    val editordelete = getSharedPreferences("data", Context.MODE_PRIVATE).edit()
                    editordelete.putString("orderId", "")
                    editordelete.putString("deviceNo", "")
                    editordelete.putString("InventoryLeft", "")
                    editordelete.apply()
                    val fragment = HomeFragment()
                    val manager = supportFragmentManager
                    val transaction = manager.beginTransaction()
                    transaction.replace(R.id.nav_host_fragment_activity_main, fragment)
                    transaction.commit()
                    prevPrint()
                    timer.schedule(timerTask {
                        PrintReceipts("$ruleName",
                            "$deskTop",
                            "$desktopOrderId",
                            "$deviceType",
                            "$paytime",
                            "$price",
                            "$value",
                            "$payway")
                    }, 1200)
                } else {
                    Toast.makeText(this@BaseActivity,
                        response.body()?.errmsg.toString(),
                        Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<DeviceAction>, t: Throwable) {
                Toast.makeText(this@BaseActivity, "网络连接错误，请稍后重试！出酒", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //获取桌面码订单
    open fun GetOrderInfo(OrderStutas: String) {
        //消息通知相关
        var managernoti = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel("normal", "Normal", NotificationManager.IMPORTANCE_DEFAULT)
            managernoti.createNotificationChannel(channel)
            channel.setSound(null, null);
        }
        Log.e("cyclerString","轮询打印")

        ApiService.getRepository(token, userId, OrderStutas, "paid", 1, 999).enqueue(object :
            Callback<OrderList> {
            override fun onResponse(call: Call<OrderList>, response: Response<OrderList>) {
                if (response.body()!!.status.toString() == "1") {
                    //如果获取订单数不等于已有订单数，则标识有新订单
                    if (OrderSum != response.body()!!.data.total) {
                        if (response.body()!!.data.total > OrderSum) {
                            val notification =
                                NotificationCompat.Builder(this@BaseActivity, "normal")
                                    .setContentTitle("云桥售酒机")
                                    .setContentText("您有新的待出酒订单")
                                    .setSmallIcon(R.drawable.alcohol)
                                    .setAutoCancel(true)
                                    .setLargeIcon(BitmapFactory.decodeResource(resources,
                                        R.drawable.alcohol))
                                    .setSound(Uri.parse("android.resource://" + this@BaseActivity.getPackageName() + "/" + R.raw.new_aijia))
                                    .build()
                            managernoti.notify(1, notification)
                        }
                        if (screenState == true) {
                            val fragment = HomeFragment()
                            val manager = supportFragmentManager
                            val transaction = manager.beginTransaction()
                            transaction.replace(R.id.nav_host_fragment_activity_main, fragment)
                            transaction.commit()
//                            transaction.commitAllowingStateLoss()
                        }
                        OrderSum = response.body()!!.data.total
                    }


                } else {
                    Toast.makeText(this@BaseActivity,
                        response.body()?.errmsg.toString(),
                        Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<OrderList>, t: Throwable) {
                Toast.makeText(this@BaseActivity, "网络连接错误，请稍后重试！獲取訂單", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //打开选择酒机页面


    //获取选择的酒机信息
    fun getOrderId(): String? {
        val editordata = getSharedPreferences("data", Context.MODE_PRIVATE)
        val orderId = editordata.getString("orderId", "")
        return orderId
    }

    fun getDeviceNo(): String? {
        val editordata = getSharedPreferences("data", Context.MODE_PRIVATE)
        val deviceNo = editordata.getString("deviceNo", "")
        return deviceNo
    }

    fun getInventoryLeft(): String? {
        val editordata = getSharedPreferences("data", Context.MODE_PRIVATE)
        val inventoryLeft = editordata.getString("InventoryLeft", "")
        return inventoryLeft
    }

    //打开酒机选择弹窗
    fun ChooseAlcohol(orderId: String, brandName: String) {
        //保存选择的订单
        val editordata = getSharedPreferences("data", Context.MODE_PRIVATE).edit()
        editordata.putString("orderId", orderId)
        editordata.apply()
    }

    //酒机选择页面返回数据
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data.toString() == "Intent {  }") return
        when (requestCode) {
            1 -> if (resultCode == Activity.RESULT_OK) {
                returnedDeviceNo = data?.getStringExtra("deviceNo").toString()
                returnedInventoryLeft = data?.getStringExtra("inventoryLeft").toString()
                val editordata = getSharedPreferences("data", Context.MODE_PRIVATE).edit()
                editordata.putString("deviceNo", returnedDeviceNo)
                editordata.putString("InventoryLeft", returnedInventoryLeft)
                editordata.apply()

                val fragment = HomeFragment()
                val manager = supportFragmentManager
                val transaction = manager.beginTransaction()
                transaction.replace(R.id.nav_host_fragment_activity_main, fragment)
                transaction.commitAllowingStateLoss()
            }
        }
    }

    //获取弹窗的选择数据
    fun getDeviceData(returnedDeviceNo: String, returnedInventoryLeft: String) {
        val editordata = getSharedPreferences("data", Context.MODE_PRIVATE).edit()
        editordata.putString("deviceNo", returnedDeviceNo)
        editordata.putString("InventoryLeft", returnedInventoryLeft)
        editordata.apply()

        val fragment = HomeFragment()
        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment_activity_main, fragment)
        transaction.commitAllowingStateLoss()
    }

    override fun onStop() {
        super.onStop()
        screenState = false
    }

    override fun onRestart() {
        super.onRestart()
        screenState = true
        nav_view.postDelayed(object : Runnable {
            override fun run() {
                nav_view.selectedItemId = nav_view.menu.getItem(0).itemId
            }
        }, 100)
        timer.schedule(timerTask {
            val fragment = HomeFragment()
            val manager = supportFragmentManager
            val transaction = manager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment_activity_main, fragment)
            transaction.commitAllowingStateLoss()
        }, 700)

    }

    override fun onPause() {
        super.onPause()
        screenState = false
    }

    //预打印
    open fun prevPrint(){
        val utf8Str_1 = "\n"

        var btUTF8_1 = ByteArray(0)
        try {
            btUTF8_1 = utf8Str_1.toByteArray(charset("UTF-8"))

//            modify printer encoding to utf-8
            Companion.mIzkcService!!.sendRAWData("print", byteArrayOf(0x1C, 0x43, 0xFF.toByte()))
//            must sleep，wait setting and save success
            SystemClock.sleep(100)
            Companion.mIzkcService!!.sendRAWData("print", btUTF8_1)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
        mDetectPrinterThread = DetectPrinterThread()
        mDetectPrinterThread!!.start()
    }

    //  打印具体方法
    open fun PrintReceipts(
        ruleName: String,
        deskTop: String,
        orderId: String,
        deviceType: String,
        paytime: String,
        price: String,
        value: String,
        payway: String,
    ) {
        val editor = getSharedPreferences("data", Context.MODE_PRIVATE)
        var name = editor.getString("dynamicname", "")
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val time: String = format.format(Date())
        val utf8Str_0 = "$ruleName\n\n"
        val utf8Str_2 = "$deskTop\n\n"
        val utf8Str_1 = "收银员：$name\n" +
                "打单日期：$time\n" +
                "支付日期：$paytime\n" +
                "支付方式：$payway\n" +
                "品类名称  $deviceType\n" +
                "金额     $price\n" +
                "出酒量   ${value}ml\n" +
                "单号:$orderId\n\n\n\n\n"

        var btUTF8_0 = ByteArray(0)
        var btUTF8_1 = ByteArray(0)
        var btUTF8_2 = ByteArray(0)
        try {
            btUTF8_0 = utf8Str_0.toByteArray(charset("UTF-8"))
            btUTF8_1 = utf8Str_1.toByteArray(charset("UTF-8"))
            btUTF8_2 = utf8Str_2.toByteArray(charset("UTF-8"))

//            modify printer encoding to utf-8
            Companion.mIzkcService!!.sendRAWData("print", byteArrayOf(0x1C, 0x43, 0xFF.toByte()))

//            must sleep，wait setting and save success
            SystemClock.sleep(100)

            Companion.mIzkcService!!.setAlignment(1)
            Companion.mIzkcService!!.setFontSize(1)
            Companion.mIzkcService!!.sendRAWData("print", btUTF8_0)
            Companion.mIzkcService!!.setAlignment(0)
            Companion.mIzkcService!!.sendRAWData("print", btUTF8_2)
            Companion.mIzkcService!!.setFontSize(0)
            Companion.mIzkcService!!.sendRAWData("print", btUTF8_1)
            edialog.dismiss()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
        mDetectPrinterThread = DetectPrinterThread()
        mDetectPrinterThread!!.start()
    }

    @Throws(RemoteException::class)
    fun permitPrint() {

        //唤醒打印机
        mIzkcService!!.sendRAWData("", byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x00))
        SystemClock.sleep(100)
        /* 关闭自动休眠
		 * 1E 02 N1 N2 N3 N4 N5
		 * 说明: N1=1. 开启自动进入休眠功能;
		 * N1=0. 关闭自动进入休眠功能;*/mIzkcService!!.sendRAWData("",
            byteArrayOf(0x1E, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00))
        SystemClock.sleep(100)

        //关闭自动禁止打印
        mIzkcService!!.sendRAWData("", byteArrayOf(0x1E, 0x04, 0x00,
            0xBF.toByte(), 0xD8.toByte(), 0xD6.toByte(), 0xC6.toByte()))
        SystemClock.sleep(100)

        /*
		 * 设置允许打印
		*协议: 1E 03 N BF D8 D6 C6
		说明: N = 1 允许打印. 单片机返回"bPtCtrl Enable !\r\n"
			  N = 0 禁止打印. 单片机返回"bPtCtrl Disable !\r\n"*/mIzkcService!!.sendRAWData("",
            byteArrayOf(0x1E, 0x03, 0x01,
                0xBF.toByte(),
                0xD8.toByte(),
                0xD6.toByte(),
                0xC6.toByte()))
        SystemClock.sleep(100)
    }

    override fun handleStateMessage(message: Message?) {
        super.handleStateMessage(message)
        when (message!!.what) {
            MessageType.BaiscMessage.SEVICE_BIND_SUCCESS -> {
//                Toast.makeText(this, "service bind success", Toast.LENGTH_SHORT).show()
                try {
                    mIzkcService!!.setModuleFlag(8)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }
            //服务绑定失败
            MessageType.BaiscMessage.SEVICE_BIND_FAIL -> {
//                Toast.makeText(this,
//                    " service bind fail",
//                    Toast.LENGTH_SHORT).show()
            }
            //連接成功
            MessageType.BaiscMessage.DETECT_PRINTER_SUCCESS -> {
                val msg = message.obj as String
//                Toast.makeText(this, "printer link success", Toast.LENGTH_SHORT).show()
            }
            //连接超时
            MessageType.BaiscMessage.PRINTER_LINK_TIMEOUT -> {
//                Toast.makeText(this,
//                    "printer link timeout",
//                    Toast.LENGTH_SHORT).show()
            }
        }
    }
//
    inner class DetectPrinterThread : Thread() {
        override fun run() {
            super.run()
            while (runFlag) {
                val start_time = SystemClock.currentThreadTimeMillis().toFloat()
                var end_time = 0f
                var time_lapse = 0f
                if (detectFlag) {
                    //检测打印是否正常 detect if printer is normal
                    try {
                        if (Companion.mIzkcService != null) {
                            val printerSoftVersion = Companion.mIzkcService!!.firmwareVersion1
                            if (TextUtils.isEmpty(printerSoftVersion)) {
                                Companion.mIzkcService!!.setModuleFlag(0)
                                end_time = SystemClock.currentThreadTimeMillis().toFloat()
                                time_lapse = end_time - start_time
                                if (time_lapse > PINTER_LINK_TIMEOUT_MAX) {
                                    detectFlag = false
                                    //打印机连接超时 printer link timeout
                                    sendEmptyMessage(MessageType.BaiscMessage.PRINTER_LINK_TIMEOUT)
                                }
                            } else {
                                //打印机连接成功 printer link success
                                sendMessage(MessageType.BaiscMessage.DETECT_PRINTER_SUCCESS,
                                    printerSoftVersion)
                                detectFlag = false
                            }
                        }
                    } catch (e: RemoteException) {
                        e.printStackTrace()
                    }
                }
                SystemClock.sleep(1000)
            }
        }
    }
    override fun onResume() {
        detectFlag = true
        super.onResume()
        if ((wakeLock != null) && (wakeLock.isHeld() == false)) {
            wakeLock.acquire();
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        wakeLock.release();
        runFlag = false
        mDetectPrinterThread?.interrupt()
        mDetectPrinterThread = null
        //清除通知
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(1)
//        Toast.makeText(this, "再按一次退出软件", Toast.LENGTH_SHORT).show()
    }

    //  右上角功能列表相关==================================
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_nav_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        flipTimeCount.stop()
        cyclerTimer.cancel()
        //清除通知
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(1)
        //清除定时器标识
        val editordel = getSharedPreferences("timerStatus", Context.MODE_PRIVATE).edit()
        val getkey = getSharedPreferences("keyword", Context.MODE_PRIVATE).edit()
        getkey.clear().apply()
        editordel.clear()
        editordel.putBoolean("status", false)
        editordel.putBoolean("activityStatus", false)
        editordel.apply()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val doubleExitTime = System.currentTimeMillis()
        if (mDoubleClickExit && doubleExitTime - firstExitTime < 2000) {
            methods.finishAll()
        } else {
            firstExitTime = doubleExitTime
            Toast.makeText(this, "再按一次退出软件", Toast.LENGTH_SHORT).show()
            mDoubleClickExit = true
            return
        }
        super.onBackPressed()
    }



    fun showDialog(alcoholList: ArrayList<Alcohol>) {
        BtnBottomDialog().setData(alcoholList);
        BtnBottomDialog().show(supportFragmentManager, "tag")
    }
}

