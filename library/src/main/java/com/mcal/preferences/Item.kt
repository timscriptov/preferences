package com.mcal.preferences

class Item<T>(
    var key: String,
    var value: T
) {
    override fun toString(): String {
        return "$key : $value"
    }
}
