package com.example.tradernet.network.socket

import okhttp3.*

class OkHttpSocketImpl(private val url: String): ISocket {
    private var webSocket: WebSocket? = null

    override fun openSocket(listener: ISocketListener) {
        val request = Request.Builder().url(url).build()
        val webClient = OkHttpClient().newBuilder().build()

        webSocket = webClient.newWebSocket(
            request = request,
            listener = object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) =
                    listener.onOpen()

                override fun onFailure(
                    webSocket: WebSocket,
                    throwable: Throwable,
                    response: Response?) =
                    listener.onFailure(throwable)

                override fun onMessage(webSocket: WebSocket, text: String) =
                    listener.onMessage(text)

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) =
                    listener.onClosed(code, reason)
            }
        )
        webClient.dispatcher.executorService.shutdown()
        webClient.connectionPool.evictAll()
    }

    override fun closeSocket(code: Int, reason: String) {
        webSocket?.close(code, reason)
    }

    override fun sendMessage(msg: String) {
        webSocket?.send(msg)
    }
}