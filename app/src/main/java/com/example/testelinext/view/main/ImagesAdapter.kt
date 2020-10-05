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
import com.example.testelinext.data.model.Image
import com.example.testelinext.extensions.dpToPx
import com.example.testelinext.extensions.setCornerRadius
import kotlinx.android.synthetic.main.item_photo.view.*


class ImagesAdapter : RecyclerView.Adapter<ImagesAdapter.PhotosHolder>() {

    val items = ArrayList<Image>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosHolder {
        return PhotosHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PhotosHolder, position: Int) {
        holder.bindItem(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setData(newList: List<Image>){
        items.clear()
        items.addAll(newList)
    }

    class PhotosHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val imgPhoto = view.imgPhoto
        private val imageContainer = view.imageContainer
        private val progressBar = view.progressBar

        fun bindItem(image: Image) {
            imageContainer.setCornerRadius(imageContainer.dpToPx(7f))
            when (image.imageStatus) {
                Image.ImageStatus.EMPTY -> {
                    imgPhoto.setImageDrawable(null)
                    progressBar.visibility = View.GONE
                }
                Image.ImageStatus.LOADING -> {
                    imgPhoto.setImageDrawable(null)
                    progressBar.visibility = View.VISIBLE
                }
                Image.ImageStatus.LOADED -> {
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