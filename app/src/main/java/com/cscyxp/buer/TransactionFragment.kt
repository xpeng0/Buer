package com.cscyxp.buer

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.cscyxp.buer.databinding.DialogCategoryPickerBinding
import com.cscyxp.buer.databinding.DialogMonthPickerBinding
import com.cscyxp.buer.databinding.FragmentHomeBinding
import com.cscyxp.buer.utils.NotificationUtil
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "HomeFragment"
@AndroidEntryPoint
class TransactionFragment: Fragment() {

    // 只能在 onCreateView/onViewCreated 之间访问
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private var monthPickerDialog: BottomSheetDialog? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, systemBars.top, v.paddingRight, systemBars.bottom)
            insets
        }
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.i(TAG, "onViewCreated: --------------")

        binding.tvMonth.setOnClickListener {
            if (monthPickerDialog == null) {
                monthPickerDialog = createMonthPickerDialog()
            }
            monthPickerDialog?.show()
        }

        binding.tvCategory.setOnClickListener {
            createCategoryChangeDialog(5) { category, dialog ->
                viewModel.setCategory(category)
                dialog.dismiss()
            }.show()
        }


        binding.btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_transactionFragment_to_addFragment)
        }
        val adapter = DailyTransactionAdapter { adapter, position, item, binding ->
            createCategoryChangeDialog(5) {category, dialog ->
                val newTransaction = item.copy(categoryId = category.id)
                // 更新数据库
                viewModel.updateTransaction(newTransaction)
                dialog.dismiss()
            }.show()
        }
        binding.rvRecentTransactions.adapter = adapter
        binding.rvRecentTransactions.itemAnimator = null
        lifecycleScope.launch {
            launch {
                viewModel.homeUiState.collect { homeUiState ->
                    adapter.submitList(homeUiState.dailyTransactions) {
                        // rv显示后再滑动
                        binding.rvRecentTransactions.smoothScrollToPosition(0)
                    }
                    binding.tvExpenseValue.text = homeUiState.expenseSumStr
                    binding.tvIncomeValue.text = homeUiState.incomeSumStr
                    binding.tvBalanceValue.text = homeUiState.balanceStr
                }
            }

            launch {
                viewModel.filter.collect {
                    binding.tvMonth.text = "${it.month}月收支"
                    binding.tvCategory.text = it.category?.name ?: "全部类型"
                }
            }

            launch {
                // 日支出变化时 月支出一定变化
                // 所以监听月支出就够
                viewModel.monthExpend.collect {
                    NotificationUtil.notifyBase(viewModel.todayExpend.value, it)
                }
            }
        }
        binding.rvRecentTransactions.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        monthPickerDialog?.dismiss()
        monthPickerDialog = null
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
            dialog.dismiss()
        }
        monthPickerBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        return dialog;
    }

    private fun createCategoryChangeDialog(rowCount: Int, onCategoryClick: (category: Category, dialog: BottomSheetDialog) -> Unit): BottomSheetDialog {
        val categoryPickerBinding = DialogCategoryPickerBinding.inflate(layoutInflater)
        val categoryPickerDialog = BottomSheetDialog(requireContext())
        val adapter = GridCategoryExpandAdapter(
            emptyList<Category>().toMutableList(),
            onParentsCategoryClick =  { expandAdapter, position, category, _ ->
                if (!category.sonCategories.isNullOrEmpty()) {
                    expandAdapter.handleParentsExpand(position, category, rowCount)
                } else {
                    onCategoryClick(category, categoryPickerDialog)
                }
            },
            onSonCategoryClick = { _, _, category, _ ->
                onCategoryClick(category, categoryPickerDialog)
            }
        )
        categoryPickerBinding.apply {
            rvCategories.adapter = adapter

            rvCategories.layoutManager = GridLayoutManager(categoryPickerBinding.rvCategories.context, rowCount).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        val viewType = adapter.getItemViewType(position)
                        return if (viewType == GridCategoryExpandAdapter.TYPE_CHILD) {
                            rowCount // 如果是子项，占满全部列
                        } else {
                            1 // 如果是父项，占 1 列
                        }
                    }
                }
            }

            tvExpend.setOnClickListener {
                viewModel.setCategoryDialogFilterType(Category.TYPE_EXPAND)
            }

            tvIncome.setOnClickListener {
                viewModel.setCategoryDialogFilterType(Category.TYPE_INCOME)
            }
        }


        val job = viewLifecycleOwner.lifecycleScope.launch {
            launch {
                // 加载数据
                viewModel.topCategoriesByFilter.collect { categories ->
                    adapter.dataList = categories.toMutableList()
                    adapter.notifyDataSetChanged()
                }
            }

            launch {
                viewModel.categoryFilter.collect { type ->
                    if (type == Category.TYPE_EXPAND) {
                        categoryPickerBinding.tvExpend.setTypeface(null, Typeface.BOLD)
                        categoryPickerBinding.tvIncome.setTypeface(null)

                    } else {
                        categoryPickerBinding.tvExpend.setTypeface(null)
                        categoryPickerBinding.tvIncome.setTypeface(null, Typeface.BOLD)
                    }
                }
            }
        }

        categoryPickerDialog.apply {
            setContentView(categoryPickerBinding.root)
            window?.navigationBarColor = Color.TRANSPARENT
            setOnDismissListener {
                job.cancel()
            }
        }
        return categoryPickerDialog
    }
}