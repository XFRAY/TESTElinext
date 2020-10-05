package com.example.testelinext.data.model

data class Page(
    val maxImageCount: Int,
    val imageList: ArrayList<Image> = arrayListOf()
) {
    init {
        for (i: Int in 0 until maxImageCount) {
            imageList.add(Image())
        }
    }

    fun imageCountWithStatus(status: Image.ImageStatus) = imageList.count { it.imageStatus == status }

}