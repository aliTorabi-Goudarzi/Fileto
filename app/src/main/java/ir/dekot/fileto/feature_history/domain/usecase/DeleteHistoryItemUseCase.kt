package ir.dekot.fileto.feature_history.domain.usecase

import ir.dekot.fileto.feature_history.domain.repository.HistoryRepository
import javax.inject.Inject

class DeleteHistoryItemUseCase @Inject constructor(
    private val repository: HistoryRepository
) {
    suspend operator fun invoke(id: Int) {
        repository.deleteHistoryItem(id)
    }
}