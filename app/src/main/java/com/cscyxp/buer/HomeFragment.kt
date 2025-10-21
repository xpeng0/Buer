package com.cscyxp.buer

import android.graphics.Color
import android.os.Bundle
import android.provider.CalendarContract.Colors
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.cscyxp.buer.databinding.DialogCategoryPickerBinding
import com.cscyxp.buer.databinding.DialogMonthPickerBinding
import com.cscyxp.buer.databinding.FragmentHomeBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Locale

private const val TAG = "HomeFragment"
class HomeFragment: Fragment() {

    // 只能在 onCreateView/onViewCreated 之间访问
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private var monthPickerDialog: BottomSheetDialog? = null

    private var categoryPickerDialog: BottomSheetDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.tvMonth.setOnClickListener {
            if (monthPickerDialog == null) {
                monthPickerDialog = createMonthPickerDialog()
            }
            monthPickerDialog?.show()
        }

        binding.tvCategory.setOnClickListener {
            if (categoryPickerDialog == null) {
                categoryPickerDialog = createCategoryDialog()
            }
            categoryPickerDialog?.show()
        }


        binding.btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_detailFragment)
        }
        // val adapter = DailyTransactionAdapter()
        binding.rvRecentTransactions.adapter = viewModel.adapter
        binding.rvRecentTransactions.itemAnimator = null
        lifecycleScope.launch {
            viewModel.dailyTransactions.collect { dailyTransactions ->
                viewModel.adapter.submitList(dailyTransactions) {
                    // rv显示后再滑动
                    binding.rvRecentTransactions.smoothScrollToPosition(0)
                }

                val expense = dailyTransactions.sumOf { it.expense }
                val income = dailyTransactions.sumOf { it.income }
                binding.tvExpenseValue.text = String.format(Locale.getDefault(), "%.2f", expense)
                binding.tvIncomeValue.text = String.format(Locale.getDefault(), "%.2f", income)
                binding.tvBalanceValue.text = String.format(Locale.getDefault(), "%.2f", income - expense)
            }
        }
        binding.rvRecentTransactions.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        monthPickerDialog?.dismiss()
        monthPickerDialog = null
        categoryPickerDialog?.dismiss()
        categoryPickerDialog = null
    }

    private fun createMonthPickerDialog(): BottomSheetDialog {
        val monthPickerBinding = DialogMonthPickerBinding.inflate(layoutInflater)
        val npMonth = monthPickerBinding.npMonth
        npMonth.maxValue = 12
        npMonth.minValue = 1
        npMonth.value = viewModel.getMonth()
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(monthPickerBinding.root)
        // todo 有什么更好的BottomSheetDialog去除底部导航黑边，适配全面屏的方法?
        dialog.window?.navigationBarColor = Color.TRANSPARENT
        monthPickerBinding.btnConfirm.setOnClickListener {
            val month = monthPickerBinding.npMonth.value
            viewModel.setMonth(month)
            binding.tvMonth.text = "${month}月收支"
            dialog.dismiss()
        }
        monthPickerBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        return dialog;
    }

    private fun createCategoryDialog(): BottomSheetDialog {
        val categoryPickerBinding = DialogCategoryPickerBinding.inflate(layoutInflater)
        val categoryPickerDialog = BottomSheetDialog(requireContext())
        categoryPickerDialog.setContentView(categoryPickerBinding.root)
        categoryPickerDialog.window?.navigationBarColor = Color.TRANSPARENT

        val adapter = CategoryGridAdapter { adapter, position ->
            val category = adapter.currentList[position]
            viewModel.setCategory(category.id)
            binding.tvCategory.text = category.name
            categoryPickerDialog.dismiss()
        }
        adapter.submitList(TransactionRepository.categories.take(10))

        categoryPickerBinding.rvCategories.adapter = adapter
        categoryPickerBinding.rvCategories.layoutManager = GridLayoutManager(categoryPickerBinding.rvCategories.context, 5)
        return categoryPickerDialog
    }
}