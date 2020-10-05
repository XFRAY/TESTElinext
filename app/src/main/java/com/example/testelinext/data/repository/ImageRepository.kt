package com.example.testelinext.data.repository

import io.reactivex.Single

interface ImageRepository {

    fun getRandomImageUrl(width: Int, height: Int): Single<String>
}