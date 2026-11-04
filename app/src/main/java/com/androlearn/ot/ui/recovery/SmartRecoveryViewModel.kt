package com.androlearn.ot.ui.recovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androlearn.ot.data.repository.RecoveryRepository
import com.androlearn.ot.domain.recovery.RecoveryResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface RecoveryUiState {
    data object Idle : RecoveryUiState
    data object Loading : RecoveryUiState
    data class Success(val result: RecoveryResult) : RecoveryUiState
    data class Error(val message: String) : RecoveryUiState
}

@HiltViewModel
class SmartRecoveryViewModel @Inject constructor(
    private val recoveryRepository: RecoveryRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<RecoveryUiState>(RecoveryUiState.Idle)

    val uiState: StateFlow<RecoveryUiState> = _uiState.asStateFlow()

    fun generatePlan(
        date: String,
        day: String,
        nowMinute: Int,
        nowMillis: Long
    ) {
        viewModelScope.launch {
            _uiState.value = RecoveryUiState.Loading

            try {
                val result = recoveryRepository.generateRecoveryPlan(
                    date = date,
                    day = day,
                    nowMinute = nowMinute,
                    nowMillis = nowMillis
                )

                _uiState.value = RecoveryUiState.Success(result)

            } catch (exception: Exception) {
                _uiState.value = RecoveryUiState.Error(
                    exception.message ?: "ساخت برنامه جدید ناموفق بود."
                )
            }
        }
    }
}