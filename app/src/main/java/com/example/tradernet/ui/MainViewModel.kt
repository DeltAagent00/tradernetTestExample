package com.example.tradernet.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tradernet.entity.Quote
import com.example.tradernet.entity.QuoteVectorEnum
import com.example.tradernet.model.IModelSocket
import com.example.tradernet.model.IQuoteFabric
import com.example.tradernet.network.socket.ISocketQuoteChangeListener
import kotlinx.coroutines.*
import javax.inject.Inject

class MainViewModel: ViewModel(), ISocketQuoteChangeListener {
    companion object {
        private const val DEFAULT_TICKERS = "RSTI,GAZP,MRKZ,RUAL,HYDR,MRKS,SBER,FEES,TGKA,VTBR," +
                "ANH.US,VICL.US,BURG.US,NBL.US,YETI.US,WSFS.US,NIO.US,DXC.US,MIC.US,HSBC.US," +
                "EXPN.EU,GSK.EU,SHP.EU,MAN.EU,DB1.EU,MUV2.EU,TATE.EU,KGF.EU,MGGT.EU,SGGD.EU"
        private const val DELAY_RESET_QUOTES_VECTOR_MILLIS = 1 * 1000L
    }

    @Inject
    lateinit var socket: IModelSocket
    @Inject
    lateinit var quoteFabric: IQuoteFabric

    private val parentJob = Job()
    private val coroutineContext = parentJob + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)
    private var jobResetVector: Job? = null

    private val tickerList = ArrayList<String>()
    private val quotesBuffer = HashMap<String, Quote>()
    private val _quotes = MutableLiveData<List<Quote>>()
    val quotes: LiveData<List<Quote>> = _quotes

    fun getUseListTicker() {
        if (tickerList.isEmpty()) {
            val tickers = DEFAULT_TICKERS.split(",")
            tickerList.addAll(tickers)

            tickers.forEach {
                val quote = quoteFabric.createEmptyQuote(it)
                quotesBuffer[it] = quote
            }
            _quotes.postValue(quotesBuffer.values.toList())
        }
    }

    fun startSocket() {
        socket.addQuoteChangesListener(this)
        socket.addListTickersForObservable(tickerList)
        socket.connect()
    }

    fun onCloseSocket() {
        socket.disconnect()
    }

    override fun onChange(quote: Quote) {
        val vector = calcVector(quote)
        val prevQuote = quotesBuffer[quote.tickerId]
        val resultQuote = prevQuote?.let {
            it.copy(
                name = quote.name
                    ?: it.name,
                changePriceLastDeal = quote.changePriceLastDeal
                    ?: it.changePriceLastDeal,
                lastTradePrice = quote.lastTradePrice
                    ?: it.lastTradePrice,
                lastTradeExchange = quote.lastTradeExchange
                    ?: it.lastTradeExchange,
                rounding = quote.rounding
                    ?: it.rounding,
                priceOpenCurrentSession = quote.priceOpenCurrentSession
                    ?: it.priceOpenCurrentSession,
                diffPercentClosePrevSession = quote.diffPercentClosePrevSession
                    ?: it.diffPercentClosePrevSession,
                pricePrevClosed = quote.pricePrevClosed
                    ?: it.pricePrevClosed,
                _vector = vector
            )
        } ?: quote.copy(_vector = vector)
        quotesBuffer[quote.tickerId] = resultQuote

        _quotes.postValue(quotesBuffer.values.toList())

        startDelayForResetVector()
    }

    private fun calcVector(quote: Quote): QuoteVectorEnum {
        val prevItem = quotesBuffer[quote.tickerId]

        return prevItem?.let { prevQuote ->
            val prevVector = prevQuote._vector ?: return QuoteVectorEnum.None
            val prevPercent = prevQuote.diffPercentClosePrevSession ?: return prevVector
            val currentPercent = quote.diffPercentClosePrevSession ?: return prevVector

            val diff = prevPercent - currentPercent

            when {
                diff > .0 -> {
                    QuoteVectorEnum.Negative
                }
                diff < .0 -> {
                    QuoteVectorEnum.Positive
                }
                else -> {
                    prevVector
                }
            }
        } ?: QuoteVectorEnum.None
    }

    private fun startDelayForResetVector() {
        val firstItemWithVectorNotNone = quotesBuffer.values.find {
            hasVector(it)
        }

        if (firstItemWithVectorNotNone != null) {
            closeResetVectorJob()

            jobResetVector = scope.launch {
                delay(DELAY_RESET_QUOTES_VECTOR_MILLIS)

                quotesBuffer.values.forEach { itQuote ->
                     if (hasVector(itQuote)) {
                         quotesBuffer[itQuote.tickerId] = itQuote.copy(
                             _vector = QuoteVectorEnum.None
                         )
                     }
                }
                _quotes.postValue(quotesBuffer.values.toList())
            }
        }
    }

    private fun hasVector(quote: Quote): Boolean =
        quote._vector != null && quote._vector != QuoteVectorEnum.None

    private fun closeResetVectorJob() {
        jobResetVector?.let {
            if (!it.isCancelled) {
                it.cancel()
            }
        }
    }
}
