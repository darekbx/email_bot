package com.darekbx.emailbot

import android.app.Application
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.darekbx.emailbot.di.appModules
import com.darekbx.emailbot.di.databaseModule
import com.darekbx.emailbot.di.domainModule
import com.darekbx.emailbot.di.imap
import com.darekbx.emailbot.di.viewModelModule
import com.darekbx.notebookcheckreader.worker.CleanUpBotWorker
import com.darekbx.notebookcheckreader.worker.KoinWorkerFactory
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import java.util.concurrent.TimeUnit
import kotlin.getValue

class BotApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@BotApplication)
            modules(appModules, databaseModule, domainModule, imap, viewModelModule)
        }

        val workerFactory: KoinWorkerFactory by inject()
        WorkManager.initialize(
            this,
            Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .build()
        )

        schedulePeriodicDataRefresh()
    }

    private fun schedulePeriodicDataRefresh() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val dataRefreshWorkRequest = PeriodicWorkRequestBuilder<CleanUpBotWorker>(8, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "CleanUpBotWork",
            ExistingPeriodicWorkPolicy.KEEP,
            dataRefreshWorkRequest
        )
    }
}
