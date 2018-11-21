package com.pixelized.ordiscord.util

class Observable<T>(value: T) {
    private var observers = ArrayList<(T) -> Unit>()

    var value: T = value
        set(value) {
            field = value
            observers.forEach { it(field) }
        }

    fun observe(observer: (T) -> Unit) {
        observers.add(observer)
        observer(value)
    }
}