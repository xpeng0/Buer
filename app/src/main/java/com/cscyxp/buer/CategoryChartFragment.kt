package com.cscyxp.buer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.cscyxp.buer.databinding.FragmentChartBinding
import com.cscyxp.buer.databinding.FragmnetCategoryChartBinding
import kotlinx.coroutines.launch
import java.time.LocalDate

private const val TAG = "CategoryChartFragment"
class CategoryChartFragment: Fragment() {
    private var _binding: FragmnetCategoryChartBinding? = null
    private val binding get() = _binding!!
    private val chartViewModel: ChartViewModel by activityViewModels()

    private val args: CategoryChartFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmnetCategoryChartBinding.inflate(inflater, container, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, systemBars.top, v.paddingRight, systemBars.bottom)
            insets
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.i(TAG, "onViewCreated: args: ${args.categoryId}")
        val categoryChartAdapter = CategoryChartAdapter { _, _, _ ->

        }
        binding.rvCategoryChart.itemAnimator = null
        binding.rvCategoryChart.adapter = categoryChartAdapter
        binding.rvCategoryChart.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        lifecycleScope.launch {
            launch {
                chartViewModel.getCategoryChartUiState(args.categoryId).collect {
                    Log.i(TAG, "onViewCreated: ${it.first}")
                    binding.pc.setData(it.first)
                    categoryChartAdapter.submitList(it.second)
                }
            }
        }
    }
}