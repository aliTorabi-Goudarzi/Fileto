package ir.dekot.fileto.feature_history.presentation.viewmodel

import android.content.Intent
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.dekot.fileto.feature_compress.domain.model.CompressionSettings

import ir.dekot.fileto.feature_history.domain.usecase.DeleteHistoryItemUseCase
import ir.dekot.fileto.feature_history.domain.usecase.FormatFileSizeUseCase
import ir.dekot.fileto.feature_history.domain.usecase.GetHistoryUseCase
import ir.dekot.fileto.feature_history.domain.usecase.ToggleStarStatusUseCase
import ir.dekot.fileto.feature_history.presentation.screen.HistoryUiItem
import ir.dekot.fileto.feature_history.presentation.state.HistoryScreenState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    getHistoryUseCase: GetHistoryUseCase,
    private val formatFileSizeUseCase: FormatFileSizeUseCase,
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
                    historyItems = historyItems.map { domainItem ->
                        val reduction = if (domainItem.originalSize > 0) {
                            100 - (domainItem.compressedSize * 100 / domainItem.originalSize)
                        } else 0

                        // --- منطق پارس کردن JSON ---
                        val customSettingsList: List<Pair<String, String>>? = domainItem.customSettingsJson?.let { json ->
                            try {
                                val settings = Gson().fromJson(json, CompressionSettings::class.java)
                                buildList {
                                    add("کیفیت تصویر" to "${settings.imageQuality}%")
                                    add("حذف متادیتا" to if (settings.removeMetadata) "بله" else "خیر")
                                    add("فشرده‌سازی ساختاری" to if (settings.useObjectStreamCompression) "بله" else "خیر")
                                    add("حداکثر رزولوشن" to "${settings.downscaleResolution} DPI")
                                }
                            } catch (_: Exception) {
                                null
                            }
                        }

                        HistoryUiItem(
                            id = domainItem.id,
                            fileName = domainItem.fileName,
                            formattedDate = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(Date(domainItem.timestamp)),
                            compressionProfile = domainItem.compressionProfile,
                            customSettings = customSettingsList,
                            formattedSize = "${formatFileSizeUseCase(domainItem.originalSize)} -> ${formatFileSizeUseCase(domainItem.compressedSize)}",
                            reductionPercentage = reduction.toInt(),
                            compressedFileUri = domainItem.compressedFileUri,
                            isStarred = domainItem.isStarred
                        )
                    }
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

    sealed class UserEvent {
        data class OpenFile(val uriString: String) : UserEvent()
        data class ToggleStar(val id: Int, val isStarred: Boolean) : UserEvent()
        data class DeleteItem(val id: Int) : UserEvent()
    }

    sealed class HistoryEvent {
        data class Navigate(val intent: Intent) : HistoryEvent()
    }
}