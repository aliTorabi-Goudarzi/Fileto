package ir.dekot.fileto.feature_history.presentation.viewmodel

import android.content.Intent
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.dekot.fileto.feature_history.domain.usecase.DeleteHistoryItemUseCase
// UseCase فرمت‌بندی حذف شد
// import ir.dekot.fileto.feature_history.domain.usecase.FormatFileSizeUseCase
import ir.dekot.fileto.feature_history.domain.usecase.GetHistoryUseCase
import ir.dekot.fileto.feature_history.domain.usecase.ToggleStarStatusUseCase
import ir.dekot.fileto.feature_history.presentation.event.HistoryEvent
import ir.dekot.fileto.feature_history.presentation.event.UserEvent
// Mapper جدید ایمپورت شد
import ir.dekot.fileto.feature_history.presentation.mapper.HistoryUiMapper
import ir.dekot.fileto.feature_history.presentation.state.HistoryScreenState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
// ایمپورت‌های مربوط به فرمت‌بندی حذف شدند
// import java.text.SimpleDateFormat
// import java.util.Date
// import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    getHistoryUseCase: GetHistoryUseCase,
    // وابستگی به UseCase حذف و به Mapper جدید جایگزین شد
    private val historyUiMapper: HistoryUiMapper,
    private val toggleStarStatusUseCase: ToggleStarStatusUseCase,
    private val deleteHistoryItemUseCase: DeleteHistoryItemUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryScreenState())
    val uiState = _uiState.asStateFlow()

    private val _eventChannel = Channel<HistoryEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        getHistoryUseCase().onEach { historyItems ->
            _uiState.update {
                it.copy(
                    isLoading = false,
                    // <<-- تغییر اصلی اینجاست -->>
                    // تمام منطق تبدیل به Mapper واگذار شد
                    historyItems = historyItems.map(historyUiMapper::toUiItem)
                )
            }
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: UserEvent) {
        viewModelScope.launch {
            when (event) {
                is UserEvent.OpenFile -> {
                    val openIntent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(event.uriString.toUri(), "application/pdf")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    _eventChannel.send(HistoryEvent.Navigate(openIntent))
                }
                is UserEvent.ToggleStar -> {
                    toggleStarStatusUseCase(event.id, !event.isStarred)
                }
                is UserEvent.DeleteItem -> {
                    deleteHistoryItemUseCase(event.id)
                }
            }
        }
    }
}