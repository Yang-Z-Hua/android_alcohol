package com.example.android_alcohol

import android.content.*
import android.os.*
import android.util.Log
import android.util.Log.e
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.android_alcohol.common.MessageCenter
import com.example.android_alcohol.common.MessageType
import com.smartdevice.aidl.IZKCService

open class PrintBeforeActivity : AppCompatActivity() {
    var mReceiver: ScreenOnOffReceiver? = null
    private var mhanlder: Handler? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MessageCenter.instance?.addHandler(handler)
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
        module_flag = intent.getIntExtra(MODULE_FLAG, 8)
        bindService()
        //		mReceiver = new ScreenOnOffReceiver();
//		IntentFilter screenStatusIF = new IntentFilter();
//		screenStatusIF.addAction(Intent.ACTION_SCREEN_ON);
//		screenStatusIF.addAction(Intent.ACTION_SCREEN_OFF);
//		registerReceiver(mReceiver, screenStatusIF);
    }

    open fun handleStateMessage(message: Message?) {}

    /** handler  */
    protected val handler: Handler
        protected get() {
            if (mhanlder == null) {
                mhanlder = object : Handler() {
                    override fun handleMessage(msg: Message) {
                        handleStateMessage(msg)
                    }
                }
            }
            return mhanlder!!
        }

    protected fun sendMessage(message: Message?) {
        handler.sendMessage(message!!)
    }

    protected fun sendMessage(what: Int, obj: Any?) {
        val message = Message()
        message.what = what
        message.obj = obj
        handler.sendMessage(message)
    }

    protected fun sendEmptyMessage(what: Int) {
        handler.sendEmptyMessage(what)
    }

    var bindSuccessFlag = false
    private val mServiceConn: ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName) {
            mIzkcService = null
            bindSuccessFlag = false
            //发送消息绑定成功
            sendEmptyMessage(MessageType.BaiscMessage.SEVICE_BIND_FAIL)
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            mIzkcService = IZKCService.Stub.asInterface(service)
            if (mIzkcService != null) {
                try {
                    DEVICE_MODEL = mIzkcService!!.getDeviceModel()
                    mIzkcService!!.setModuleFlag(module_flag)
                    if (module_flag == 3) {
                        mIzkcService!!.openBackLight(1)
                    }
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
                bindSuccessFlag = true
                //发送消息绑定成功
                sendEmptyMessage(MessageType.BaiscMessage.SEVICE_BIND_SUCCESS)
            }
        }
    }

    fun bindService() {
        //com.zkc.aidl.all为远程服务的名称，不可更改
        //com.smartdevice.aidl为远程服务声明所在的包名，不可更改，
        // 对应的项目所导入的AIDL文件也应该在该包名下
        val intent = Intent("com.zkc.aidl.all")
        intent.setPackage("com.smartdevice.aidl")
        bindService(intent, mServiceConn, BIND_AUTO_CREATE)
    }
    open fun unbindService() {
        unbindService(mServiceConn)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        if (module_flag == 3) {
            try {
                mIzkcService!!.openBackLight(0)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
        super.onStop()
    }

    override fun onDestroy() {
        try {
            unbindService()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
        //		unregisterReceiver(mReceiver);
        super.onDestroy()
    }

    inner class ScreenOnOffReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == "android.intent.action.SCREEN_ON") {
//				SCREEN_ON = true;
                try {
                    //打开电源+
                    if (mIzkcService != null) {
                        mIzkcService!!.setModuleFlag(8)
                    }
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            } else if (action == "android.intent.action.SCREEN_OFF") {
//				SCREEN_ON = false;
//				try {
//					//关闭电源
//					mIzkcService.setModuleFlag(8);
//				} catch (RemoteException e) {
//					e.printStackTrace();
//				}
            }
        }

//        companion object {
//            private const val TAG = "ScreenOnOffReceiver"
//        }
    }

    companion object {
        var MODULE_FLAG = "module_flag"
        var module_flag = 0
        var DEVICE_MODEL = 0
        var mIzkcService: IZKCService? = null
    }
}