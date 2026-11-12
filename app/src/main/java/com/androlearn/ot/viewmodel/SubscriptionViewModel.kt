
package com.androlearn.ot.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androlearn.ot.data.billing.ProPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val proPreferences: ProPreferences
) : ViewModel() {

    val isPro: StateFlow<Boolean> =
        proPreferences.isPro.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    fun setProStatus(isPro: Boolean) {
        viewModelScope.launch {
            proPreferences.setPro(isPro)
        }
    }
}
