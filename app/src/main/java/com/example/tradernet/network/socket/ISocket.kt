package com.example.tradernet.network.socket

interface ISocket {
    fun openSocket(listener: ISocketListener)
    fun closeSocket(code: Int, reason: String)
    fun sendMessage(msg: String)
}