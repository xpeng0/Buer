package com.cscyxp.finance.search.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.cscyxp.finance.databinding.FragmentSerchBinding
import com.cscyxp.finance.search.ui.StockSearchListAdapter
import com.cscyxp.finance.search.ui.state.SearchResultState
import com.cscyxp.finance.search.ui.state.SearchScreenUiState
import com.cscyxp.finance.search.vm.SearchViewModel
import com.google.android.material.divider.MaterialDividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class SearchFragment: Fragment() {

    private var _binding: FragmentSerchBinding? = null
    private val binding get() = _binding!!
    private val searchViewModel: SearchViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSerchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val listAdapter = StockSearchListAdapter(searchViewModel::watchStock)
        binding.rvStockSearch.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = listAdapter
            addItemDecoration(MaterialDividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL).apply {
                dividerColor = "#F5F5F5".toColorInt()
                dividerThickness = 2
            })
        }

        binding.etSearchInput.doAfterTextChanged {
            searchViewModel.changeSearchInput(it.toString())
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                searchViewModel.searchScreenState.collect { screenState ->
                    when (screenState) {
                        is SearchScreenUiState.SearchBoard -> {
                            binding.rvStockSearch.visibility = View.GONE
                        }
                        is SearchScreenUiState.SearchResult -> {
                            val resultState = screenState.resultState
                            when (resultState) {
                                is SearchResultState.Error -> {

                                }
                                is SearchResultState.Loading -> {

                                }
                                is SearchResultState.Success -> {
                                    listAdapter.submitList(resultState.stockSearchItems)
                                    binding.rvStockSearch.visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                }
            }
        }

        binding.etSearchInput.requestFocus()
        // 利用 post 延迟到 View 渲染挂载完毕后的下一帧去呼叫键盘
        binding.etSearchInput.post {
            // 使用现代的 WindowInsets API 来优雅地弹出键盘 (IME)
            WindowCompat.getInsetsController(requireActivity().window, binding.etSearchInput)
                .show(WindowInsetsCompat.Type.ime())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}