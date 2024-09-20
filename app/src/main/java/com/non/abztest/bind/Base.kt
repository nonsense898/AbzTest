package com.non.abztest.bind

import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.non.abztest.R

object Base {
    fun drawableUrl(imageView: AppCompatImageView, drawableUrl: String?) {
        Glide.with(imageView.context).clear(imageView)

        Glide.with(imageView.context)
            .load(drawableUrl)
            .placeholder(R.drawable.ic_avatar_placeholder)
            .error(R.drawable.ic_avatar_placeholder)
            .circleCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)
    }
}