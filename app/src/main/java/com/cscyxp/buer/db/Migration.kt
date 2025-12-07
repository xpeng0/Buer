package com.cscyxp.buer.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE categories (
                id INTEGER PRIMARY KEY NOT NULL,
                parent_id INTEGER,
                name TEXT NOT NULL,
                type INTEGER NOT NULL,
                icon TEXT NOT NULL
            )
        """.trimIndent())
    }
}