package com.praktikerja.artoz.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object CurrencyFormatter {
    fun formatRupiah(amount: Double): String {
        val symbols = DecimalFormatSymbols(Locale("id", "ID")).apply {
            groupingSeparator = '.'
            decimalSeparator = ','
        }
        val decimalFormat = DecimalFormat("#,##0.00", symbols)
        return "Rp${decimalFormat.format(amount)}"
    }
}
