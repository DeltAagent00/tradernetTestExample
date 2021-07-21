package com.example.tradernet.utils

import timber.log.Timber

object LoggerHelper {
    fun error(msg: String, throwable: Throwable?) {
        if (throwable != null) {
            Timber.e(throwable, msg)
        } else {
            Timber.e(msg)
        }
    }

    fun debug(msg: String) {
        Timber.e(msg)
    }
}
