package com.praktikerja.artoz.ui.book

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.praktikerja.artoz.R
import com.praktikerja.artoz.adapter.TransactionAdapter
import com.praktikerja.artoz.data.TransactionEntity
import com.praktikerja.artoz.databinding.FragmentBookBinding
import com.praktikerja.artoz.ui.add_transaction.AddActivity
import com.praktikerja.artoz.utils.CurrencyFormatter
import com.praktikerja.artoz.utils.PreferenceManager
import com.praktikerja.artoz.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.*

class BookFragment : Fragment() {

    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var adapter: TransactionAdapter
    private lateinit var binding: FragmentBookBinding
    private var selectedDate: Long? = null
    private lateinit var preferenceManager: PreferenceManager
    private var selectedCurrency = "IDR"
    private var exchangeRate = 1.0
    private var isStaggedGridLayout = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookBinding.inflate(inflater, container, false)
        preferenceManager = PreferenceManager(requireContext())

        loadCurrencySettings()
        setupUI()
        return binding.root
    }

    private fun loadCurrencySettings() {
        selectedCurrency = preferenceManager.getCurrency()
        exchangeRate = preferenceManager.getExchangeRate().toDouble()
    }

    private fun setupUI() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = TransactionAdapter { transaction -> showTransactionDetailDialog(transaction) }
        binding.recyclerView.adapter = adapter

        // Pastikan adapter diperbarui dengan exchange rate saat ini
        adapter.updateExchangeRate(exchangeRate, selectedCurrency)

        transactionViewModel = ViewModelProvider(this)[TransactionViewModel::class.java]
        transactionViewModel.allTransactions.observe(viewLifecycleOwner) { transactions ->
            transactions?.let {
                val sortedTransactions = it.sortedByDescending { transaction -> transaction.date }
                adapter.submitList(sortedTransactions)
                updateTotals(sortedTransactions)
            }
        }

        binding.toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            val btnFilter = binding.btnFilter
            if (checkedId == R.id.btnFilter) {
                if (isChecked) {
                    showDatePickerDialog()
                    btnFilter.icon = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_filter_list_alt_24)
                    btnFilter.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorActive))
                } else {
                    resetTransactions()
                    btnFilter.icon = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_filter_alt_off_24)
                    btnFilter.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorInactive))
                }
            }
        }

        binding.btnLayout.setOnClickListener {
            toggleLayoutManager()
        }
    }

    private fun toggleLayoutManager() {
        isStaggedGridLayout = !isStaggedGridLayout

        if (isStaggedGridLayout) {
            binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            binding.btnLayout.icon = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_grid_view_24)
        } else {
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.btnLayout.icon = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_format_list_bulleted_24)
        }
    }


    private fun showTransactionDetailDialog(transaction: TransactionEntity) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_transaction_detail, null)
        val dialog = AlertDialog.Builder(requireContext()).setView(dialogView).create()

        val formattedAmount = CurrencyFormatter.format(transaction.amount * exchangeRate, selectedCurrency)
        dialogView.findViewById<TextView>(R.id.tvCategory).text = transaction.category
        dialogView.findViewById<TextView>(R.id.tvAmount).text = formattedAmount
        dialogView.findViewById<TextView>(R.id.tvDate).text = formatDate(transaction.date)

        dialogView.findViewById<View>(R.id.btnEdit).setOnClickListener {
            startActivity(Intent(requireContext(), AddActivity::class.java).apply {
                putExtra("TRANSACTION_ID", transaction.id)
                putExtra("TRANSACTION_AMOUNT", transaction.amount)
                putExtra("TRANSACTION_CATEGORY", transaction.category)
                putExtra("TRANSACTION_DATE", transaction.date)
                putExtra("TRANSACTION_TYPE", transaction.type)
            })
            dialog.dismiss()
        }

        dialogView.findViewById<View>(R.id.btnDelete).setOnClickListener {
            showDeleteConfirmationDialog(transaction, dialog)
        }

        dialog.show()
    }

    private fun showDeleteConfirmationDialog(transaction: TransactionEntity, parentDialog: AlertDialog) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Transaksi")
            .setMessage("Apakah Anda yakin ingin menghapus transaksi ini?")
            .setPositiveButton("Hapus") { _, _ ->
                transactionViewModel.delete(transaction)
                parentDialog.dismiss()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                selectedDate = Calendar.getInstance().apply { set(year, month, dayOfMonth) }.timeInMillis
                filterTransactionsByDate()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun filterTransactionsByDate() {
        selectedDate?.let { date ->
            val selectedCalendar = Calendar.getInstance().apply { timeInMillis = date }

            transactionViewModel.allTransactions.value?.let { transactions ->
                val filteredTransactions = transactions.filter {
                    val transactionCalendar = Calendar.getInstance().apply { timeInMillis = it.date }
                    transactionCalendar.get(Calendar.YEAR) == selectedCalendar.get(Calendar.YEAR) &&
                            transactionCalendar.get(Calendar.MONTH) == selectedCalendar.get(Calendar.MONTH) &&
                            transactionCalendar.get(Calendar.DAY_OF_MONTH) == selectedCalendar.get(Calendar.DAY_OF_MONTH)
                }.sortedByDescending { it.date }

                adapter.submitList(filteredTransactions)
                updateTotals(filteredTransactions)
            }
        }
    }

    private fun resetTransactions() {
        transactionViewModel.allTransactions.value?.let { transactions ->
            val sortedTransactions = transactions.sortedByDescending { it.date }
            adapter.submitList(sortedTransactions)
            updateTotals(sortedTransactions)
            binding.emptyView.visibility = if (sortedTransactions.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun updateTotals(transactions: List<TransactionEntity>) {
        val totalIncome = transactions.filter { it.type == "Pemasukan" }.sumOf { it.amount }
        val totalExpense = transactions.filter { it.type == "Pengeluaran" }.sumOf { it.amount }

        binding.tvTotalIncomeAmount.text = CurrencyFormatter.format(totalIncome * exchangeRate, selectedCurrency)
        binding.tvTotalExpenseAmount.text = CurrencyFormatter.format(totalExpense * exchangeRate, selectedCurrency)
        binding.emptyView.visibility = if (transactions.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(timestamp))
    }
}
