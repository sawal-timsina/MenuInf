package com.codeace.menuinf.helpers

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.codeace.menuinf.R

fun setImage(context: Context, url: String, imageView: ImageView, placeHolder: Int = R.drawable.imageplaceholder) {
    Glide.with(context).load(url).centerCrop()
        .placeholder(placeHolder).into(imageView)
}