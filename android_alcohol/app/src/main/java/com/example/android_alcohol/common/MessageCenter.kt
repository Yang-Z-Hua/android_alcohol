package com.example.android_alcohol.common

import android.os.Handler
import android.os.Message
import java.util.*

class MessageCenter private constructor() {
    private val mHandlerList: MutableList<Handler> = Vector()
    @Synchronized
    fun addHandler(handler: Handler) {
        mHandlerList.add(handler)
    }

    @Synchronized
    fun removeHandler(handler: Handler) {
        mHandlerList.remove(handler)
    }

    @Synchronized
    fun sendMessage(what: Int, obj: Any?) {
        val message = Message()
        message.obj = obj
        message.what = what
        sendMessage(message)
    }

    @Synchronized
    fun sendMessageWithPre(what: Int, obj: Any?, arg1: Int) {
        val message = Message()
        message.obj = obj
        message.arg1 = arg1
        message.what = what
        sendMessage(message)
    }

    @Synchronized
    fun sendMessage(message: Message?) {
        for (handler in mHandlerList) {
            handler.sendMessage(Message.obtain(message))
        }
    }

    @Synchronized
    fun sendEmptyMessage(what: Int) {
        for (handler in mHandlerList) {
            handler.sendEmptyMessage(what)
        }
    }

    companion object {
        private var CENTER: MessageCenter? = null
        val instance: MessageCenter?
            get() {
                if (CENTER == null) {
                    CENTER = MessageCenter()
                }
                return CENTER
            }
    }
}