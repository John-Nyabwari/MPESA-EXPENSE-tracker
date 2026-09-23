package com.example.ledger.data.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.ledger.worker.SmsImportWorker

/**
 * Listens for new incoming SMS and hands them to WorkManager rather than parsing inline,
 * so a slow parse (or Room write) never blocks the broadcast, and retries are automatic.
 */
class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        val sender = messages.firstOrNull()?.originatingAddress ?: return
        val body = messages.joinToString(separator = "") { it.messageBody ?: "" }
        val timestamp = messages.firstOrNull()?.timestampMillis ?: System.currentTimeMillis()

        val request = OneTimeWorkRequestBuilder<SmsImportWorker>()
            .setInputData(
                workDataOf(
                    SmsImportWorker.KEY_MODE to SmsImportWorker.MODE_SINGLE,
                    SmsImportWorker.KEY_SENDER to sender,
                    SmsImportWorker.KEY_BODY to body,
                    SmsImportWorker.KEY_TIMESTAMP to timestamp,
                ),
            )
            .build()
        WorkManager.getInstance(context).enqueue(request)
    }
}
