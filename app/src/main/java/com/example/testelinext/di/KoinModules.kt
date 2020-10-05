package com.example.testelinext.di

import com.example.testelinext.BuildConfig
import com.example.testelinext.data.network.ApiService
import com.example.testelinext.data.repository.ImageRepository
import com.example.testelinext.data.repository.ImageRepositoryImpl
import com.example.testelinext.view.main.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

val networkModule = module {
    single { createRetrofit().create(ApiService::class.java) }
}

val viewModelModule = module {
    viewModel { MainViewModel(get()) }
}

val repositoryModule = module {
    single<ImageRepository> { ImageRepositoryImpl(get()) }
}

fun createRetrofit(): Retrofit{
    return Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
}

