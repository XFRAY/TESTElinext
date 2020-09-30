package com.example.testelinext.view.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.testelinext.network.ApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.*
import kotlin.collections.ArrayList

class MainViewModel(application: Application, private val apiService: ApiService) :
    AndroidViewModel(application) {

    companion object {
        private const val DEFAULT_IMAGE_WIDTH = 200
        private const val DEFAULT_IMAGE_HEIGHT = 200
        private const val DEFAULT_IMAGE_TO_LOAD_COUNT = 140
        const val DEFAULT_SPAN_COUNT = 10
        const val DEFAULT_COLUMN_COUNT = 7
    }

    private val disposable = CompositeDisposable()
    private val pageList = ArrayList<Page>()

    val pageListSubject: BehaviorSubject<List<Page>> = BehaviorSubject.create<List<Page>>()
    val updateImageSubject: BehaviorSubject<Image> = BehaviorSubject.create<Image>()

    fun addNewPhoto() {
        if (isNeedToCreateNewPage()) createNewPage()
        onImageStartLoading()
        disposable.add(
            apiService.getPhoto(DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val url = it.raw().request().url().toString()
                    onImageLoaded(url)
                }, {

                })
        )
    }

    private fun onImageStartLoading() {
        pageList.last().getFirstEmptyImage()?.run {
            imageStatus = ImageStatus.LOADING
            updateImageSubject.onNext(this)
        }?:run {
            if (isNeedToCreateNewPage()) createNewPage()
            onImageStartLoading()
        }
    }

    private fun onImageLoaded(url: String) {
        pageList.forEach {
            if (it.isContainsImageWithStatus(ImageStatus.LOADING)) {
                it.findFirstImageWithStatus(ImageStatus.LOADING).apply {
                    this.url = url
                    imageStatus = ImageStatus.LOADED
                    updateImageSubject.onNext(this)
                    return
                }
            }
        }
    }

    private fun isNeedToCreateNewPage(): Boolean {
        return if (pageList.isEmpty())
            true
        else
            pageList.last().isFull()

    }

    private fun createNewPage() {
        pageList.add(Page())
        pageListSubject.onNext(pageList)
    }

    fun reloadPhotos() {
        disposable.clear()
        pageList.clear()
        for (i: Int in 0 until DEFAULT_IMAGE_TO_LOAD_COUNT) {
            addNewPhoto()
        }
    }

    data class Page(
        val maxImageCount: Int = DEFAULT_COLUMN_COUNT * DEFAULT_SPAN_COUNT,
        val imageList: ArrayList<Image> = arrayListOf()
    ) {
        init {
            for (i: Int in 0 until maxImageCount) {
                imageList.add(Image())
            }
        }

        fun isFull(): Boolean {
            imageList.find { it.imageStatus == ImageStatus.EMPTY }?.let {
                return false
            } ?: return true
        }

        fun isContainsImageWithStatus(status: ImageStatus): Boolean {
            imageList.find { it.imageStatus == status }?.let {
                return true
            } ?: return false
        }

        fun getFirstEmptyImage() = imageList.firstOrNull {
            it.imageStatus == ImageStatus.EMPTY
        }

        fun findFirstImageWithStatus(status: ImageStatus) =
            imageList.first { it.imageStatus == status }

    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    data class Image(
        val id: String = UUID.randomUUID().toString(),
        var url: String? = null,
        var imageStatus: ImageStatus = ImageStatus.EMPTY
    )

    enum class ImageStatus {
        EMPTY, LOADING, LOADED
    }
}



