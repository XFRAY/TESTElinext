package com.example.testelinext.view.main

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.testelinext.R
import kotlinx.android.synthetic.main.item_photo.view.*


class PhotosAdapter : RecyclerView.Adapter<PhotosAdapter.PhotosHolder>() {

    private val items = ArrayList<MainViewModel.Image>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosHolder {
        return PhotosHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PhotosHolder, position: Int) {
        holder.bindItem(items[position])
    }

    fun updatePages(pages: List<MainViewModel.Page>) {
        val currentItemCount = itemCount
        val listOfAllItem = ArrayList<MainViewModel.Image>()
        pages.forEach {
            listOfAllItem.addAll(it.imageList)
        }
        val newItemsCount = listOfAllItem.size
        if (itemCount > newItemsCount) {
            items.clear()
            items.addAll(listOfAllItem)
            notifyDataSetChanged()
            return
        }
        val newItems = listOfAllItem.subList(currentItemCount, newItemsCount)
        items.addAll(newItems)
        notifyItemRangeInserted(currentItemCount, newItems.size)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateItem(image: MainViewModel.Image) {
        getPhotoById(image.id).run {
            imageStatus = image.imageStatus
            url = image.url
            notifyItemChanged(getItemPosition(this))
        }
    }

    private fun getItemPosition(image: MainViewModel.Image): Int {
        return items.indexOf(image)
    }

    private fun getPhotoById(id: String) = items.first {
        it.id == id
    }

    class PhotosHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val imgPhoto = view.imgPhoto
        private val progressBar = view.progressBar

        fun bindItem(image: MainViewModel.Image) {
            when (image.imageStatus) {
                MainViewModel.ImageStatus.EMPTY -> {
                    imgPhoto.setImageDrawable(null)
                    progressBar.visibility = View.GONE
                }
                MainViewModel.ImageStatus.LOADING -> {
                    imgPhoto.setImageDrawable(null)
                    progressBar.visibility = View.VISIBLE
                }
                MainViewModel.ImageStatus.LOADED -> {
                    Glide.with(imgPhoto.context)
                        .load(image.url)
                        .into(object : CustomTarget<Drawable>() {
                            override fun onLoadCleared(placeholder: Drawable?) {
                            }

                            override fun onResourceReady(
                                resource: Drawable,
                                transition: Transition<in Drawable>?
                            ) {
                                imgPhoto.setImageDrawable(resource)
                                progressBar.visibility = View.GONE
                            }
                        })

                }
            }
        }
    }

}