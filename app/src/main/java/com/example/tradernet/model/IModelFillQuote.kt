package com.example.tradernet.model

import com.example.tradernet.entity.Quote
import com.example.tradernet.view.IQuoteListRow

interface IModelFillQuote {
    fun prepare(item: Quote, view: IQuoteListRow)
}