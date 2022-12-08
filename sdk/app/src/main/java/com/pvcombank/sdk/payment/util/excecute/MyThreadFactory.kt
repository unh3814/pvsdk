package com.pvcombank.sdk.payment.util.excecute

import java.util.concurrent.ThreadFactory
import android.os.Process

class MyThreadFactory(
    private val priority: Int
) : ThreadFactory {
    override fun newThread(r: Runnable?): Thread {
        val wrapperRunnable = Runnable {
            try {
                Process.setThreadPriority(priority)
            } catch (_: Throwable) {

            }
            r?.run()
        }
        return Thread(wrapperRunnable)
    }
}