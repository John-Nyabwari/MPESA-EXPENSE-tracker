package com.example.ledger.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ledger.data.sms.SmsPermissionManager
import com.example.ledger.domain.repository.TransactionRepository
import com.example.ledger.security.EncryptedSettingsStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val settingsStore: EncryptedSettingsStore,
    private val smsPermissionManager: SmsPermissionManager,
) : ViewModel() {

    fun isSmsAuthorized(): Boolean = smsPermissionManager.isAuthorized()

    var appLockEnabled: Boolean
        get() = settingsStore.appLockEnabled
        set(value) { settingsStore.appLockEnabled = value }

    // README Security & Privacy: "Provide a clear delete-all-data action."
    fun deleteAllData(onComplete: () -> Unit) {
        viewModelScope.launch {
            transactionRepository.deleteAllData()
            onComplete()
        }
    }
}
