package com.cscyxp.buer

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cscyxp.buer.databinding.ActivityMainBinding
import com.cscyxp.buer.db.AppDataBase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity: AppCompatActivity() {
    private lateinit var binding :ActivityMainBinding
    private val topFragmentIds: MutableSet<Int> = mutableSetOf(R.id.homeFragment, R.id.chartFragment, com.cscyxp.finance.R.id.financeFragment)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setContentView(binding.root)
        binding.bottomNav.apply {
            post {
                // 不放在post中 findNavController(R.id.nav_host_fragment)会为空
                val navController = findNavController(R.id.nav_host_fragment)
                navController.addOnDestinationChangedListener {_, destination, _ ->
                    binding.bottomNav.post {
                        if (destination.id in topFragmentIds) {
                            binding.bottomNav.visibility = View.VISIBLE
                        } else {
                            binding.bottomNav.visibility = View.GONE
                        }
                    }
                }
                setupWithNavController(navController)
            }
        }

        lifecycleScope.launch {
            launch {
                // 读取json文件中的默认分类
                val defaultCategories = RawUtil.loadCategoriesFromRaw()
                //插入数据库中
                AppDataBase.instance.categoryDao().insertList(defaultCategories)
            }
        }
    }


}