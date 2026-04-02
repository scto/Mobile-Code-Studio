package com.scto.mcs.domain.usecase

import com.scto.mcs.core.LogcatManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBuildLogsUseCase @Inject constructor(private val logcatManager: LogcatManager) {
    operator fun invoke(): Flow<String> = logcatManager.output
}
