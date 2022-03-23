package com.example.android_alcohol.common

class MessageType {
    interface BaiscMessage {
        companion object {
            const val BASE = 0x00000010
            const val SEVICE_BIND_SUCCESS = BASE + 1
            const val SEVICE_BIND_FAIL = BASE + 2
            const val GET_IDETIFY_INFO_SUCCESS = BASE + 3
            const val DETECT_PRINTER_FAIL = BASE + 4
            const val DETECT_PRINTER_SUCCESS = BASE + 5
            const val PRINTER_LINK_TIMEOUT = BASE + 6
            const val SCAN_RESULT_GET_SUCCESS = BASE + 7
        }
    }
}