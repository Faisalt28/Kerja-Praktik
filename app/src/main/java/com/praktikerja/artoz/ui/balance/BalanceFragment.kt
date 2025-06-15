package com.praktikerja.artoz.ui.balance

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.praktikerja.artoz.databinding.FragmentBalanceBinding
import com.praktikerja.artoz.utils.CurrencyFormatter
import com.praktikerja.artoz.viewmodel.TransactionViewModel

class BalanceFragment : Fragment() {

    private var _binding: FragmentBalanceBinding? = null
    private val binding get() = _binding!!
    private lateinit var transactionViewModel: TransactionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBalanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        transactionViewModel = ViewModelProvider(this)[TransactionViewModel::class.java]

        transactionViewModel.totalIncome.observe(viewLifecycleOwner) { updateUI(it, binding.tvTotalIncome) }
        transactionViewModel.totalExpense.observe(viewLifecycleOwner) { updateUI(it, binding.tvTotalExpense) }
        transactionViewModel.totalBalance.observe(viewLifecycleOwner) { updateUI(it, binding.tvBalanceAmount) }

        binding.webView.apply {
            settings.javaScriptEnabled = true
            webViewClient = WebViewClient()
        }

        binding.chartTypeGroup.setOnCheckedChangeListener { _, _ ->
            updateChart()
        }
    }

    private fun updateUI(amount: Double?, textView: android.widget.TextView) {
        val value = amount ?: 0.0
        textView.text = CurrencyFormatter.formatRupiah(value)
        updateChart()
    }

    private fun updateChart() {
        val income = transactionViewModel.totalIncome.value ?: 0.0
        val expense = transactionViewModel.totalExpense.value ?: 0.0

        val chartType = when {
            binding.rbPie.isChecked -> "pie"
            binding.rbBar.isChecked -> "bar"
            else -> "pie"
        }

        val legendDisplay = if (chartType == "pie") "true" else "false"

        val htmlContent = """
        <!DOCTYPE html>
        <html>
        <head>
            <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
        </head>
        <body>
            <canvas id="myChart" width="200" height="200"></canvas>
            <script>
                document.addEventListener("DOMContentLoaded", function() {
                    var ctx = document.getElementById('myChart').getContext('2d');
                    new Chart(ctx, {
                        type: '$chartType',
                        data: {
                            labels: ['Pemasukan', 'Pengeluaran'],
                            datasets: [{
                                data: [$income, $expense],
                                backgroundColor: [
                                    'rgba(54, 162, 235, 0.8)',
                                    'rgba(255, 99, 132, 0.8)'
                                ],
                                borderColor: [
                                    'rgba(75, 192, 192, 1)',
                                    'rgba(255, 99, 132, 1)'
                                ],
                                borderWidth: 1
                            }]
                        },
                        options: {
                            responsive: true,
                            maintainAspectRatio: false,
                            plugins: {
                                legend: {
                                    display: $legendDisplay
                                }
                            }
                        }
                    });
                });
            </script>
        </body>
        </html>
    """
        binding.webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
