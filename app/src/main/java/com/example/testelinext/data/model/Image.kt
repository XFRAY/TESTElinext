package com.example.testelinext.data.model

import java.util.*


data class Image(
    val id: String = UUID.randomUUID().toString(),
    val url: String? = null,
    val imageStatus: ImageStatus = ImageStatus.EMPTY
) {
    enum class ImageStatus {
        EMPTY, LOADING, LOADED
    }
}