package com.example.ledger.data.sms

import android.content.Context
import android.database.Cursor
import android.provider.Telephony
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import javax.inject.Inject

data class InboxMessage(val sender: String, val body: String, val receivedAt: Instant)

/**
 * Reads historical SMS from the system content provider for the date-based import flow.
 * Only reads — never modifies or deletes device SMS.
 */
class SmsReader @Inject constructor(@ApplicationContext private val context: Context) {

    fun readSince(sinceEpochMillis: Long): List<InboxMessage> {
        val messages = mutableListOf<InboxMessage>()
        val projection = arrayOf(Telephony.Sms.ADDRESS, Telephony.Sms.BODY, Telephony.Sms.DATE)
        val selection = "${Telephony.Sms.DATE} >= ?"
        val selectionArgs = arrayOf(sinceEpochMillis.toString())

        val cursor: Cursor? = context.contentResolver.query(
            Telephony.Sms.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            "${Telephony.Sms.DATE} ASC",
        )

        cursor?.use {
            val addressIdx = it.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)
            val bodyIdx = it.getColumnIndexOrThrow(Telephony.Sms.BODY)
            val dateIdx = it.getColumnIndexOrThrow(Telephony.Sms.DATE)
            while (it.moveToNext()) {
                val sender = it.getString(addressIdx) ?: continue
                val body = it.getString(bodyIdx) ?: continue
                val date = it.getLong(dateIdx)
                messages += InboxMessage(sender, body, Instant.ofEpochMilli(date))
            }
        }
        return messages
    }
}
