package com.example.testelinext.view.main.di

import com.example.testelinext.view.main.MainViewModel
import com.example.testelinext.view.main.data.network.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val networkModule = module {
    single { createOkHttpClient(get()) }
    single { createHttpLoggingInterceptor() }
    single { createRetrofit(get()).create(ApiService::class.java) }

}

val viewModelModule = module {
    viewModel { MainViewModel(get(), get()) }
}

