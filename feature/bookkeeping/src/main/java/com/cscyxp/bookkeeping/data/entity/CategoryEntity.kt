package com.cscyxp.bookkeeping.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity (
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "parent_id") val parentId: Long? = null,
    val name: String,
    val type: Int,
    val icon: String,
)
