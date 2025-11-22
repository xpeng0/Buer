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
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cscyxp.buer.databinding.ActivityMainBinding

private const val TAG = "MainActivity"


class MainActivity: AppCompatActivity() {
    private lateinit var binding :ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private val topFragmentIds: MutableSet<Int> = mutableSetOf(R.id.homeFragment, R.id.chartFragment)

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
    }


}