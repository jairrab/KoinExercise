package com.github.jairrab.koinexercise

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.component.KoinComponent
import org.koin.core.context.GlobalContext

//region APPLICATION
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Start Koin
        GlobalContext.startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            workManagerFactory()
            modules(Modules.appModule, workManagerModule)
        }

        //setupWorkManagerFactory()
    }
}