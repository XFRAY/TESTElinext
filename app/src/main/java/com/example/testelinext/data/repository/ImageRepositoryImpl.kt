package com.example.testelinext.data.repository

import com.example.testelinext.data.network.ApiService
import io.reactivex.Single

class ImageRepositoryImpl(private val apiService: ApiService) : ImageRepository {

    override fun getRandomImageUrl(width: Int, height: Int):  Single<String> {
        return apiService.getPhoto(width, height)
            .map { it.raw().request().url().toString() }
    }

}