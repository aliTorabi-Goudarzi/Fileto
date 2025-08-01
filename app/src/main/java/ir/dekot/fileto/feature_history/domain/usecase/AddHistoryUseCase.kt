package ir.dekot.fileto.feature_history.domain.usecase

import ir.dekot.fileto.feature_history.domain.model.HistoryItem
import ir.dekot.fileto.feature_history.domain.repository.HistoryRepository
import javax.inject.Inject

class AddHistoryUseCase @Inject constructor(
    private val repository: HistoryRepository
) {
    suspend operator fun invoke(item: HistoryItem) {
        repository.addHistoryItem(item)
    }
}