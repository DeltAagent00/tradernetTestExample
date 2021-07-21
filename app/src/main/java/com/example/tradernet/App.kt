package com.example.tradernet

import android.app.Application
import com.example.tradernet.di.Injector
import timber.log.Timber

class App: Application() {
    override fun onCreate() {
        super.onCreate()

        initDI()
        initTimber()
    }

    private fun initDI() =
        Injector.getInstance().init(this)

    private fun initTimber() =
        Timber.plant(Timber.DebugTree())
}