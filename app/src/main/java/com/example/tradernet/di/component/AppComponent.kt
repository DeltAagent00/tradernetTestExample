package com.example.tradernet.di.component

import com.example.tradernet.di.module.AppModule
import com.example.tradernet.di.module.ModelModule
import com.example.tradernet.ui.MainViewModel
import com.example.tradernet.ui.adapter.QuoteListAdapter
import dagger.Component
import javax.inject.Singleton

@Component(modules = [AppModule::class, ModelModule::class])
@Singleton
interface AppComponent {
    fun inject(viewModel: MainViewModel)

    fun inject(adapter: QuoteListAdapter)

    fun inject(viewHolder: QuoteListAdapter.ViewHolder)
}