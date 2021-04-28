package com.timmytruong.materialintervaltimer.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class Store<out T>(private val item: T) {

    private val store = MutableSharedFlow<T>()
    val observe: Flow<@JvmSuppressWildcards T> = store

    fun get() = item

    suspend fun update(change: (T) -> Unit) {
        change.invoke(item)
        forceEmit()
    }

    suspend fun forceEmit() = store.emit(item)
}