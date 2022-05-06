package com.timmytruong.materialintervaltimer.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timmytruong.materialintervaltimer.utils.Event
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel : ViewModel() {

    abstract val mainDispatcher: CoroutineDispatcher

    private val _eventFlow = MutableSharedFlow<Event>()
    val eventFlow: SharedFlow<Event> = _eventFlow

    protected fun Event.fire() = startSuspending(mainDispatcher) { _eventFlow.emit(this) }

    protected fun ViewModel.startSuspending(
        context: CoroutineContext = Dispatchers.Default,
        block: suspend (CoroutineScope) -> Unit
    ) {
        viewModelScope.launch(context, CoroutineStart.DEFAULT, block)
    }
}