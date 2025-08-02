package ir.dekot.fileto.feature_history.presentation.event

sealed class UserEvent {
    data class OpenFile(val uriString: String) : UserEvent()
    data class ToggleStar(val id: Int, val isStarred: Boolean) : UserEvent()
    data class DeleteItem(val id: Int) : UserEvent()
}
