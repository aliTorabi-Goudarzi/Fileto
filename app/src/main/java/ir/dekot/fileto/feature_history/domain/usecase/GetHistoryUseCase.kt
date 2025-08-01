package ir.dekot.fileto.feature_history.domain.usecase

import ir.dekot.fileto.feature_history.domain.model.HistoryItem
import ir.dekot.fileto.feature_history.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHistoryUseCase @Inject constructor(
    private val repository: HistoryRepository
) {
    operator fun invoke(): Flow<List<HistoryItem>> {
        return repository.getHistory()
    }
}