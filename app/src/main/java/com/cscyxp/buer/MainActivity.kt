package com.cscyxp.buer

import android.os.Bundle
import android.os.Looper
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
import com.cscyxp.finance.watchlist.WatchlistPreloader
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity: AppCompatActivity() {
    private lateinit var binding :ActivityMainBinding
    private val topFragmentIds: MutableSet<Int> = mutableSetOf(R.id.homeFragment, R.id.chartFragment, com.cscyxp.finance.R.id.watchlistFragment)
    @Inject lateinit var watchlistPreloader: Lazy<WatchlistPreloader>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
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

        // 🌟 2. 依然等待首页 UI 渲染完毕，主线程彻底空闲
        Looper.myQueue().addIdleHandler {

            // 🌟 3. 切到子线程去触发 Hilt 的实例化！
            lifecycleScope.launch(Dispatchers.Default) {

                // 此时调用 .get()，Hilt 才会真正开始 new Preloader() 和 Repository()。
                // 因为我们在 Dispatchers.Default 中，所以那些耗时的构造函数、数据库初始化，全都在子线程完成！绝对不卡 UI！
                val preloader = watchlistPreloader.get()

                // 然后开始预热
                preloader.preheat(20)
            }

            false // 只执行一次
        }
    }


}