package com.cscyxp.bookkeeping.util

import android.content.Context
import com.cscyxp.bookkeeping.data.entity.CategoryEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object RawUtil {
    suspend fun loadCategoriesFromRaw(context: Context): List<CategoryEntity> {
        return withContext(Dispatchers.IO) {
            val json = context.resources.openRawResource(com.cscyxp.bookkeeping.R.raw.categories)
                .bufferedReader().use { it.readText() }

            val type = object : TypeToken<List<CategoryEntity>>() {}.type
            Gson().fromJson(json, type)
        }
    }
}
