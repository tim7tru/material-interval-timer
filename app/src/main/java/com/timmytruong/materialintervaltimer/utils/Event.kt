package com.timmytruong.materialintervaltimer.utils

/**
 * This class is a wrapper that handles single shot events through LiveData observables
 */
open class Event<out T>(private val content: T?) {

    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): T? = content
}