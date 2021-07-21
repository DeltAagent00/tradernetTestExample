package com.example.tradernet.model

import com.example.tradernet.network.socket.ISocketQuoteChangeListener

interface IModelSocket {
    fun isConnected(): Boolean
    fun isClosed(): Boolean

    fun connect()
    fun disconnect()

    fun addListTickersForObservable(tickers: List<String>)

    fun addQuoteChangesListener(listener: ISocketQuoteChangeListener)
    fun removeQuoteChangesListener(listener: ISocketQuoteChangeListener)
}