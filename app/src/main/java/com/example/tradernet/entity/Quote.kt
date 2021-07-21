package com.example.tradernet.entity

import com.google.gson.annotations.SerializedName

data class Quote(
//    val ClosePrice: Int = 0,
//    val TradingReferencePrice: Int = 0,
//    val TradingSessionSubID: String = "",
//    val acd: Int = 0,
//    val baf: Int = 0,
//    val bap: Double = .0,
//    val bas: Int = 0,
//    val base_contract_code: String = "",
//    val base_currency: String = "",
//    val base_ltr: String = "",
//    val bbf: Int = 0,
//    val bbp: Double = .0,
//    val bbs: Int = 0,
    @SerializedName("c")//    Идентификатор
    val tickerId: String = "",
    @SerializedName("chg")
    val changePriceLastDeal: Double? = null,
//    val chg110: Double = .0,
//    val chg22: Double = .0,
//    val chg220: Double = .0,
//    val chg5: Double = .0,
//    val codesub_nm: String = "",
//    val cpn: Int = 0,
//    val cpp: Int = 0,
//    val dpb: Int = 0,
//    val dps: Int = 0,
//    val fv: Int = 0,
//    val init: Int = 0,
//    val issue_nb: String = "",
//    val kind: Int = 0,
    @SerializedName("ltp")
    val lastTradePrice: Double? = null,
    @SerializedName("ltr")
    val lastTradeExchange: String? = null,
//    val lts: Int = 0,
//    val ltt: String = "",
//    val marketStatus: String = "",
//    val maxtp: Double = .0,
    @SerializedName("min_step")
    val rounding: Double? = null,
//    val mintp: Double = .0,
//    val mrg: String = "",
//    val mtd: String = "",
//    val n: Int = 0,
    val name: String? = null,
//    val name2: String = "",
//    val ncd: String = "",
//    val ncp: Int = 0,
    @SerializedName("op")
    val priceOpenCurrentSession: Double? = null,
//    val otc_instr: String = "",
//    val p110: Double = .0,
//    val p22: Double = .0,
//    val p220: Double = .0,
//    val p5: Double = .0,
    @SerializedName("pcp")
    val diffPercentClosePrevSession: Double? = null,
    @SerializedName("pp")
    val pricePrevClosed: Double? = null,
//    val rev: Int = 0,
//    val scheme_calc: String = "",
//    val step_price: Double = .0,
//    val trades: Int = 0,
//    val type: Int = 0,
//    val virt_base_instr: String = "",
//    val vlt: Double = .0,
//    val vol: Int = 0,
//    val x_curr: String = "",
//    val x_currVal: Double = .0,
//    val x_descr: String = "",
//    val x_dsc1: Int = 0,
//    val x_dsc2: Int = 0,
//    val x_dsc3: Int = 0,
//    val x_istrade: Int = 0,
//    val x_lot: Int = 0,
//    val x_max: Double = .0,
//    val x_min: Double = .0,
//    val x_short: Int = 0,
//    val yld: String = "",
//    val yld_ytm_ask: Int = 0,
//    val yld_ytm_bid: Int = 0,
    val _vector: QuoteVectorEnum? = null //    vector
) {
    companion object {
        private const val BASE_LOGO_URL = "https://tradernet.ru/logos/get-logo-by-ticker?ticker="
    }

    val logo: String
        get() = BASE_LOGO_URL.plus(tickerId.lowercase())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Quote

        if (tickerId != other.tickerId) return false

        return true
    }

    override fun hashCode(): Int {
        return tickerId.hashCode()
    }
}