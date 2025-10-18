package com.cscyxp.buer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.cscyxp.buer.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch
import java.time.LocalDate

private const val TAG = "HomeFragment"
class HomeFragment: Fragment() {

    // 只能在 onCreateView/onViewCreated 之间访问
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_detailFragment)
        }
        // val adapter = DailyTransactionAdapter()
        binding.rvRecentTransactions.adapter = viewModel.adapter
        binding.rvRecentTransactions.itemAnimator = NoFadeItemAnimator().apply {
            supportsChangeAnimations = false
        }
        lifecycleScope.launch {
            viewModel.dailyTransactions.collect { dailyTransactions ->
                viewModel.adapter.submitList(dailyTransactions) {
                    // rv显示后再滑动
                    binding.rvRecentTransactions.smoothScrollToPosition(0)
                }
            }
        }
        binding.rvRecentTransactions.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

    }
}