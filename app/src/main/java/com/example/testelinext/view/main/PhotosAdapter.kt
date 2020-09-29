package com.example.testelinext.view.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.testelinext.R
import kotlinx.android.synthetic.main.item_photo.view.*


class PhotosAdapter : RecyclerView.Adapter<PhotosAdapter.PhotosHolder>() {

    private val items = ArrayList<MainViewModel.Photo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosHolder {
        return PhotosHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PhotosHolder, position: Int) {
        holder.bindItem(items[position])
    }

    fun addItems(newItems: List<MainViewModel.Photo>) {
        items.addAll(newItems)
        notifyItemRangeInserted(items.size - newItems.size, newItems.size)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateItem(photo: MainViewModel.Photo) {
        getPhotoById(photo.id)?.run {
            state = photo.state
            url = photo.url
            notifyItemChanged(getItemPosition(this))
        }
    }

    private fun getItemPosition(photo: MainViewModel.Photo): Int {
        return items.indexOf(photo)
    }

    private fun getPhotoById(id: String) = items.find {
        it.id == id
    }

    class PhotosHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val imgPhoto = view.imgPhoto
        private val progressBar = view.progressBar

        fun bindItem(photo: MainViewModel.Photo) {
            progressBar.visibility = if(photo.state == MainViewModel.ItemState.LOADING) View.VISIBLE else View.GONE
            if(photo.state == MainViewModel.ItemState.FILLED) {
                Glide.with(imgPhoto.context)
                    .load(photo.url)
                    .apply(RequestOptions.centerCropTransform())
                    .into(imgPhoto)
            }
        }
    }

}