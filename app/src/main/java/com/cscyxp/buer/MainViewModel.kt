package com.cscyxp.buer

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.serialization.descriptors.PrimitiveKind

private const val TAG = "MainViewModel"


class MainViewModel: ViewModel() {
    val dailyTransactions = TransactionRepository.getDailyTransactionsFlow()

    val adapter by lazy { DailyTransactionAdapter() }

    fun addTransaction(transaction: Transaction) {
        Log.d(TAG, "addTransaction: ")
        viewModelScope.launch {
            TransactionRepository.addTransaction(transaction)
        }
    }
}