package com.cscyxp.buer

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cscyxp.buer.databinding.FragmentChartBinding
import com.cscyxp.xpviews.BarChartView
import com.cscyxp.xpviews.PieChartView.PieEntry
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val TAG = "ChartFragment"
class ChartFragment: Fragment() {

    private var _binding: FragmentChartBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ChartViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.bc.onBarClickListener = { index, barEntry ->
            Log.i(TAG, "onBarClick index $index")
            val month = LocalDate.now().monthValue
            // index是
            viewModel.setMonth(month + index - 5)

        }
        val categoryChartAdapter = CategoryChartAdapter { adapter, position, categoryChart ->
            val action = ChartFragmentDirections.actionChartFragmentToCategoryChartFragment(categoryChart.category.id)
            findNavController().navigate(action)
        }
        binding.rvCategoryChart.itemAnimator = null
        binding.rvCategoryChart.adapter = categoryChartAdapter
        binding.rvCategoryChart.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        lifecycleScope.launch {
            launch {
                viewModel.recentSixMonthBarEntry.collect { barEntries ->
                    Log.i(TAG, "on recentSixMonthTransactions Collected ---- barEntries: $barEntries")
                    binding.bc.setData(barEntries)
                }
            }

            launch {
                viewModel.chartUIState.collect { chartUIState ->
                    Log.i(TAG, "on filterMonthTransactions Collected ---- chartUIState: $chartUIState")
                    binding.pc.setData(chartUIState.first)
                    categoryChartAdapter.submitList(chartUIState.second)
                }
            }
        }


    }
}