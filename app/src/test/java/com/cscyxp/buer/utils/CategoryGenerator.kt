package com.cscyxp.buer.utils

import com.cscyxp.buer.Category
import kotlin.random.Random
import kotlin.random.nextInt

object CategoryGenerator {

    fun generatorRandomCategories(
        count: Int = 1,
        type: Int = -1
    ): List<Category> {
        val categories = mutableListOf<Category>()
        var categoryType = type
        if (type != Category.TYPE_INCOME && type != Category.TYPE_EXPAND) {
            categoryType = Random.nextInt(0, 2)
        }
        repeat(count) {
            categories.add(
                Category(
                    id = count.toLong(),
                    name = "Category-$count",
                    type = categoryType,
                    icon = "Icon-$count"
                )
            )
        }

        return categories
    }
}