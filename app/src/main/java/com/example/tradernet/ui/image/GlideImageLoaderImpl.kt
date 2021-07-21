package com.example.tradernet.ui.image

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.tradernet.model.IImageLoader

class GlideImageLoaderImpl: IImageLoader<ImageView> {
    companion object {
        private const val SIZE_EMPTY_IMAGE = 1
    }

    override fun loadInto(
        url: String?,
        container: ImageView,
        @DrawableRes placeholderImage: Int,
        @DrawableRes failImage: Int,
        successAction: (() -> Unit)?,
        failAction: (() -> Unit)?
    ) {
        Glide
            .with(container.context)
            .load(url)
            .placeholder(placeholderImage)
            .error(failImage)
            .listener(object: RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    failAction?.invoke()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    resource?.let {
                        if (it.minimumWidth == SIZE_EMPTY_IMAGE &&
                                it.minimumHeight == SIZE_EMPTY_IMAGE) {
                            failAction?.invoke()
                        } else {
                            successAction?.invoke()
                        }
                    } ?: failAction?.invoke()

                    return false
                }

            })
            .into(container)
    }
}