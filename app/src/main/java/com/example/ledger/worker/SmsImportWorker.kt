package com.example.ledger.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.ledger.data.sms.SmsReader
import com.example.ledger.domain.usecase.ImportSmsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.Instant

/**
 * Handles both:
 *  - MODE_SINGLE: one freshly-received SMS (from SmsReceiver).
 *  - MODE_HISTORICAL: the onboarding date-based bulk import (see README "Date-based history
 *    import" flow) — runs off the main thread and reports progress via WorkManager progress data.
 */
@HiltWorker
class SmsImportWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val importSmsUseCase: ImportSmsUseCase,
    private val smsReader: SmsReader,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return when (inputData.getString(KEY_MODE)) {
            MODE_SINGLE -> runSingle()
            MODE_HISTORICAL -> runHistorical()
            else -> Result.failure()
        }
    }

    private suspend fun runSingle(): Result {
        val sender = inputData.getString(KEY_SENDER) ?: return Result.failure()
        val body = inputData.getString(KEY_BODY) ?: return Result.failure()
        val timestamp = inputData.getLong(KEY_TIMESTAMP, System.currentTimeMillis())
        importSmsUseCase.importOne(sender, body, Instant.ofEpochMilli(timestamp))
        return Result.success()
    }

    private suspend fun runHistorical(): Result {
        val sinceMillis = inputData.getLong(KEY_SINCE_MILLIS, 0L)
        val messages = smsReader.readSince(sinceMillis)
        val total = messages.size
        var processed = 0

        for (message in messages) {
            importSmsUseCase.importOne(message.sender, message.body, message.receivedAt)
            processed++
            setProgressAsync(
                androidx.work.Data.Builder()
                    .putInt(PROGRESS_PROCESSED, processed)
                    .putInt(PROGRESS_TOTAL, total)
                    .build(),
            )
        }
        return Result.success()
    }

    companion object {
        const val KEY_MODE = "mode"
        const val MODE_SINGLE = "single"
        const val MODE_HISTORICAL = "historical"

        const val KEY_SENDER = "sender"
        const val KEY_BODY = "body"
        const val KEY_TIMESTAMP = "timestamp"
        const val KEY_SINCE_MILLIS = "since_millis"

        const val PROGRESS_PROCESSED = "processed"
        const val PROGRESS_TOTAL = "total"
    }
}
