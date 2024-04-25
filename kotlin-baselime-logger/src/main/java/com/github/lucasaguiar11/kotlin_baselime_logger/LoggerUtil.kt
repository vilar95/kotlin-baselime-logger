package com.github.lucasaguiar11.kotlin_baselime_logger

import android.os.Build
import android.os.DeadObjectException
import android.os.DeadSystemException
import android.util.Log
import java.lang.StringBuilder
import java.net.UnknownHostException

object LoggerUtil {

    fun getError(throwable: Throwable?): String? {
        if (throwable == null) {
            return null
        }

        val sb = StringBuilder()
        var t: Throwable? = throwable
        while (t != null) {
            if (t is UnknownHostException) {
                break
            }
            if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    t is DeadSystemException
                } else {
                    t is DeadObjectException
                }
            ) {
                sb.append("DeadSystemException: The system died; earlier logs will point to the root cause")
                break
            }
            t = t.cause
        }
        if (t == null) {
            sb.append(throwable.message)
        }

        return sb.toString()
    }

    fun debug(message: String) {
        if (BaselimeConfig.getIsDebug()) {
            Log.d("[DEBUG-KBS]", message)
        }
    }
}