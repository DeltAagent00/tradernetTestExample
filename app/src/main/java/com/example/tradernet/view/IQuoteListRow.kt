package com.example.tradernet.view

interface IQuoteListRow {
    fun setLogo(url: String)
    fun setTicker(ticker: String)
    fun setDescription(description: String)
    fun setPercent(percent: String)
    fun setColorPercentPositiveValue()
    fun setColorPercentNegativeValue()
    fun setDifference(difference: String)
    fun setBgPercentPositiveValue()
    fun setBgPercentNegativeValue()
}