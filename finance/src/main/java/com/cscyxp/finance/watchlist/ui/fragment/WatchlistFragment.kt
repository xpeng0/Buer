package com.cscyxp.finance.watchlist.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cscyxp.finance.R
import com.cscyxp.finance.databinding.FragmentWatchlistBinding
import com.cscyxp.finance.watchlist.ui.adapter.WatchlistAdapter
import com.cscyxp.finance.watchlist.vm.WatchlistViewModel
import com.google.android.material.divider.MaterialDividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

@AndroidEntryPoint
class WatchlistFragment: Fragment() {
    private var _binding: FragmentWatchlistBinding? = null
    private val binding get() = _binding!!
    private val watchlistViewModel: WatchlistViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWatchlistBinding.inflate(inflater, container, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, systemBars.top, v.paddingRight, systemBars.bottom)
            insets
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val stockListAdapter = WatchlistAdapter {
            val action = WatchlistFragmentDirections.actionWatchlistFragmentToDetailFragment(it.stockKey)
            findNavController().navigate(action)
        }
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
            itemAnimator = null
            adapter = stockListAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(divider)
            addOnScrollListener(object : RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    dispatchZonesToViewModel(
                        layoutManager as LinearLayoutManager,
                        adapter as WatchlistAdapter
                    )
                }
            })
        }
        binding.tvSearch.setOnClickListener { v ->
            findNavController().navigate(R.id.action_watchlistFragment_to_searchFragment)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                watchlistViewModel.watchlistUiStates.collect {
                    stockListAdapter.submitList(it)
                }
            }
        }
    }

    private fun dispatchZonesToViewModel(layoutManager: LinearLayoutManager, adapter: WatchlistAdapter) {
        // 获取红区（可视区域）的边界
        val firstVisible = layoutManager.findFirstVisibleItemPosition()
        val lastVisible = layoutManager.findLastVisibleItemPosition()

        // 严谨判断：如果是空列表或者还没布局好，直接返回
        if (firstVisible == RecyclerView.NO_POSITION || lastVisible == RecyclerView.NO_POSITION) {
            return
        }

        val totalCount = adapter.itemCount
        val currentList = adapter.currentList

        // 🔴 计算红区 (Visible Zone)
        val redZoneKeys = currentList.subList(firstVisible, lastVisible + 1).map { it.stockKey }.toSet()

        // 🔵 计算蓝区 (Nearby Zone)
        // 蓝区 = 向上缓冲 10 条 + 向下缓冲 10 条
        val firstNearby = max(0, firstVisible - 10)
        val lastNearby = min(totalCount - 1, lastVisible + 10)

        val blueZoneKeys = currentList.subList(firstNearby, lastNearby).map { it.stockKey }.toSet() - redZoneKeys

        watchlistViewModel.updateVisibleWatchStock(redZoneKeys)
        watchlistViewModel.updateNearbyWatchStock(blueZoneKeys)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}