package com.example.tradernet.model

import androidx.annotation.DrawableRes

interface IImageLoader<T> {
    fun loadInto(url: String?,
                 container: T,
                 @DrawableRes placeholderImage: Int = 0,
                 @DrawableRes failImage: Int = 0,
                 successAction: (() -> Unit)? = null,
                 failAction: (() -> Unit)? = null
    )
}