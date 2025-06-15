package com.praktikerja.artoz.ui.book

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.praktikerja.artoz.R
import com.praktikerja.artoz.adapter.TransactionAdapter
import com.praktikerja.artoz.data.TransactionEntity
import com.praktikerja.artoz.databinding.FragmentBookBinding
import com.praktikerja.artoz.ui.add_transaction.AddActivity
import com.praktikerja.artoz.utils.CurrencyFormatter.formatRupiah
import com.praktikerja.artoz.utils.PreferencesManager
import com.praktikerja.artoz.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.*

class BookFragment : Fragment() {

    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var adapter: TransactionAdapter
    private lateinit var binding: FragmentBookBinding
    private var isStaggedGridLayout = false
    private lateinit var preferencesManager: PreferencesManager
    private var allTransactions: List<TransactionEntity> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookBinding.inflate(inflater, container, false)
        preferencesManager = PreferencesManager(requireContext())
        isStaggedGridLayout = preferencesManager.isLayoutGrid()
        setupUI()
        return binding.root
    }

    private fun setupUI() {
        adapter = TransactionAdapter { transaction -> showTransactionDetailDialog(transaction) }
        binding.recyclerView.adapter = adapter
        applyLayoutManager()

        transactionViewModel = ViewModelProvider(this)[TransactionViewModel::class.java]
        transactionViewModel.allTransactions.observe(viewLifecycleOwner) { transactions ->
            transactions?.let {
                allTransactions = it.sortedByDescending { t -> t.date }
                adapter.submitList(allTransactions)
                updateTotals(allTransactions)
            }
        }

        binding.btnLayout.setOnClickListener {
            toggleLayoutManager()
        }

        setupSearch()

        binding.searchView.setOnClickListener {
            binding.searchView.isIconified = false
        }
        binding.searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.searchView.isIconified = false
        }
    }

    private fun applyLayoutManager() {
        if (isStaggedGridLayout) {
            binding.recyclerView.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            binding.btnLayout.icon =
                ContextCompat.getDrawable(requireContext(), R.drawable.baseline_grid_view_24)
        } else {
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.btnLayout.icon =
                ContextCompat.getDrawable(requireContext(), R.drawable.baseline_format_list_bulleted_24)
        }
    }

    private fun toggleLayoutManager() {
        isStaggedGridLayout = !isStaggedGridLayout
        applyLayoutManager()
        preferencesManager.setLayoutGrid(isStaggedGridLayout)
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchTransactions(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { searchTransactions(it) }
                return true
            }
        })
    }

    private fun searchTransactions(query: String) {
        val filtered = allTransactions.filter { t ->
            t.category.contains(query, ignoreCase = true) ||
                    t.amount.toString().contains(query) ||
                    formatDate(t.date).contains(query, ignoreCase = true) ||
                    t.type.contains(query, ignoreCase = true)
        }
        adapter.submitList(filtered)
        updateTotals(filtered)
    }

    private fun updateTotals(transactions: List<TransactionEntity>) {
        val totalIncome = transactions.filter { it.type == "Pemasukan" }.sumOf { it.amount }
        val totalExpense = transactions.filter { it.type == "Pengeluaran" }.sumOf { it.amount }

        binding.tvTotalIncomeAmount.text = formatRupiah(totalIncome)
        binding.tvTotalExpenseAmount.text = formatRupiah(totalExpense)
        binding.emptyView.visibility = if (transactions.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun showTransactionDetailDialog(transaction: TransactionEntity) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_transaction_detail, null)
        val dialog = AlertDialog.Builder(requireContext()).setView(dialogView).create()

        dialogView.findViewById<TextView>(R.id.tvCategory).text = transaction.category
        dialogView.findViewById<TextView>(R.id.tvAmount).text = formatRupiah(transaction.amount)
        dialogView.findViewById<TextView>(R.id.tvDate).text = formatDate(transaction.date)

        dialogView.findViewById<View>(R.id.btnEdit).setOnClickListener {
            startActivity(Intent(requireContext(), AddActivity::class.java).apply {
                putExtra("TRANSACTION_ID", transaction.id)
                putExtra("TRANSACTION_AMOUNT", transaction.amount)
                putExtra("TRANSACTION_CATEGORY", transaction.category)
                putExtra("TRANSACTION_DATE", transaction.date)
                putExtra("TRANSACTION_TYPE", transaction.type)
                putExtra("TRANSACTION_ICON", transaction.iconResId)
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

    private fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(timestamp))
    }
}
