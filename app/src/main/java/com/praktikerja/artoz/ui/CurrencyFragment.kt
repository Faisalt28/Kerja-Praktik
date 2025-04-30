package com.praktikerja.artoz.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.praktikerja.artoz.data.RetrofitInstance
import com.praktikerja.artoz.data.ExchangeRateResponse
import com.praktikerja.artoz.databinding.FragmentCurrencyBinding
import com.praktikerja.artoz.utils.PreferenceManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CurrencyFragment : Fragment() {

    private var _binding: FragmentCurrencyBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferenceManager: PreferenceManager
    private var currencyChangeListener: CurrencyChangeListener? = null

    private val currencyMap = linkedMapOf(
        "USD" to "Dollar AS (USD)",
        "IDR" to "Rupiah (IDR)",
        "MYR" to "Ringgit (MYR)",
        "SGD" to "Dollar Singapura (SGD)",
        "THB" to "Baht (THB)",
        "PHP" to "Peso (PHP)",
        "VND" to "Dong (VND)",
        "MMK" to "Kyat (MMK)",
        "KHR" to "Riel (KHR)",
        "LAK" to "Kip (LAK)",
        "BND" to "Dollar Brunei (BND)",
        "EUR" to "Euro (EUR)",
        "JPY" to "Yen (JPY)",
        "GBP" to "Poundsterling (GBP)",
        "AUD" to "Dollar Australia (AUD)",
        "CAD" to "Dollar Kanada (CAD)",
        "CNY" to "Yuan (CNY)",
        "KRW" to "Won (KRW)"
    )

    private var selectedCurrencyCode = "IDR"

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CurrencyChangeListener) {
            currencyChangeListener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCurrencyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenceManager = PreferenceManager(requireContext())
        selectedCurrencyCode = preferenceManager.getCurrency()

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, currencyMap.values.toList())
        binding.spinnerCurrency.adapter = adapter

        val selectedPosition = currencyMap.keys.indexOf(selectedCurrencyCode)
        binding.spinnerCurrency.setSelection(if (selectedPosition != -1) selectedPosition else 0)

        binding.spinnerCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCurrencyCode = currencyMap.keys.elementAt(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.btnConvert.setOnClickListener {
            fetchExchangeRate("IDR", selectedCurrencyCode)
        }
    }

    private fun fetchExchangeRate(baseCurrency: String, targetCurrency: String) {
        showLoading(true)

        RetrofitInstance.api.getExchangeRates(baseCurrency).enqueue(object : Callback<ExchangeRateResponse> {
            override fun onResponse(call: Call<ExchangeRateResponse>, response: Response<ExchangeRateResponse>) {
                showLoading(false)

                if (response.isSuccessful) {
                    val rates = response.body()?.conversion_rates
                    val rate = rates?.get(targetCurrency) ?: 1.0
                    preferenceManager.setExchangeRate(rate.toFloat())
                    preferenceManager.setCurrency(targetCurrency)
                    currencyChangeListener?.onCurrencyChanged(rate, targetCurrency)
                } else {
                    Toast.makeText(requireContext(), "Gagal mengambil kurs", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ExchangeRateResponse>, t: Throwable) {
                showLoading(false)
                Toast.makeText(requireContext(), "Terjadi kesalahan jaringan", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.lottieLoading.visibility = View.VISIBLE
            binding.lottieLoading.playAnimation()
            binding.btnConvert.isEnabled = false
        } else {
            binding.lottieLoading.visibility = View.GONE
            binding.lottieLoading.cancelAnimation()
            binding.btnConvert.isEnabled = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface CurrencyChangeListener {
        fun onCurrencyChanged(exchangeRate: Double, currencyCode: String)
    }
}
