package com.cscyxp.finance.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cscyxp.finance.R
import com.cscyxp.finance.StockListAdapter
import com.cscyxp.finance.databinding.FragmentMainBinding
import com.cscyxp.finance.viewModel.StockViewModel
import com.google.android.material.divider.MaterialDividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment: Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val stockViewModel: StockViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val stockListAdapter = StockListAdapter()
        val divider = MaterialDividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL).apply {
            // 🌟 1. 核心魔法：告诉它最后一行不要画线！
            // isLastItemDecorated = false

            // 🌟 2. 直接用代码设置颜色，告别 XML
            dividerColor = "#F5F5F5".toColorInt()

            // 🌟 3. 设置粗细（单位是像素，如果你想要 1dp，可以自己转换一下，或者直接写数字）
            dividerThickness = 2

            // （可选）甚至可以设置左右边距，比如左边空出 16dp
            // dividerInsetStart = 40
        }
        binding.rvStocks.apply {
            adapter = stockListAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(divider)
        }
        binding.tvSearch.setOnClickListener { v ->
            findNavController().navigate(R.id.action_financeFragment_to_searchFragment)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                stockViewModel.watchlistUiStateFlow.collect {
                    stockListAdapter.submitList(it)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}