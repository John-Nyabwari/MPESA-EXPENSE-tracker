package com.example.ledger.presentation.smart_rules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ledger.data.local.dao.SmartRuleDao
import com.example.ledger.data.local.entities.SmartRuleEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SmartRulesViewModel @Inject constructor(
    private val smartRuleDao: SmartRuleDao,
) : ViewModel() {

    val rules: StateFlow<List<SmartRuleEntity>> =
        smartRuleDao.observeAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleEnabled(rule: SmartRuleEntity) {
        viewModelScope.launch { smartRuleDao.update(rule.copy(enabled = !rule.enabled)) }
    }

    fun delete(rule: SmartRuleEntity) {
        viewModelScope.launch { smartRuleDao.delete(rule) }
    }
}
