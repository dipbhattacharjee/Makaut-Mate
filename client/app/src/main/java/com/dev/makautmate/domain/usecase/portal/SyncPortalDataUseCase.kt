package com.dev.makautmate.domain.usecase.portal

import com.dev.makautmate.data.local.SecurityHelper
import com.dev.makautmate.domain.repository.PortalRepository
import javax.inject.Inject

class SyncPortalDataUseCase @Inject constructor(
    private val repository: PortalRepository,
    private val securityHelper: SecurityHelper
) {
    suspend operator fun invoke(): Result<Unit> {
        val roll = securityHelper.getRoll()
        val pass = securityHelper.getPass()

        return if (roll != null && pass != null) {
            repository.syncProfile(roll, pass)
        } else {
            Result.failure(Exception("Credentials not found"))
        }
    }
}
