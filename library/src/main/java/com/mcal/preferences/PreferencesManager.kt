package com.mcal.preferences

import com.mcal.preferences.utils.Extensions.formatJson
import com.mcal.preferences.utils.Extensions.tryDeserialize
import com.mcal.preferences.utils.FileHelper
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.*
import kotlinx.serialization.serializer
import java.io.*
import java.util.*
import kotlin.reflect.KClass

open class PreferencesManager(
    private val dir: File,
    private val name: String
) : Preferences {
    companion object {
        private const val INTEGERS = "integers"
        private const val STRINGS = "strings"
        private const val BOOLEANS = "booleans"
        private const val FLOATS = "floats"
        private const val DOUBLES = "doubles"
        private const val LONGS = "longs"
        private const val OBJECTS = "objects"
        private const val ARRAYS = "arrays"
    }

    private var mJsonObject = buildJsonObject { }
    private var mIntegers: JsonObject? = null
    private var mStrings: JsonObject? = null
    private var mBooleans: JsonObject? = null
    private var mFloats: JsonObject? = null
    private var mDoubles: JsonObject? = null
    private var mLongs: JsonObject? = null
    private var mObjects: JsonObject? = null
    private var mArrays: JsonObject? = null

    /**
     * Read content from data file
     */
    init {
        if (!dir.exists() && !dir.mkdirs()) {
            println("Cannot create dir")
        }
        val prefFile = File(dir, name)
        if (!prefFile.exists()) {
            if (!prefFile.createNewFile()) {
                println("Cannot create memory file")
            }
        }
        loadJSON(prefFile)
        checkJson()
    }

    /**
     * Load data file as JSON
     */
    @Throws(IOException::class)
    private fun loadJSON(file: File) {
        mJsonObject = runCatching {
            val content = FileHelper.readText(file)
            Json.parseToJsonElement(content) as JsonObject
        }.getOrElse {
            buildJsonObject { }
        }
    }

    /**
     * Check json keys
     */
    private fun checkJson() {
        mIntegers = mIntegers ?: ifNotExist(INTEGERS)
        mStrings = mStrings ?: ifNotExist(STRINGS)
        mBooleans = mBooleans ?: ifNotExist(BOOLEANS)
        mFloats = mFloats ?: ifNotExist(FLOATS)
        mDoubles = mDoubles ?: ifNotExist(DOUBLES)
        mLongs = mLongs ?: ifNotExist(LONGS)
        mObjects = mObjects ?: ifNotExist(OBJECTS)
        mArrays = mArrays ?: ifNotExist(ARRAYS)
    }

    /**
     * Create JSON Object if not exist
     */
    private fun ifNotExist(key: String): JsonObject? {
        val json = mJsonObject
        if (!json.containsKey(key)) {
            mJsonObject = buildJsonObject {
                json.forEach { (k, v) ->
                    put(k, v)
                }
                put(key, buildJsonObject { })
            }
        }
        return mJsonObject[key]?.jsonObject
    }

    /**
     * Write each change on the file
     */
    private fun updateMemory() {
        runBlocking {
            val prefFile = File(dir, name)
            val jsonString = mJsonObject.toString().formatJson()
            FileHelper.writeTextAsync(prefFile, jsonString)
        }
    }

    private fun updateJson(key: String, newObject: JsonObject) {
        val json = mJsonObject
        val updatedJson = buildJsonObject {
            json.forEach { (k, v) ->
                if (k == key) {
                    put(k, newObject)
                } else {
                    put(k, v)
                }
            }
            if (!json.containsKey(key)) {
                put(key, newObject)
            }
        }
        mJsonObject = updatedJson
        updateMemory()
    }

    /**
     * Remove all values with same key
     *
     * @param key of values to remove
     */
    fun remove(key: String) {
        mIntegers = removeValue(mIntegers, key)
        mStrings = removeValue(mStrings, key)
        mFloats = removeValue(mFloats, key)
        mDoubles = removeValue(mDoubles, key)
        mBooleans = removeValue(mBooleans, key)
        mObjects = removeValue(mObjects, key)
        mArrays = removeValue(mArrays, key)
        updateMemory()
    }

    /**
     * Remove value with that key and class (to prevent multiple values deletions)
     *
     * @param key of value
     * @param c   class of value
     */
    fun remove(key: String, c: Class<Any>) {
        when (c) {
            Int::class.java -> {
                mIntegers = removeValue(mIntegers, key)
            }

            Float::class.java -> {
                mFloats = removeValue(mFloats, key)
            }

            Boolean::class.java -> {
                mBooleans = removeValue(mBooleans, key)
            }

            String::class.java -> {
                mStrings = removeValue(mStrings, key)
            }

            Double::class.java -> {
                mDoubles = removeValue(mDoubles, key)
            }

            Long::class.java -> {
                mLongs = removeValue(mLongs, key)
            }

            List::class.java -> {
                mArrays = removeValue(mArrays, key)
            }

            else -> {
                mObjects = removeValue(mObjects, key)
            }
        }
        updateMemory()
    }

    private fun removeValue(json: JsonObject?, key: String): JsonObject? {
        return if (json != null && json.containsKey(key)) {
            buildJsonObject {
                json.filterKeys { it != key }.forEach { (k, v) ->
                    put(k, v)
                }
            }.also {
                mJsonObject = it
            }
        } else {
            json
        }
    }

    /**
     * Iterate a kind of values (String, Boolean , Integer ..)
     */
    fun <T> iterator(c: Class<Any?>): MutableIterator<Item<T>?>? {
        val obj: JsonObject?
        when (c) {
            Int::class.java -> {
                obj = mIntegers
            }

            Float::class.java -> {
                obj = mFloats
            }

            Boolean::class.java -> {
                obj = mBooleans
            }

            String::class.java -> {
                obj = mStrings
            }

            Double::class.java -> {
                obj = mDoubles
            }

            List::class.java -> {
                obj = mArrays
            }

            else -> {
                obj = mObjects
            }
        }
        if (obj != null) {
            val keysIterator = obj.keys.iterator()
            return object : MutableIterator<Item<T>?> {
                override fun hasNext(): Boolean {
                    return keysIterator.hasNext()
                }

                @Suppress("UNCHECKED_CAST")
                override fun next(): Item<T> {
                    val key = keysIterator.next()
                    return Item(key, obj[key] as T)
                }

                override fun remove() {
                    throw UnsupportedOperationException()
                }
            }
        }
        return null
    }

    /**
     * Get the value of a key as an array
     *
     * @param key the key of the array
     * @param defaultValue the default value if the array doesn't exist or has a different type
     * @param T the type of the array elements
     * @return the array value or the default value
     */
    override fun <T : Any> getArray(key: String, defaultValue: List<T>, elementClass: KClass<T>): List<T> {
        val array = mArrays?.get(key)?.jsonArray
        return if (array != null) {
            try {
                array.mapNotNull { element ->
                    element.jsonPrimitive.tryDeserialize(elementClass)
                }
            } catch (e: Exception) {
                defaultValue
            }
        } else {
            defaultValue
        }
    }

    /**
     * Put an array value in the preferences file.
     *
     * @param key the key of the array
     * @param value the array value
     * @param elementClass the class of the array elements
     */
    @OptIn(InternalSerializationApi::class)
    override fun <T : Any> putArray(key: String, value: List<T>, elementClass: KClass<T>) {
        val newArray = buildJsonObject {
            if (mArrays != null) {
                mArrays?.forEach { (k, v) ->
                    put(k, v)
                }
            } else {
                mArrays = buildJsonObject {}
            }
            put(key, buildJsonArray {
                value.forEach {
                    add(Json.encodeToJsonElement(elementClass.serializer(), it))
                }
            })
        }
        mArrays = newArray.also { objects ->
            updateJson(ARRAYS, objects)
        }
        updateMemory()
    }

    /**
     * Get the value of a key as an array
     *
     * @param key the key of the array
     * @param defaultValue the default value if the array doesn't exist or has a different type
     * @return the array value or the default value
     */
    override fun getList(key: String, defaultValue: List<String>): List<String> {
        val array = mArrays?.get(key)?.jsonArray
        return if (array != null) {
            try {
                array.mapNotNull { element ->
                    element.jsonPrimitive.tryDeserialize(String::class)
                }
            } catch (e: Exception) {
                defaultValue
            }
        } else {
            defaultValue
        }
    }

    /**
     * Put an array value in the preferences file.
     *
     * @param key the key of the array
     * @param value the array value
     */
    @OptIn(InternalSerializationApi::class)
    override fun putList(key: String, value: List<String>) {
        val newArray = buildJsonObject {
            if (mArrays != null) {
                mArrays?.forEach { (k, v) ->
                    put(k, v)
                }
            } else {
                mArrays = buildJsonObject {}
            }
            put(key, buildJsonArray {
                value.forEach {
                    add(Json.encodeToJsonElement(String::class.serializer(), it))
                }
            })
        }
        mArrays = newArray.also { objects ->
            updateJson(ARRAYS, objects)
        }
        updateMemory()
    }

    /**
     * Get String Value
     *
     * @param key          of value
     * @param defaultValue if absent
     */
    override fun getString(key: String, defaultValue: String): String {
        return mStrings?.get(key)?.jsonPrimitive?.content ?: defaultValue
    }

    /**
     * Save String Value
     *
     * @param key   of value
     * @param value to store
     */
    override fun putString(key: String, value: String) {
        mStrings = putValue(mStrings, key, value).also {
            updateJson(STRINGS, it)
        }
    }

    /**
     * Get Boolean Value
     *
     * @param key          of value
     * @param defaultValue if absent
     */
    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return mBooleans?.get(key)?.jsonPrimitive?.boolean ?: defaultValue
    }

    /**
     * Save Boolean Value
     *
     * @param key   of value
     * @param value to store
     */
    override fun putBoolean(key: String, value: Boolean) {
        mBooleans = putValue(mBooleans, key, value).also {
            updateJson(BOOLEANS, it)
        }
    }

    /**
     * Get Long Value
     *
     * @param key          of value
     * @param defaultValue if absent
     */
    override fun getLong(key: String, defaultValue: Long): Long {
        return mLongs?.get(key)?.jsonPrimitive?.long ?: defaultValue
    }

    /**
     * Save Long Value
     *
     * @param key   of value
     * @param value to store
     */
    override fun putLong(key: String, value: Long) {
        mLongs = putValue(mLongs, key, value).also {
            updateJson(LONGS, it)
        }
    }

    /**
     * Get String Value
     *
     * @param key          of value
     * @param defaultValue if absent
     */
    override fun getInt(key: String, defaultValue: Int): Int {
        return mIntegers?.get(key)?.jsonPrimitive?.int ?: defaultValue
    }

    /**
     * Save Integer Value
     *
     * @param key   of value
     * @param value to store
     */
    override fun putInt(key: String, value: Int) {
        mIntegers = putValue(mIntegers, key, value).also {
            updateJson(INTEGERS, it)
        }
    }

    /**
     * Get Float Value
     *
     * @param key          of value
     * @param defaultValue if absent
     */
    override fun getFloat(key: String, defaultValue: Float): Float {
        return mFloats?.get(key)?.jsonPrimitive?.float ?: defaultValue
    }

    /**
     * Save Float Value
     *
     * @param key   of value
     * @param value to store
     */
    override fun putFloat(key: String, value: Float) {
        mFloats = putValue(mFloats, key, value).also {
            updateJson(FLOATS, it)
        }
    }

    /**
     * Get Double Value
     *
     * @param key          of value
     * @param defaultValue if absent
     */
    override fun getDouble(key: String, defaultValue: Double): Double {
        return mDoubles?.get(key)?.jsonPrimitive?.double ?: defaultValue
    }

    /**
     * Save Double Value
     *
     * @param key   of value
     * @param value to store
     */
    override fun putDouble(key: String, value: Double) {
        mDoubles = putValue(mDoubles, key, value).also {
            updateJson(DOUBLES, it)
        }
    }

    /**
     * Get Object Value
     *
     * @param key          of value
     * @param defaultValue if absent
     */
    override fun <T> getObject(key: String, defaultValue: T): T {
        var result: T? = null
        try {
            val objects = mObjects
            if (objects != null) {
                val filePath = objects[key]?.jsonPrimitive?.content
                if (filePath != null) {
                    ObjectInputStream(FileInputStream(filePath)).use {
                        result = it.readObject() as T
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
        return result ?: defaultValue
    }

    /**
     * Save Object (Serializable) Value
     *
     * @param key   of value
     * @param value to store
     */
    override fun <T : Serializable?> putObject(key: String, value: T) {
        val outputName = File(dir, File.separator + key.lowercase(Locale.getDefault()) + ".data")
        try {
            ObjectOutputStream(FileOutputStream(outputName)).use {
                it.writeObject(value)
            }.also {
                mObjects = putValue(mObjects, key, outputName).also { objects ->
                    updateJson(OBJECTS, objects)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun <T> putValue(json: JsonObject?, key: String, value: T): JsonObject {
        return buildJsonObject {
            json?.forEach { (k, v) ->
                put(k, v)
            }
            put(key, JsonPrimitive(value.toString()))
        }
    }
}
