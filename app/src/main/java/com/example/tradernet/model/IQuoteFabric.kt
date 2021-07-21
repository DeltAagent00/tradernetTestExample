package com.example.tradernet.model

import com.example.tradernet.entity.Quote

interface IQuoteFabric {
    fun createEmptyQuote(tickerId: String): Quote
}