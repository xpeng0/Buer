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

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS watchlist (
                `symbol` TEXT NOT NULL,
                `exchange` TEXT NOT NULL,
                PRIMARY KEY(`symbol`, `exchange`)
            )
        """.trimIndent())
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE watchlist ADD COLUMN stockName TEXT NOT NULL DEFAULT ''")
    }
}