package com.example.tradernet.network.socket

import com.example.tradernet.entity.Quote
import com.example.tradernet.model.IModelSocket
import com.example.tradernet.utils.LoggerHelper
import com.google.gson.Gson
import kotlinx.coroutines.*

class ModelSocketImpl(
    private val gson: Gson,
    private val socket: ISocket): IModelSocket {

    companion object {
        private const val INTERVAL_RECONNECT_MILLIS = 5 * 1000L
        private const val INTERVAL_SEND_PING_MILLIS = 5 * 1000L
        private const val INTERVAL_SEND_QUOTE_MILLIS = 5 * 1000L
        private const val INTERVAL_RETRY_CONNECT_MILLIS = 1 * 1000L
        private const val RETRY_INFINITY_CONNECT = -1
        private const val RETRY_COUNT_CONNECT = RETRY_INFINITY_CONNECT

        private const val CLOSE_NORMAL_CODE = 1000
        private const val CLOSER_NORMAL_TEXT = "The user has closed the connection."
        private const val PING_SENT_MESSAGE = "ping"

        private const val KEY_EVENT = "[\"q\","
        private const val CHAR_END_EMIT = "]"
    }

    private val parentJob = Job()
    private val coroutineContext = parentJob + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)
    private var pingJob: Job? = null
    private var quoteRepeatJob: Job? = null

    private var currentState = SocketStateEnum.CLOSED
    private var needReconnect = false

    private val quoteChangesListenerListeners = HashSet<ISocketQuoteChangeListener>()
    private var quoteRepeatCommand: String? = null

    private val socketListener = object: ISocketListener {
        override fun onOpen() {
            currentState = SocketStateEnum.CONNECTED
            ping()

            if (!quoteRepeatCommand.isNullOrEmpty()) {
                startRepeatQuote()
            }
        }

        override fun onFailure(throwable: Throwable) {
            currentState = SocketStateEnum.CLOSED
            LoggerHelper.error(msg = "Socket fail", throwable = throwable)

            if (needReconnect) {
                scope.launch {
                    delay(INTERVAL_RECONNECT_MILLIS)
                    connect()
                }
            }
        }

        override fun onMessage(msg: String) {
            parseEvent(msg)
        }

        override fun onClosed(code: Int, reason: String) {
            currentState = SocketStateEnum.CLOSED
            closePingJob()
            closeQuoteRepeatJob()
        }
    }

    override fun isConnected(): Boolean =
        currentState == SocketStateEnum.CONNECTED

    override fun isClosed(): Boolean =
        currentState == SocketStateEnum.CLOSED

    override fun connect() {
        needReconnect = true

        if (!isClosed()) {
            startWaitCloseForRestart()
        } else {
            currentState = SocketStateEnum.CONNECTING
            socket.openSocket(socketListener)
        }
    }

    private fun startWaitCloseForRestart() {
        scope.launch {
            var retry = 1
            val hasInfinityRestart = hasInfinityRestartSocket()

            while (hasInfinityRestart || retry <= RETRY_COUNT_CONNECT) {
                delay(INTERVAL_RETRY_CONNECT_MILLIS)

                if (isClosed()) {
                    currentState = SocketStateEnum.CONNECTING
                    socket.openSocket(socketListener)
                    break
                }

                if (!hasInfinityRestart) {
                    retry++
                }
            }

            if (!hasInfinityRestart &&
                retry > RETRY_COUNT_CONNECT &&
                !isConnected()) {
                throw IllegalStateException("The socket is can't start.")
            }
        }
    }

    private fun hasInfinityRestartSocket(): Boolean =
        RETRY_COUNT_CONNECT == RETRY_INFINITY_CONNECT

    private fun send(msg: String) {
        if (isConnected()) {
            socket.sendMessage(msg)
        }
    }

    private fun ping() {
        closePingJob()

        pingJob = scope.launch {
            while (true) {
                delay(INTERVAL_SEND_PING_MILLIS)

                if (isConnected()) {
                    send(PING_SENT_MESSAGE)
                } else {
                    break
                }
            }
        }
    }

    private fun startRepeatQuote() {
        closeQuoteRepeatJob()

        quoteRepeatJob = scope.launch {
            while(true) {
                quoteRepeatCommand?.let {
                    if (it.isNotBlank() && isConnected()) {
                        send(it)
                    }
                }

                if (!isConnected()) {
                    break
                }

                delay(INTERVAL_SEND_QUOTE_MILLIS)
            }
        }
    }

    private fun closePingJob() {
        closeJob(pingJob)
    }

    private fun closeQuoteRepeatJob() {
        closeJob(quoteRepeatJob)
    }

    private fun closeJob(job: Job?) {
        job?.let {
            if (!it.isCancelled) {
                it.cancel()
            }
        }
    }

    override fun disconnect() {
        needReconnect = false

        if (!isClosed()) {
            currentState = SocketStateEnum.CLOSING
            socket.closeSocket(CLOSE_NORMAL_CODE, CLOSER_NORMAL_TEXT)
        }
    }

    override fun addListTickersForObservable(tickers: List<String>) {
        val messageBuilder = StringBuilder("[\"quotes\", [")

        tickers.forEachIndexed { index, ticker ->
            if (index > 0) {
                messageBuilder.append(", ")
            }
            messageBuilder.append("\"")
                .append(ticker)
                .append("\"")
        }
        messageBuilder.append("]]")

        quoteRepeatCommand = messageBuilder.toString()

        startRepeatQuote()
    }

    override fun addQuoteChangesListener(listener: ISocketQuoteChangeListener) {
        quoteChangesListenerListeners.add(listener)
    }

    override fun removeQuoteChangesListener(listener: ISocketQuoteChangeListener) {
        quoteChangesListenerListeners.remove(listener)
    }

    private fun parseEvent(msg: String) {
        try {
            if (msg.startsWith(KEY_EVENT)) {
                val json = msg.removePrefix(KEY_EVENT).removeSuffix(CHAR_END_EMIT)
                val quote = gson.fromJson(json, Quote::class.java)

                quoteChangesListenerListeners.forEach {
                    it.onChange(quote)
                }
            }
        } catch (exception: Exception) {
            LoggerHelper.error(msg, exception)
        }
    }
}