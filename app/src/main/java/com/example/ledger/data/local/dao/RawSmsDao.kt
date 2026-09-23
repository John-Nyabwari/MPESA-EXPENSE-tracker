package com.example.ledger.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.ledger.data.local.entities.RawSmsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RawSmsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(sms: RawSmsEntity): Long

    @Update
    suspend fun update(sms: RawSmsEntity)

    @Query("SELECT * FROM raw_sms WHERE parseStatus = 'FAILED' ORDER BY receivedAtEpochMillis DESC")
    fun observeFailed(): Flow<List<RawSmsEntity>>

    @Query("DELETE FROM raw_sms WHERE receivedAtEpochMillis < :cutoffMillis")
    suspend fun deleteOlderThan(cutoffMillis: Long)

    @Query("DELETE FROM raw_sms")
    suspend fun deleteAll()
}
