package com.example.ledger.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ledger.data.local.dao.CategoryDao
import com.example.ledger.data.local.dao.RawSmsDao
import com.example.ledger.data.local.dao.SmartRuleDao
import com.example.ledger.data.local.dao.TransactionDao
import com.example.ledger.data.local.entities.CategoryEntity
import com.example.ledger.data.local.entities.RawSmsEntity
import com.example.ledger.data.local.entities.SmartRuleEntity
import com.example.ledger.data.local.entities.TransactionEntity

@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        SmartRuleEntity::class,
        RawSmsEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun smartRuleDao(): SmartRuleDao
    abstract fun rawSmsDao(): RawSmsDao

    companion object {
        const val DATABASE_NAME = "ledger.db"
        // Future schema changes go here as Migration(1, 2) objects, added to
        // DatabaseModule's Room.databaseBuilder(...).addMigrations(...).
    }
}
