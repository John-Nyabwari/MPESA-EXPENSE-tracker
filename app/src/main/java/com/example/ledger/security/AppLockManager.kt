package com.example.ledger.security

import android.content.Context
import androidx.biometric.BiometricManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gate for the app-lock requirement in the README's Security & Privacy section.
 * The actual BiometricPrompt UI flow lives in the activity layer; this class only
 * reports capability and the user's lock preference (stored in EncryptedSettingsStore).
 */
@Singleton
class AppLockManager @Inject constructor(@ApplicationContext private val context: Context) {
    fun canUseBiometrics(): Boolean {
        val manager = BiometricManager.from(context)
        return manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) ==
            BiometricManager.BIOMETRIC_SUCCESS
    }
}
