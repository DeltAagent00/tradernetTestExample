package com.example.tradernet.di.module

import android.content.Context
import android.widget.ImageView
import com.example.tradernet.model.IImageLoader
import com.example.tradernet.model.IModelSocket
import com.example.tradernet.network.socket.ISocket
import com.example.tradernet.network.socket.ModelSocketImpl
import com.example.tradernet.network.socket.OkHttpSocketImpl
import com.example.tradernet.ui.image.GlideImageLoaderImpl
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val appContext: Context) {
    companion object {
        private const val WS_URL = "https://wss.tradernet.ru"
    }

    @Provides
    @Singleton
    fun provideContext(): Context {
        return appContext
    }

    @Provides
    @Singleton
    fun provideGson(): Gson =
        Gson()

    @Provides
    @Singleton
    fun provideSocket(): ISocket =
        OkHttpSocketImpl(WS_URL)

    @Provides
    @Singleton
    fun provideModelSocket(gson: Gson, socket: ISocket): IModelSocket =
        ModelSocketImpl(gson, socket)

    @Provides
    @Singleton
    fun provideImageLoader(): IImageLoader<ImageView> =
        GlideImageLoaderImpl()
}