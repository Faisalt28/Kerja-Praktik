package com.praktikerja.artoz.utils

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object CurrencyFormatter {
    fun format(amount: Double, currencyCode: String): String {
        val locale = getLocaleForCurrency(currencyCode)
        val formatter = NumberFormat.getCurrencyInstance(locale)
        formatter.currency = Currency.getInstance(currencyCode)

        // Format angka dan tambahkan spasi setelah simbol mata uang
        return formatter.format(amount).replace(formatter.currency.symbol, "${formatter.currency.symbol} ")
    }

    private fun getLocaleForCurrency(currencyCode: String): Locale {
        return when (currencyCode) {
            "IDR" -> Locale("id", "ID") // Indonesia
            "USD" -> Locale("en", "US") // US Dollar
            "MYR" -> Locale("ms", "MY") // Malaysia
            "SGD" -> Locale("en", "SG") // Singapore
            "THB" -> Locale("th", "TH") // Thailand
            "PHP" -> Locale("en", "PH") // Philippines
            "VND" -> Locale("vi", "VN") // Vietnam
            "MMK" -> Locale("my", "MM") // Myanmar
            "KHR" -> Locale("km", "KH") // Cambodia
            "LAK" -> Locale("lo", "LA") // Laos
            "BND" -> Locale("ms", "BN") // Brunei
            "EUR" -> Locale("fr", "FR") // Euro
            "JPY" -> Locale("ja", "JP") // Japan
            "GBP" -> Locale("en", "GB") // UK
            "AUD" -> Locale("en", "AU") // Australia
            "CAD" -> Locale("en", "CA") // Canada
            "CNY" -> Locale("zh", "CN") // China
            "KRW" -> Locale("ko", "KR") // Korea
            else -> Locale.getDefault() // Default ke Locale sistem
        }
    }
}
