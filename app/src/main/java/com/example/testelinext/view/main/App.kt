package com.example.testelinext.view.main

import android.app.Application
import com.bumptech.glide.Glide
import com.example.testelinext.view.main.di.networkModule
import com.example.testelinext.view.main.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(networkModule, viewModelModule)
        }
    }
}