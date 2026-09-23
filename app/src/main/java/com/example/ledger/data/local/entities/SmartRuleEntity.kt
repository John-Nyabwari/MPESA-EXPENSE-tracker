package com.example.ledger.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "smart_rules")
data class SmartRuleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val matchField: String,
    val pattern: String,
    val isRegex: Boolean,
    val caseInsensitive: Boolean,
    val categoryId: Long?,
    val assignedType: String?,
    val assignedSource: String?,
    val priority: Int,
    val enabled: Boolean,
)
