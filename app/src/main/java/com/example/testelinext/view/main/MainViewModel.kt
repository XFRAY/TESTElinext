package com.example.testelinext.view.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.testelinext.view.main.data.network.ApiService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class MainViewModel(application: Application, private val apiService: ApiService) :
    AndroidViewModel(application) {

    companion object {
        private const val DEFAULT_IMAGE_WIDTH = 200
        private const val DEFAULT_IMAGE_HEIGHT = 200
        const val DEFAULT_SPAN_COUNT = 10
        const val DEFAULT_COLUMN_COUNT = 7
    }

    private var countOfPages = 0

    val addPageData = MutableLiveData<Page>()
    val updateItemData = MutableLiveData<Photo>()
    val listOfPages = ArrayList<Page>()

    fun addPhoto() {
        generatePage()
        createNewPhoto()
        apiService.getPhoto(DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    val url = response.raw().request().url().toString()
                    getFirstLoadingPhoto().apply {
                        state = ItemState.FILLED
                        this.url = url
                        updateItemData.value = this
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("1111", "11111")
                }

            })

    }

    private fun getFirstLoadingPhoto(): Photo {
        return listOfPages.last().getFirstEmptyItem()
    }

    private fun createNewPhoto(): Photo {
        return listOfPages.last().getFirstEmptyItem().apply {
            state = ItemState.LOADING
            updateItemData.value = this
        }

    }

    private fun shouldGenerateNewPage(): Boolean {
        return if (listOfPages.isEmpty())
            true
        else
            listOfPages.last().isFilled()

    }

    private fun generatePage() {
        if (shouldGenerateNewPage()) {
            listOfPages.add(Page(countOfPages).apply {
                for (i: Int in 0 until maxItemCount) {
                    listOfItems.add(Photo(countOfPages))
                }
            })
            countOfPages++
            addPageData.value = listOfPages.last()
        }
    }

    data class Page(
        val pageId: Int,
        val maxItemCount: Int = DEFAULT_COLUMN_COUNT * DEFAULT_SPAN_COUNT,
        val listOfItems: ArrayList<Photo> = arrayListOf()
    ) {
        fun isFilled(): Boolean {
            listOfItems.find { it.state == ItemState.EMPTY }?.let {
                return false
            } ?: return true
        }

        fun getFirstEmptyItem() = listOfItems.first {
            it.state == ItemState.EMPTY
        }

        fun getFirstLoadingPhoto() = listOfItems.first {
            it.state == ItemState.LOADING
        }

    }

    data class Photo(
        val page: Int,
        val id: String = UUID.randomUUID().toString(),
        var url: String? = null,
        var state: ItemState = ItemState.EMPTY
    )

    enum class ItemState {
        EMPTY, LOADING, FILLED
    }
}



