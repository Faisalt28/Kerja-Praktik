package com.praktikerja.artoz.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    fun setCurrency(currencyCode: String) {
        sharedPreferences.edit().putString("currency", currencyCode).apply()
    }

    fun getCurrency(): String {
        return sharedPreferences.getString("currency", "IDR") ?: "IDR"
    }

    fun setExchangeRate(rate: Float) {
        sharedPreferences.edit().putFloat("exchangeRate", rate).apply()
    }

    fun getExchangeRate(): Float {
        return sharedPreferences.getFloat("exchangeRate", 1.0f)
    }
}