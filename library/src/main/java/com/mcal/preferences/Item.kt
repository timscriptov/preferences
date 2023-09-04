package com.mcal.preferences

class Item<T>(
    private var key: String,
    private var value: T
) {
    override fun toString(): String {
        return "$key : $value"
    }
}
