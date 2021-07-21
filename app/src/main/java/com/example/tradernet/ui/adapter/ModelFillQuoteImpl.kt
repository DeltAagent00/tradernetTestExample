package com.example.tradernet.ui.adapter

import com.example.tradernet.entity.Quote
import com.example.tradernet.entity.QuoteVectorEnum
import com.example.tradernet.model.IModelFillQuote
import com.example.tradernet.view.IQuoteListRow
import java.math.RoundingMode

class ModelFillQuoteImpl: IModelFillQuote {
    companion object {
        private const val NO_POSITION = -1
        private const val ROUND_PERCENT_DIGITS = 2
    }

    private val isOneNumber: (Char) -> Boolean = {
        it == '1'
    }
    private val isDotNumber: (Char) -> Boolean = {
        it == ',' || it == '.'
    }

    override fun prepare(item: Quote, view: IQuoteListRow) {
        view.setLogo(url = item.logo)
        view.setTicker(ticker = item.tickerId)

        prepareDescription(item = item, view = view)
        preparePercent(item = item, view = view)
        prepareTradePrice(item = item, view = view)
    }

    private fun prepareDescription(item: Quote, view: IQuoteListRow) {
        val result = StringBuilder()

        item.lastTradeExchange ?.let {
            result.append(it)
        }

        item.name?.let {
            if (result.isNotEmpty()) {
                result.append(" | ")
            }
            result.append(it)
        }

        view.setDescription(description = result.toString())
    }

    private fun preparePercent(item: Quote, view: IQuoteListRow) {
        val value = item.diffPercentClosePrevSession ?: .0
        val result = prepareNumberByRoundingDigits(value, ROUND_PERCENT_DIGITS)

        val isPositive = value > .0

        val prefix = if (isPositive) {
            "+"
        } else {
            ""
        }

        val vector = item._vector ?: QuoteVectorEnum.None
        when(vector) {
            QuoteVectorEnum.None ->
                if (isPositive) {
                    view.setColorPercentPositiveValue()
                } else if (value < .0) {
                    view.setColorPercentNegativeValue()
                }
            QuoteVectorEnum.Positive ->
                view.setBgPercentPositiveValue()
            QuoteVectorEnum.Negative ->
                view.setBgPercentNegativeValue()
        }

        view.setPercent("$prefix$result%")
    }

    private fun prepareTradePrice(item: Quote, view: IQuoteListRow) {
        val rounding = (item.rounding ?: .0).toBigDecimal().toString()

        val firstPosition = rounding.indexOfFirst(isDotNumber)
        var lastPosition = rounding.indexOfFirst(isOneNumber)

        if (lastPosition == NO_POSITION) {
            lastPosition = firstPosition + 1
        }
        val roundDigits = lastPosition - firstPosition

        val lastTradePrice = prepareNumberByRoundingDigits(
            value = item.lastTradePrice ?: .0,
            roundDigits = roundDigits)

        val changePriceLastDealPrefix = if ((item.changePriceLastDeal ?: .0) > .0) {
            "+"
        } else {
            ""
        }
        val changePriceLastDeal = prepareNumberByRoundingDigits(
            value = item.changePriceLastDeal ?: .0,
            roundDigits = roundDigits
        )

        val result = "$lastTradePrice ( $changePriceLastDealPrefix$changePriceLastDeal )"
        view.setDifference(result)
    }

    private fun prepareNumberByRoundingDigits(value: Double, roundDigits: Int): String {
        val tmpValue = value.toBigDecimal()
        val resultRound = tmpValue.setScale(roundDigits, RoundingMode.HALF_EVEN).toString()
        val positionDot = resultRound.indexOfFirst(isDotNumber)

        val countDigitsAfterDot = resultRound.length - positionDot - 1

        return if (positionDot != NO_POSITION && countDigitsAfterDot < roundDigits) {
            val maxLengthString = (resultRound.length + roundDigits - countDigitsAfterDot)
            resultRound.padEnd(maxLengthString, '0')
        } else {
            resultRound
        }
    }
}