package com.darekbx.emailbot.di

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.darekbx.emailbot.BuildConfig
import com.darekbx.emailbot.bot.CleanUpBot
import com.darekbx.emailbot.domain.AddSpamFilterUseCase
import com.darekbx.emailbot.domain.FetchSpamFiltersUseCase
import com.darekbx.emailbot.imap.Connection
import com.darekbx.emailbot.imap.EmailOperations
import com.darekbx.emailbot.imap.FetchEmails
import com.darekbx.emailbot.repository.database.AppDatabase
import com.darekbx.emailbot.repository.database.dao.SpamDao
import com.darekbx.emailbot.ui.configuration.ui.ConfigurationViewModel
import com.darekbx.emailbot.repository.storage.CryptoUtils
import com.darekbx.emailbot.repository.storage.EncryptedConfiguration
import com.darekbx.emailbot.ui.MainActivityViewModel
import com.darekbx.emailbot.ui.emails.EmailsViewModel
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

    factory { CleanUpBot(get(), get(), get()) }
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
}

val imap = module {
    factory { Connection() }
    factory { FetchEmails(get(), get()) }
    factory { EmailOperations(get(), get()) }
}

val viewModelModule = module {
    viewModel { ConfigurationViewModel(get(), get()) }
    viewModel { EmailsViewModel(get(), get(), get()) }
    viewModel { MainActivityViewModel(get()) }
}
