package com.cscyxp.buer

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cscyxp.buer.databinding.ActivityMainBinding

private const val TAG = "MainActivity"


class MainActivity: AppCompatActivity() {
    private lateinit var binding :ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setContentView(binding.root)
//        val adapter = TransactionAdapter {}
//        binding.rvRecentTransactions.adapter = adapter
//        binding.rvRecentTransactions.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//        viewModel.transactions.observe(this) { transitions ->
//            Log.d(TAG, "observe: ")
//            adapter.submitList(transitions) {
//                binding.rvRecentTransactions.scrollToPosition(0)
//            }
//        }
//        binding.btnAdd.setOnClickListener {
//            // viewModel.addTransaction(Transaction(1, "新交易", amount = 12.00))
//            Log.d(TAG, "click add: ")
//            findNavController(R.id.nav_host_fragment).navigate(R.id.action_homeFragment_to_detailFragment)
//        }
    }


}