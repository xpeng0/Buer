package com.cscyxp.buer.home.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cscyxp.buer.R

class HomeFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return ComposeView(requireContext()).apply {

            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )

            val navController = findNavController()
            setContent {
                BuerHomeScreen(
                    onFinanceClick = { navController.navigate(R.id.action_homeFragment_to_finance) },
                    onBookkeepingClick = { navController.navigate(R.id.action_homeFragment_to_transactionFragment) }
                )
            }
        }
    }
}