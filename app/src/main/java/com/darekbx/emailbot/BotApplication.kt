package com.darekbx.emailbot

import android.app.Application
import com.darekbx.emailbot.di.appModules
import com.darekbx.emailbot.di.databaseModule
import com.darekbx.emailbot.di.domainModule
import com.darekbx.emailbot.di.imap
import com.darekbx.emailbot.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class BotApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@BotApplication)
            modules(appModules, databaseModule, domainModule, imap, viewModelModule)
        }
    }
}
