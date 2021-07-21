package com.example.tradernet.network.socket

import com.example.tradernet.entity.Quote

interface ISocketQuoteChangeListener {
    fun onChange(quote: Quote)
}