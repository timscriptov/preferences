package com.mcal.preferences

import java.io.Serializable
import kotlin.reflect.KClass

interface Preferences {
    fun <T : Any> getArray(key: String, defaultValue: List<T> = emptyList(), elementClass: KClass<T>): List<T>
    fun <T : Any> putArray(key: String, value: List<T>, elementClass: KClass<T>)
    fun getList(key: String, defaultValue: List<String>): List<String>
    fun putList(key: String, value: List<String>)
    fun getString(key: String, defaultValue: String): String
    fun putString(key: String, value: String)
    fun getBoolean(key: String, defaultValue: Boolean): Boolean
    fun putBoolean(key: String, value: Boolean)
    fun getLong(key: String, defaultValue: Long): Long
    fun putLong(key: String, value: Long)
    fun getInt(key: String, defaultValue: Int): Int
    fun putInt(key: String, value: Int)
    fun getFloat(key: String, defaultValue: Float): Float
    fun putFloat(key: String, value: Float)
    fun getDouble(key: String, defaultValue: Double): Double
    fun putDouble(key: String, value: Double)
    fun <T> getObject(key: String, defaultValue: T): T
    fun <T : Serializable?> putObject(key: String, value: T)
}