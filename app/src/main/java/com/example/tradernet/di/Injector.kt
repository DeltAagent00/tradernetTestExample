package com.example.tradernet.di

import android.content.Context
import com.example.tradernet.di.component.AppComponent
import com.example.tradernet.di.component.DaggerAppComponent
import com.example.tradernet.di.module.AppModule

class Injector {
    companion object {
        private val instance = Injector()

        fun getInstance(): Injector {
            return instance
        }
    }

    private var appComponent: AppComponent? = null

    fun init(context: Context) {
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(context))
            .build()
    }

    fun appComponent(): AppComponent {
        checkAppComponent()
        return appComponent!!
    }

    fun clearAppComponent() {
        appComponent = null
    }

    private fun checkAppComponent() {
        if (appComponent == null) {
            throw NullPointerException("AppComponent need Injector.init()")
        }
    }
}