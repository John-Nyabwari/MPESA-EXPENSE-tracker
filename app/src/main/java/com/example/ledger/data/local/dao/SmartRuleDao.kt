package com.example.ledger.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.ledger.data.local.entities.SmartRuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SmartRuleDao {
    @Insert
    suspend fun insert(rule: SmartRuleEntity): Long

    @Update
    suspend fun update(rule: SmartRuleEntity)

    @Delete
    suspend fun delete(rule: SmartRuleEntity)

    @Query("SELECT * FROM smart_rules ORDER BY priority DESC")
    fun observeAll(): Flow<List<SmartRuleEntity>>

    // Enabled rules, highest priority first — used at match time by the rule engine.
    @Query("SELECT * FROM smart_rules WHERE enabled = 1 ORDER BY priority DESC")
    suspend fun getEnabledOrderedByPriority(): List<SmartRuleEntity>
}
