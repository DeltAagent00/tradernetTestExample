package com.example.tradernet.network.socket

interface ISocketListener {
    fun onOpen()
    fun onFailure(throwable: Throwable)
    fun onMessage(msg: String)
    fun onClosed(code: Int, reason: String)
}