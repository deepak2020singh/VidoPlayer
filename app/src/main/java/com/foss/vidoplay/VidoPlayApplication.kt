package com.foss.vidoplay

import android.app.Application
import com.foss.vidoplay.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class VidoPlayApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@VidoPlayApplication)
            modules(appModule)
        }
    }
}