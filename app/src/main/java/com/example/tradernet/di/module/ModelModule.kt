package com.example.tradernet.di.module

import com.example.tradernet.model.IModelFillQuote
import com.example.tradernet.model.IQuoteFabric
import com.example.tradernet.ui.QuoteFabricImpl
import com.example.tradernet.ui.adapter.ModelFillQuoteImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ModelModule {
    @Provides
    @Singleton
    fun provideFillQuote(): IModelFillQuote =
        ModelFillQuoteImpl()

    @Provides
    @Singleton
    fun provideQuoteFabric(): IQuoteFabric =
        QuoteFabricImpl()
}