package com.example.ledger.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Keystore-backed storage for small sensitive settings (app-lock enabled, raw-SMS retention
 * days, historical-import cutoff date). Transaction data itself lives in Room; SQLCipher-based
 * full DB encryption is a follow-up (see README Security & Privacy: "Encrypt sensitive local
 * data using Android Keystore-backed keys") once the schema stabilizes past the MVP.
 */
@Singleton
class EncryptedSettingsStore @Inject constructor(@ApplicationContext context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "secure_settings",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    var appLockEnabled: Boolean
        get() = prefs.getBoolean(KEY_APP_LOCK, false)
        set(value) = prefs.edit().putBoolean(KEY_APP_LOCK, value).apply()

    var rawSmsRetentionDays: Int
        get() = prefs.getInt(KEY_RETENTION_DAYS, 90)
        set(value) = prefs.edit().putInt(KEY_RETENTION_DAYS, value).apply()

    companion object {
        private const val KEY_APP_LOCK = "app_lock_enabled"
        private const val KEY_RETENTION_DAYS = "raw_sms_retention_days"
    }
}
