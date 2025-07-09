package com.darekbx.emailbot.di

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.darekbx.emailbot.BuildConfig
import com.darekbx.emailbot.bot.CleanUpBot
import com.darekbx.emailbot.domain.AddSpamFilterUseCase
import com.darekbx.emailbot.domain.DeleteSpamFilterUseCase
import com.darekbx.emailbot.domain.FetchSpamFiltersUseCase
import com.darekbx.emailbot.imap.Connection
import com.darekbx.emailbot.imap.EmailOperations
import com.darekbx.emailbot.imap.FetchEmails
import com.darekbx.emailbot.repository.RefreshBus
import com.darekbx.emailbot.repository.database.AppDatabase
import com.darekbx.emailbot.repository.database.dao.SpamDao
import com.darekbx.emailbot.repository.storage.CommonPreferences
import com.darekbx.emailbot.ui.configuration.ui.ConfigurationViewModel
import com.darekbx.emailbot.repository.storage.CryptoUtils
import com.darekbx.emailbot.repository.storage.EncryptedConfiguration
import com.darekbx.emailbot.ui.MainActivityViewModel
import com.darekbx.emailbot.ui.emails.EmailsViewModel
import com.darekbx.emailbot.ui.filters.FiltersViewModel
import com.darekbx.emailbot.worker.BotNotificationManager
import com.darekbx.notebookcheckreader.worker.KoinWorkerFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

private val Context.dataStore by preferencesDataStore(name = "secure_config")

val appModules = module {
    single { androidContext().dataStore }
    single(named("master.key")) { BuildConfig.MASTER_KEY }
    single { CryptoUtils(get(named("master.key"))) }
    single { EncryptedConfiguration(get(), get()) }
    single { CommonPreferences(get()) }

    factory { CleanUpBot(get(), get(), get(), get(), get()) }
    single { RefreshBus() }

    // Worker
    single { androidContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
    factory { BotNotificationManager(androidContext(), get()) }
    single { KoinWorkerFactory(get(), get()) }
}

val databaseModule = module {
    single<AppDatabase> {
        Room
            .databaseBuilder(get<Application>(), AppDatabase::class.java, AppDatabase.DB_NAME)
            .build()
    }
    single<SpamDao> { get<AppDatabase>().spamDao() }
}

val domainModule = module {
    factory { AddSpamFilterUseCase(get()) }
    factory { FetchSpamFiltersUseCase(get()) }
    factory { DeleteSpamFilterUseCase(get()) }
}

val imap = module {
    factory { Connection() }
    factory { FetchEmails(get(), get()) }
    factory { EmailOperations(get(), get()) }
}

val viewModelModule = module {
    viewModel { ConfigurationViewModel(get(), get()) }
    viewModel { EmailsViewModel(get(), get(), get(), get(), get()) }
    viewModel { MainActivityViewModel(get(), get()) }
    viewModel { FiltersViewModel(get(), get()) }
}
