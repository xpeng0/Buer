package com.cscyxp.buer

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.cscyxp.buer.databinding.FragmentChartBinding
import com.cscyxp.xpviews.BarChartView
import com.cscyxp.xpviews.PieChartView.PieEntry
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

private const val TAG = "ChartFragment"
class ChartFragment: Fragment() {

    private var _binding: FragmentChartBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ChartViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            launch {
                viewModel.recentSixMonthBarEntry.collect { barEntries ->
                    Log.i(TAG, "on recentSixMonthTransactions Collected ---- barEntries: $barEntries")
                    binding.bc.setData(barEntries)
                }
            }

            launch {
                viewModel.currentMonthPieEntry.collect { pieEntries ->
                    Log.i(TAG, "on currentMonthPieEntry Collected ---- pieEntries: $pieEntries")
                    binding.pc.setData(pieEntries)
                }
            }
        }


    }
}