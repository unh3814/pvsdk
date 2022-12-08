package com.pvcombank.sdk.payment.util.excecute

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.ThreadPoolExecutor
import android.os.Process
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

sealed class MyExecutor {
    class Main : Executor {
        private val handler = Handler(Looper.getMainLooper())
        override fun execute(command: Runnable) {
            handler.post(command)
        }
    }

    class Default() {
        companion object {
            private val NUMBER_CORE get() = Runtime.getRuntime().availableProcessors()
            private lateinit var backgroundTask: ThreadPoolExecutor
            private lateinit var ioTask: ThreadPoolExecutor
            private lateinit var main: Executor
            private var INSTANCE: Default? = null

            fun build(): Default {
                return INSTANCE ?: synchronized(this) {
                    INSTANCE ?: Default().also {
                        INSTANCE = it
                    }
                }
            }
        }

        init {
            val backgroundMyThreadFactory = MyThreadFactory(Process.THREAD_PRIORITY_BACKGROUND)
            backgroundTask = ThreadPoolExecutor(
                NUMBER_CORE * 2,
                NUMBER_CORE * 2,
                60L,
                TimeUnit.SECONDS,
                LinkedBlockingQueue(),
                backgroundMyThreadFactory
            )

            ioTask = ThreadPoolExecutor(
                NUMBER_CORE * 2,
                NUMBER_CORE * 2,
                60L,
                TimeUnit.SECONDS,
                LinkedBlockingQueue(),
                backgroundMyThreadFactory
            )

            main = Main()
        }

        fun executeDefault(): ThreadPoolExecutor = backgroundTask
        fun executeIO(): ThreadPoolExecutor = ioTask
        fun executeMain(): Executor = main
    }
}