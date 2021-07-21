package com.example.tradernet.utils

import android.view.View
import android.widget.ImageView
import android.widget.TextView

object ViewsUtils {
    fun clearImageView(vararg imageViews: ImageView?) {
        clearColorFilterImageView(*imageViews)
        for (view in imageViews)
            view?.setImageResource(android.R.color.transparent)
    }

    fun clearColorFilterImageView(vararg imageViews: ImageView?) {
        for (view in imageViews)
            view?.colorFilter = null
    }

    fun clearTextViews(vararg textViews: TextView?) {
        for (view in textViews) {
            view?.text = ""
        }
    }

    fun goneViews(vararg views: View?) {
        for (view in views) {
            if (view?.visibility != View.GONE) {
                view?.visibility = View.GONE
            }
        }
    }

    fun hideViews(vararg views: View?) {
        for (view in views) {
            if (view?.visibility != View.INVISIBLE) {
                view?.visibility = View.INVISIBLE
            }
        }
    }

    fun showViews(vararg views: View?) {
        for (v in views) {
            if (v?.visibility != View.VISIBLE) {
                v?.visibility = View.VISIBLE
            }
        }
    }
}