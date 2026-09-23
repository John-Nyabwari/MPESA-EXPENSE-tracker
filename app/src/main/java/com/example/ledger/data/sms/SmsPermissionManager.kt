package com.example.ledger.data.sms

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Telephony
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Centralizes the SMS permission + default-handler checks called out in the README's
 * platform constraint: Play Store restricts READ_SMS/RECEIVE_SMS to apps that are either
 * the user's default SMS handler, or that qualify for the "SMS-based financial transaction
 * management" permitted use case declared in the Play Console questionnaire. This class
 * only reports state — the actual Play policy justification is a Play Console/legal task,
 * not a code task, and must be confirmed before requesting these permissions in production.
 */
@Singleton
class SmsPermissionManager @Inject constructor(@ApplicationContext private val context: Context) {

    fun hasReadSmsPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED

    fun hasReceiveSmsPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED

    fun isDefaultSmsApp(): Boolean =
        context.packageName == Telephony.Sms.getDefaultSmsPackage(context)

    /** True when the app can legally read SMS under the currently understood Play policy. */
    fun isAuthorized(): Boolean = hasReadSmsPermission() && (isDefaultSmsApp() || hasReceiveSmsPermission())
}
