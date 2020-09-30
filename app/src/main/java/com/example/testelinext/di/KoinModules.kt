package com.example.testelinext.di

import com.example.testelinext.BuildConfig
import com.example.testelinext.view.main.MainViewModel
import com.example.testelinext.network.*
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

val networkModule = module {
    single { Retrofit.Builder()
        .baseUrl(BuildConfig.API_URL)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(
            OkHttpClient.Builder()
            .build())
        .build().create(ApiService::class.java) }

}

val viewModelModule = module {
    viewModel { MainViewModel(get(), get()) }
}

