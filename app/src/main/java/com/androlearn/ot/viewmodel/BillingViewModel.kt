
package com.androlearn.ot.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androlearn.ot.data.billing.MyketBillingManager
import com.androlearn.ot.data.billing.ProPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class BillingViewModel @Inject constructor(
    private val proPreferences: ProPreferences
) : ViewModel() {

    val isPro = proPreferences.isPro.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private var billingManager: MyketBillingManager? = null

    fun connect(activity: Activity) {
        if (billingManager != null) return

        billingManager = MyketBillingManager(
            activity = activity,
            proPreferences = proPreferences,
            scope = viewModelScope,
            onProStatusChanged = { },
            onError = { message ->
                _error.value = message
            }
        )

        billingManager?.connect()
    }

    fun purchasePro() {
        billingManager?.purchasePro()
            ?: run {
                _error.value = "اتصال به مایکت برقرار نشده است"
            }
    }

    fun clearError() {
        _error.value = null
    }

    override fun onCleared() {
        billingManager?.dispose()
        billingManager = null
        super.onCleared()
    }
}
