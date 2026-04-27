package com.dev.makautmate.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.dev.makautmate.data.local.SecurityHelper
import com.dev.makautmate.domain.repository.PortalRepository
import com.dev.makautmate.util.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class PortalSyncWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: PortalRepository,
    private val securityHelper: SecurityHelper
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val roll = securityHelper.getRoll()
        val pass = securityHelper.getPass()

        if (roll == null || pass == null) return Result.failure()

        return try {
            repository.syncProfile(roll, pass).fold(
                onSuccess = {
                    NotificationHelper.showNotification(
                        appContext,
                        "Portal Updated",
                        "Your academic data has been successfully synced."
                    )
                    Result.success()
                },
                onFailure = { Result.retry() }
            )
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "PortalSyncWork"

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncRequest = PeriodicWorkRequestBuilder<PortalSyncWorker>(
                6, TimeUnit.HOURS
            ).setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 15, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                syncRequest
            )
        }
    }
}
