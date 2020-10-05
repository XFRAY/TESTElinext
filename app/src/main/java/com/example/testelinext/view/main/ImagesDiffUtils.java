package com.example.testelinext.view.main;

import androidx.recyclerview.widget.DiffUtil;

import com.example.testelinext.data.model.Image;

import java.util.List;

public class ImagesDiffUtils extends DiffUtil.Callback {

    private final List<Image> oldList;
    private final List<Image> newList;

    public ImagesDiffUtils(List<Image> oldList, List<Image> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        Image oldImage = oldList.get(oldItemPosition);
        Image newImage = newList.get(newItemPosition);
        return oldImage.getId().equals(newImage.getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Image oldImage = oldList.get(oldItemPosition);
        Image newImage = newList.get(newItemPosition);
        String oldUrl = oldImage.getUrl();
        String newUrl = newImage.getUrl();
        if (oldUrl != null && newUrl != null) {
            return oldUrl.equals(newUrl);
        }
        return oldImage.getImageStatus() == newImage.getImageStatus();
    }
}
