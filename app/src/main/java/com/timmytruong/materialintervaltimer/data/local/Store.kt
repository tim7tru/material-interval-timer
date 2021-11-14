package com.timmytruong.materialintervaltimer.data.local

import com.timmytruong.materialintervaltimer.utils.Open
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

@Open
class Store<T>(private val item: T) {

    private val store = MutableSharedFlow<T>(replay = 3, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val observe: Flow<T> = store

    fun get() = item

    suspend fun update(change: (T) -> Unit) {
        change.invoke(item)
        refresh()
    }

    suspend fun refresh() = store.emit(item)
}