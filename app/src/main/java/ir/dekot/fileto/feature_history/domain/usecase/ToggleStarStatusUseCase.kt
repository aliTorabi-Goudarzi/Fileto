package ir.dekot.fileto.feature_history.domain.usecase

import ir.dekot.fileto.feature_history.domain.repository.HistoryRepository
import javax.inject.Inject

class ToggleStarStatusUseCase @Inject constructor(
    private val repository: HistoryRepository
) {
    suspend operator fun invoke(id: Int, isStarred: Boolean) {
        repository.toggleStarStatus(id, isStarred)
    }
}