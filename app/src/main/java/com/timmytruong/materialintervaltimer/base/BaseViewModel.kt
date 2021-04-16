package com.timmytruong.materialintervaltimer.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

abstract class BaseViewModel : ViewModel() {

    abstract val mainDispatcher: CoroutineDispatcher

    private val _navigateFlow = MutableSharedFlow<NavDirections>()
    val navigateFlow: Flow<NavDirections> = _navigateFlow

    private val _eventFlow = MutableSharedFlow<Pair<String, Any>>()
    val eventFlow: SharedFlow<Pair<String, Any>> = _eventFlow

    protected fun fireEvent(key: String, value: Any = Unit) = startSuspending(mainDispatcher) {
        _eventFlow.emit(Pair(key, value))
    }

    protected fun navigateWith(action: NavDirections) = startSuspending(mainDispatcher) {
        _navigateFlow.emit(action)
    }

    protected fun ViewModel.startSuspending(
        context: CoroutineContext = EmptyCoroutineContext,
        block: suspend (CoroutineScope) -> Unit
    ) { viewModelScope.launch(context, CoroutineStart.DEFAULT, block) }
}