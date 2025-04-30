package com.praktikerja.artoz.data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ExchangeRateApi {
    @GET("v6/1736ba3c07e6d6106a978aa6/latest/{base}")
    fun getExchangeRates(@Path("base") baseCurrency: String): Call<ExchangeRateResponse>
}
