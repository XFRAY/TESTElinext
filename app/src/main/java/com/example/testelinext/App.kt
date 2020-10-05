package com.example.testelinext

import android.app.Application
import com.example.testelinext.di.networkModule
import com.example.testelinext.di.repositoryModule
import com.example.testelinext.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(networkModule, viewModelModule, repositoryModule)
        }
    }
}