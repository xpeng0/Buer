package com.cscyxp.finance.watchlist

import com.cscyxp.finance.hilt.ApplicationScope
import com.cscyxp.finance.repository.StockRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.min

@Singleton
class WatchlistPreloader @Inject constructor(
    private val repository: StockRepository,
    @ApplicationScope private val appScope: CoroutineScope
) {
    fun preheat(count: Int) {
        appScope.launch {
            val watchlist = repository.getWatchlist()
            val keys = watchlist.subList(0, min(watchlist.size ,count)).map { it.stockKey }
            repository.updateCache(keys)
        }
    }
}