package com.cscyxp.buer

import com.cscyxp.buer.db.AppDataBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

object CategoryRepository {
    private val dao = AppDataBase.instance.categoryDao()

    suspend fun getAllCategories(): List<Category> {
        return withContext(Dispatchers.IO) {
            dao.getAllCategories().map { it.toCategory() }
        }
    }

    suspend fun getTopCategories(): List<Category> {
        return withContext(Dispatchers.IO) {
            dao.getTopCategories().map {
                it.toCategory()
            }
        }
    }

    fun getTopCategories(type: Int): Flow<List<Category>> {
            return dao.getTopCategories(type).map { list ->
                list.map {it.toCategory()}
            }
    }

    suspend fun getSonCategories(parentId: Long): List<Category> {
        return withContext(Dispatchers.IO) {
            dao.getSonCategories(parentId).map { it.toCategory() }
        }
    }
}