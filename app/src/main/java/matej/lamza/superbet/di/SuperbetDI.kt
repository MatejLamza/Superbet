package matej.lamza.superbet.di

import android.app.Application
import matej.lamza.core_data.di.dataModule
import matej.lamza.core_network.di.networkModule
import matej.lamza.superbet.di.modules.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.Module

class SuperbetDI(private val application: Application) {
    private lateinit var koinApplication: KoinApplication
    private val modules = listOf<Module>(
        dataModule,
        networkModule,
        viewModelModule
    )

    fun init() {
        koinApplication = startKoin {
            androidContext(application)
            modules(modules)
            androidLogger(Level.INFO)
        }
    }
}


