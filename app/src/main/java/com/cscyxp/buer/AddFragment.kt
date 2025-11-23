package com.cscyxp.buer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.cscyxp.buer.databinding.FragmentAddBinding
import com.cscyxp.buer.databinding.ItemKeyBinding
import com.cscyxp.xpviews.dp
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val TAG = "AddFragment"
private const val TAG_DATE_TEXT = "KEY_DATE_TEXT"

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
                    keyText?.text = if (localDate == LocalDate.now()) {
                        "今天"
                    } else {
                        localDate.format(DateTimeFormatter.ofPattern("MM/dd"))
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

        binding.vpTags.adapter = CategoryPagerAdapter(
            addViewModel.categoryGrids,
            addViewModel::onTagClick
        )
    }
}