package com.pixelized.ordiscord.util

class Observable<T>(value: T) {
    private var observers = ArrayList<(T) -> Unit>()

    var value: T = value
        set(value) {
            field = value
            observers.forEach { it(field) }
        }

    fun observe(observer: (T) -> Unit): Observer {
        observers.add(observer)
        observer(value)
        return Observer(observer)
    }

    inner class Observer(lambda: (T) -> Unit) {
        private var lambda: ((T) -> Unit)? = lambda

        fun complete() {
            observers.remove(lambda)
            lambda = null
        }
    }
}