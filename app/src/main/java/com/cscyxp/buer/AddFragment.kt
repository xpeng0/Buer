package com.cscyxp.buer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.cscyxp.buer.databinding.FragmentAddBinding
import com.cscyxp.buer.databinding.ItemKeyBinding

private const val TAG = "AddFragment"

class AddFragment: Fragment() {

    // 只能在 onCreateView/onViewCreated 之间访问
    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()
    private val addViewModel: AddViewModel by viewModels()
    private val amount = MutableLiveData("0.00")
    private var newInput = true

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
        val keys = listOf(
            "7", "8", "9", "date",
            "4", "5", "6", "+",
            "1", "2", "3", "-",
            ".", "0", "del", "ok"
        )
        keys.forEach{ key ->
            val keyBinding = ItemKeyBinding.inflate(layoutInflater, binding.glNums, false)
            keyBinding.tvKeyText.text = key
            keyBinding.root.setOnClickListener {
                onKeyClick(key)
            }
            binding.glNums.addView(keyBinding.root)
        }
        amount.observe(viewLifecycleOwner) { amountStr ->
            binding.tvAmount.text = amountStr
        }

        binding.vpTags.adapter = CategoryPagerAdapter(
            addViewModel.categoryGrids,
            addViewModel::onTagClick
        )
    }

    private fun onKeyClick(key: String) {
        when(key) {
            "data" -> {}
            "del" -> {
                amount.value = amount.value?.dropLast(1)
            }
            "+" -> {}
            "-" -> {}
            "." -> {
                if (amount.value?.contains(".") == false) {
                    amount.value = amount.value?.plus(".")
                }
            }
            "ok" -> {
                viewModel.addTransaction(Transaction(
                    amount = amount.value?.toDoubleOrNull() ?: 0.00,
                    categoryId = addViewModel.selectedCategoryId
                ))
                findNavController().popBackStack()
            }
            else -> {
                val indexOf = amount.value!!.indexOf(".")
                if (newInput) {
                    amount.value = key
                    newInput = false
                } else if (indexOf != -1) {
                    if (amount.value.length - (indexOf + 1) < 2) {
                        amount.value = amount.value?.plus(key)
                    }
                } else {
                    amount.value = amount.value?.plus(key)
                }
            }
        }
    }
}