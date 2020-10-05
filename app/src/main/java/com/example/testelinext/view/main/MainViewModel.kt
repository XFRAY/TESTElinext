package com.example.testelinext.view.main

import androidx.lifecycle.ViewModel
import com.example.testelinext.data.model.Image
import com.example.testelinext.data.model.Page
import com.example.testelinext.data.repository.ImageRepository
import com.example.testelinext.extensions.addTo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.schedulers.Schedulers

class MainViewModel(private val imageRepository: ImageRepository) : ViewModel() {

    companion object {
        private const val DEFAULT_IMAGE_WIDTH = 200
        private const val DEFAULT_IMAGE_HEIGHT = 200
        private const val DEFAULT_IMAGE_TO_LOAD_COUNT = 140
        const val DEFAULT_SPAN_COUNT = 10
        const val DEFAULT_COLUMN_COUNT = 7
        private const val MAX_ITEM_COUNT_PER_PAGE = DEFAULT_COLUMN_COUNT * DEFAULT_SPAN_COUNT
    }

    private val disposable = CompositeDisposable()
    private val pageList = ArrayList<Page>()
    private val imageList = ArrayList<Image>()

    val imageListSubject: BehaviorSubject<List<Image>> = BehaviorSubject.create<List<Image>>()

    fun loadOneImage() {
        loadImages(1)
    }

    private fun loadImages(count: Int) {
        onImagesStartLoading(count)
        imageRepository.getRandomImageUrl(DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT)
            .repeat(count.toLong())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ url ->
                onImageLoaded(url)
            }, {
                it.printStackTrace()
            }).addTo(disposable)
    }

    private fun addNewPage() {
        val page = Page(maxImageCount = MAX_ITEM_COUNT_PER_PAGE)
        pageList.add(page)
        imageList.addAll(page.imageList)
    }

    private fun onImagesStartLoading(count: Int) {
        if (pageList.isEmpty()) addNewPage()
        var emptyItemCount = pageList.last().imageCountWithStatus(Image.ImageStatus.EMPTY)
        for (i: Int in 0 until count) {
            if (emptyItemCount == 0) {
                emptyItemCount = MAX_ITEM_COUNT_PER_PAGE
                addNewPage()
            }
            val page = pageList.last()
            val imageListSize = page.imageList.size
            val positionFrom = imageListSize - emptyItemCount
            val positionInList =
                MAX_ITEM_COUNT_PER_PAGE * pageList.size - MAX_ITEM_COUNT_PER_PAGE + positionFrom
            val image = page.imageList[positionFrom].copy(imageStatus = Image.ImageStatus.LOADING)
            page.imageList[positionFrom] = image
            imageList[positionInList] = image
            emptyItemCount--
        }
        imageListSubject.onNext(imageList)
    }

    private fun onImageLoaded(url: String) {
        for (i: Int in 0 until imageList.size) {
            val oldImage = imageList[i]
            if (oldImage.imageStatus == Image.ImageStatus.LOADING) {
                imageList[i] = imageList[i].copy(url = url, imageStatus = Image.ImageStatus.LOADED)
                imageListSubject.onNext(imageList)
                break
            }
        }
    }

    fun reloadPhotos() {
        disposable.clear()
        pageList.clear()
        imageList.clear()
        imageListSubject.onNext(imageList)
        loadImages(DEFAULT_IMAGE_TO_LOAD_COUNT)
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}



