package com.cscyxp.buer

import com.cscyxp.buer.db.entity.CategoryEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object RawUtil {
    suspend fun loadCategoriesFromRaw(): List<CategoryEntity> {
        return withContext(Dispatchers.IO) {
            val json = MyApp.appContext.resources.openRawResource(R.raw.categories)
                .bufferedReader()
                .use { it.readText() }

            val type = object : TypeToken<List<CategoryEntity>>() {}.type
            val fromJson = Gson().fromJson<List<CategoryEntity>>(json, type)
            fromJson
        }
    }
}