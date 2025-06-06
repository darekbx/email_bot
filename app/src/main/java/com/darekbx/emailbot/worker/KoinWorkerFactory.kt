package com.darekbx.notebookcheckreader.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.darekbx.emailbot.bot.CleanUpBot
import com.darekbx.emailbot.worker.BotNotificationManager
import kotlin.jvm.java

class KoinWorkerFactory(
    val cleanUpBot: CleanUpBot,
    val notificationManager: BotNotificationManager
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            CleanUpBotWorker::class.java.name -> {
                CleanUpBotWorker(
                    appContext,
                    workerParameters,
                    cleanUpBot,
                    notificationManager
                )
            }

            else -> null
        }
    }
}
