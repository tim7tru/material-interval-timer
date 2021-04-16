package com.timmytruong.materialintervaltimer.utils.events

/**
 * This class is a wrapper that handles single shot events through LiveData observables
 */
open class Event<T>(var value: T) {

    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            value
        }
    }
}