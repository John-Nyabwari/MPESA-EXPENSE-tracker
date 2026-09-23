package com.example.ledger.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Raw SMS is retained only as long as `raw_sms_retention_days` setting allows (see DataStore
 * settings + SmsRetentionWorker). Body is stored so failed parses can be retried after a
 * parser update, and so the Unreviewed queue can show the user the original text.
 */
@Entity(
    tableName = "raw_sms",
    indices = [Index(value = ["sender", "receivedAtEpochMillis"])],
)
data class RawSmsEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sender: String,
    val body: String,
    val receivedAtEpochMillis: Long,
    val parseStatus: String, // PARSED, FAILED, IGNORED
    val matchedParser: String?,
)
