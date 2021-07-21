package com.example.tradernet.ui

import com.example.tradernet.entity.Quote
import com.example.tradernet.model.IQuoteFabric

class QuoteFabricImpl: IQuoteFabric {
    override fun createEmptyQuote(tickerId: String): Quote =
        Quote(tickerId = tickerId)
}