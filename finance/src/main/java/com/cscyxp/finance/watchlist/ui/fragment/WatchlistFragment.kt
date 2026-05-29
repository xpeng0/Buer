package com.cscyxp.finance.watchlist.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cscyxp.finance.R
import com.cscyxp.finance.watchlist.ui.composable.WatchlistScreenRoute

class WatchlistFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                WatchlistScreenRoute(
                    onSearchClick = {
                        findNavController().navigate(R.id.action_watchlistFragment_to_searchFragment)
                    },
                    onStockClick = { stockKey ->
                        val action = WatchlistFragmentDirections.actionWatchlistFragmentToDetailFragment(stockKey)
                        findNavController().navigate(action)
                    }
                )
            }
        }
    }
}
