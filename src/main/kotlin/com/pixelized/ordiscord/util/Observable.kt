package com.pixelized.ordiscord.util

class Observable<T>(value: T) {
    var observabers = ArrayList<(T) -> Unit>()

    var value: T = value
        set(value) {
            field = value
            observabers.forEach { it(field) }
        }

    fun observe(observer: (T) -> Unit) {
        observabers.add(observer)
        observer(value)
    }
}