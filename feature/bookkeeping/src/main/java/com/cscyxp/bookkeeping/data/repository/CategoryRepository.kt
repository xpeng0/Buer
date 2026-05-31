package com.cscyxp.bookkeeping.data.repository

import com.cscyxp.bookkeeping.data.dao.CategoryDao
import com.cscyxp.bookkeeping.domain.Category
import com.cscyxp.bookkeeping.util.toCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CategoryRepository @Inject constructor(
    private val dao: CategoryDao
) {
    suspend fun getAllCategories(): List<Category> {
        return withContext(Dispatchers.IO) {
            dao.getAllCategories().map { it.toCategory() }
        }
    }

    suspend fun getTopCategories(): List<Category> {
        return withContext(Dispatchers.IO) {
            dao.getTopCategories().map { it.toCategory() }
        }
    }

    fun getTopCategories(type: Int): Flow<List<Category>> {
        return dao.getTopCategories(type).map { list ->
            list.map { it.toCategory() }
        }
    }

    suspend fun getSonCategories(parentId: Long): List<Category> {
        return withContext(Dispatchers.IO) {
            dao.getSonCategories(parentId).map { it.toCategory() }
        }
    }
}
