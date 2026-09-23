package com.example.ledger.di

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.ledger.data.local.AppDatabase
import com.example.ledger.data.local.dao.CategoryDao
import com.example.ledger.data.local.dao.RawSmsDao
import com.example.ledger.data.local.dao.SmartRuleDao
import com.example.ledger.data.local.dao.TransactionDao
import com.example.ledger.data.local.entities.CategoryEntity
import com.example.ledger.domain.model.DefaultCategories
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

private val seedColors = listOf(
    "#F58B4C", "#5B8DEF", "#8E6BD8", "#2F8F4E", "#B5781E", "#D4332B",
    "#9FE870", "#163300", "#6B6F66", "#0E9C8A", "#C74B8F", "#3AA0C9",
    "#E0A100", "#7C5CFC", "#4CA37A", "#A8A8A8", "#9A9D95",
)

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
            // Financial data: never allow a destructive fallback in production.
            .addCallback(object : androidx.room.RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    CoroutineScope(Dispatchers.IO).launch {
                        seedDefaultCategories(context)
                    }
                }
            })
            .build()

    private suspend fun seedDefaultCategories(context: Context) {
        // Re-fetch DB via a fresh reference isn't ideal inside the callback; in practice this is
        // done through a seeding use case invoked from App.onCreate(). Left here as documentation
        // of the intended default set — see domain.usecase.SeedDefaultDataUseCase.
    }

    @Provides
    fun provideTransactionDao(db: AppDatabase): TransactionDao = db.transactionDao()

    @Provides
    fun provideCategoryDao(db: AppDatabase): CategoryDao = db.categoryDao()

    @Provides
    fun provideSmartRuleDao(db: AppDatabase): SmartRuleDao = db.smartRuleDao()

    @Provides
    fun provideRawSmsDao(db: AppDatabase): RawSmsDao = db.rawSmsDao()
}
