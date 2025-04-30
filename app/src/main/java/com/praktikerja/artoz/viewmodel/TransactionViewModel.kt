package com.praktikerja.artoz.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.praktikerja.artoz.data.TransactionDatabase
import com.praktikerja.artoz.data.TransactionEntity
import kotlinx.coroutines.launch

class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    private val transactionDao = TransactionDatabase.getDatabase(application).transactionDao()

    val allTransactions: LiveData<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val totalIncome: LiveData<Double> = transactionDao.getTotalIncome()
    val totalExpense: LiveData<Double> = transactionDao.getTotalExpense()

    val totalBalance = MediatorLiveData<Double>().apply {
        addSource(totalIncome) { updateBalance() }
        addSource(totalExpense) { updateBalance() }
    }

    private fun updateBalance() {
        val income = totalIncome.value ?: 0.0
        val expense = totalExpense.value ?: 0.0
        totalBalance.value = income - expense
    }

    fun insert(transaction: TransactionEntity) = viewModelScope.launch {
        transactionDao.insert(transaction)
    }

    fun update(transaction: TransactionEntity) = viewModelScope.launch {
        transactionDao.update(transaction)
    }

    fun delete(transaction: TransactionEntity) = viewModelScope.launch {
        transactionDao.delete(transaction)
    }
}
