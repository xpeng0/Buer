package com.cscyxp.buer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.GridLayout
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.cscyxp.buer.databinding.FragmentAddBinding
import com.cscyxp.buer.databinding.ItemKeyBinding
import com.cscyxp.buer.databinding.ItemTagBinding
import com.cscyxp.buer.databinding.PopupSonCategoryBinding
import com.cscyxp.xpviews.dp
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val TAG = "AddFragment"
private const val TAG_DATE_TEXT = "KEY_DATE_TEXT"

@AndroidEntryPoint
class AddFragment: Fragment() {

    // 只能在 onCreateView/onViewCreated 之间访问
    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!

    private val addViewModel: AddViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.i(TAG, "onCreateView: -----------")
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, systemBars.top, v.paddingRight, systemBars.bottom)
            insets
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.i(TAG, "onViewCreated: ----------")
        super.onViewCreated(view, savedInstanceState)
        // 定义键盘布局（文字键用字符串，图标键用特殊标识）
        KeyAction.LAYOUT_ORDER.forEach{ key ->
            val keyBinding = ItemKeyBinding.inflate(layoutInflater, binding.glNums, false)
            if (key == KeyAction.DATE) {
                keyBinding.tvKeyText.tag = TAG_DATE_TEXT
            }
            keyBinding.tvKeyText.text = key.keyName
            keyBinding.root.setOnClickListener {
                addViewModel.handleKeyAction(key)
            }
            // 设置 LayoutParams
            val lp = GridLayout.LayoutParams().apply {
                width = 70.dp
                height = 55.dp
                if (key == KeyAction.OK) {
                    height = 179.dp
                }
                // 关键：使用枚举中的位置信息
                rowSpec = GridLayout.spec(key.row, key.rowSpan)
                columnSpec = GridLayout.spec(key.column, key.colSpan)
            }
            keyBinding.root.layoutParams = lp
            binding.glNums.addView(keyBinding.root)
        }
        lifecycleScope.launch {
            launch {
                addViewModel.date.collect { localDate ->
                    val keyText = binding.glNums.findViewWithTag<TextView>(TAG_DATE_TEXT)
                    localDate?.let {
                        keyText?.text = localDate.format(DateTimeFormatter.ofPattern("MM/dd"))
                    } ?: run {
                        keyText?.text = "今天"
                    }

                }
            }

            launch {
                addViewModel.amount.collect { amountStr ->
                    binding.tvAmount.text = amountStr
                }
            }

            launch {
                addViewModel.openDatePicker.collect {
                    val datePickerDialog = MaterialDatePicker.Builder.datePicker()
                        .setSelection(addViewModel.getCheckDate())
                        .build()

                    datePickerDialog.addOnPositiveButtonClickListener(addViewModel::onDateCheckListener)
                    datePickerDialog.show(parentFragmentManager, "")
                }

            }

            launch {
                addViewModel.back.collect {
                    findNavController().popBackStack()
                }
            }
        }

        lifecycleScope.launch {
            binding.vpTags.adapter = CategoryPagerAdapter(
                categoryPages = addViewModel.getTopCategoryGrids(),
                selectedFlowForPage = { page ->
                    addViewModel.selectedCategoryState.map {
                        Log.i(TAG, "selectedCategoryState change $it")
                        if (it.page == page) {
                            CategoryGridAdapterState(
                                position = it.grid,
                                category = it.category
                            )
                        } else {
                            CategoryGridAdapterState()
                        }
                    }.distinctUntilChanged()
                },
                onCategoryClick = { pagerAdapter, gridAdapter, pagePosition, gridPosition, category, binding ->
                    // 更新选中信息
                    addViewModel.onTagClick(pagePosition, gridPosition, category)
                    showPopup(category, binding)
                }
            )
        }
    }

    fun showPopup(category: Category, binding: ItemTagBinding) {
        val categoryRepository = CategoryRepository()
        lifecycleScope.launch {
            val sonCategories = categoryRepository.getSonCategories(category.id)
            if (sonCategories.isEmpty()) return@launch

            // 展示小类popup window
            val popupBinding = PopupSonCategoryBinding.inflate(layoutInflater)

            val popup = PopupWindow(popupBinding.root).apply {
                width = LayoutParams.WRAP_CONTENT
                height = LayoutParams.WRAP_CONTENT
                isOutsideTouchable = true
                setOnDismissListener {
                    this@AddFragment.binding.glNums.apply {
                        postDelayed({
                            children.forEach { v ->
                                v.isClickable = true
                            }
                        }, 100)
                    }
                }
            }
            val adapter = CategoryGridAdapter() { _, _, sonCategory, _ ->
                addViewModel.onSonCategoryClick(sonCategory)
                val resId = MyApp.appContext.resources.getIdentifier(
                    sonCategory.icon,       // 文件名
                    "drawable",      // 资源类型
                    MyApp.appContext.packageName // 包名
                )
                if (resId != 0) binding.ivTagIcon.setImageResource(resId)
                popup.dismiss()
            }
            adapter.submitList(sonCategories)

            popupBinding.rvSonCategories.layoutManager = GridLayoutManager(requireContext(), 5, GridLayoutManager.VERTICAL, false)
            popupBinding.rvSonCategories.adapter = adapter

            // 弹出popup时，屏蔽数字键盘点击事件
            this@AddFragment.binding.glNums.children.forEach { v ->
                v.isClickable = false
            }
            popup.showAsDropDown(binding.root)
        }
    }

}