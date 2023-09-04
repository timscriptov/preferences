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


class Preferences(
    prefDir: File,
    preferencesName: String
) {
    init {
        mPrefDir = prefDir
        mPrefFile = File(prefDir, preferencesName)
        mJsonObject = buildJsonObject { }
    }

    /**
     * Read content from data file
     */
    fun init() {
        val prefDir = mPrefDir
        if (prefDir != null) {
            if (!prefDir.exists() && !prefDir.mkdir()) {
                return
            }
            val prefFile = mPrefFile
            if (prefFile != null) {
                if (!prefFile.exists() && !prefFile.createNewFile()) {
                    println("Cannot create memory file")
                }
                loadJSON(prefFile)
                checkJson()
            }
        }
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

    companion object {
        const val INTEGERS = "integers"
        const val STRINGS = "strings"
        const val BOOLEANS = "booleans"
        const val FLOATS = "floats"
        const val DOUBLES = "doubles"
        const val LONGS = "longs"
        const val OBJECTS = "objects"
        const val ARRAYS = "arrays"

        private var mPrefDir: File? = null
        private var mPrefFile: File? = null

        private var mJsonObject: JsonObject? = null
        private var mIntegers: JsonObject? = null
        private var mStrings: JsonObject? = null
        private var mBooleans: JsonObject? = null
        private var mFloats: JsonObject? = null
        private var mDoubles: JsonObject? = null
        private var mLongs: JsonObject? = null
        private var mObjects: JsonObject? = null
        private var mArrays: JsonObject? = null

        /**
         * Create JSON Object if not exist
         */
        private fun ifNotExist(key: String): JsonObject? {
            val json = mJsonObject
            if (json != null) {
                if (!json.containsKey(key)) {
                    mJsonObject = buildJsonObject {
                        json.forEach { (k, v) ->
                            put(k, v)
                        }
                        put(key, buildJsonObject { })
                    }
                }
            }
            return mJsonObject?.get(key)?.jsonObject
        }

        /**
         * Write each change on the file
         */
        private fun updateMemory() {
            runBlocking {
                val prefFile = mPrefFile
                if (prefFile != null) {
                    val json = mJsonObject
                    if (json != null) {
                        val jsonString = json.toString().formatJson()
                        FileHelper.writeTextAsync(prefFile, jsonString)
                    }
                }
            }
        }

        private fun updateJson(key: String, newObject: JsonObject) {
            val json = mJsonObject
            if (json != null) {
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
        }

        /**
         * Put an array value in the preferences file.
         *
         * @param key the key of the array
         * @param value the array value
         * @param elementClass the class of the array elements
         */
        @OptIn(InternalSerializationApi::class)
        fun <T : Any> putArray(key: String, value: List<T>, elementClass: KClass<T>) {
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
         * Save Integer Value
         *
         * @param key   of value
         * @param value to store
         */
        fun putInt(key: String, value: Int) {
            mIntegers = putValue(mIntegers, key, value).also {
                updateJson(INTEGERS, it)
            }
        }

        /**
         * Save String Value
         *
         * @param key   of value
         * @param value to store
         */
        fun putString(key: String, value: String) {
            mStrings = putValue(mStrings, key, value).also {
                updateJson(STRINGS, it)
            }
        }

        /**
         * Save Boolean Value
         *
         * @param key   of value
         * @param value to store
         */
        fun putBoolean(key: String, value: Boolean) {
            mBooleans = putValue(mBooleans, key, value).also {
                updateJson(BOOLEANS, it)
            }
        }

        /**
         * Save Float Value
         *
         * @param key   of value
         * @param value to store
         */
        fun putFloat(key: String, value: Float) {
            mFloats = putValue(mFloats, key, value).also {
                updateJson(FLOATS, it)
            }
        }

        /**
         * Save Double Value
         *
         * @param key   of value
         * @param value to store
         */
        fun putDouble(key: String, value: Double) {
            mDoubles = putValue(mDoubles, key, value).also {
                updateJson(DOUBLES, it)
            }
        }

        /**
         * Save Long Value
         *
         * @param key   of value
         * @param value to store
         */
        fun putLong(key: String, value: Long) {
            mLongs = putValue(mLongs, key, value).also {
                updateJson(LONGS, it)
            }
        }

        /**
         * Save Object (Serializable) Value
         *
         * @param key   of value
         * @param value to store
         */
        fun <T : Serializable?> putObject(key: String, value: T) {
            val prefDir = mPrefDir
            if (prefDir != null) {
                val outputName =
                    File(prefDir, File.separator + key.lowercase(Locale.getDefault()) + ".data")
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
        }

        private fun <T> putValue(json: JsonObject?, key: String, value: T): JsonObject {
            return buildJsonObject {
                json?.forEach { (k, v) ->
                    put(k, v)
                }
                put(key, JsonPrimitive(value.toString()))
            }
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
        fun remove(key: String, c: Class<Any?>) {
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
         * Get the value of a key as an array
         *
         * @param key the key of the array
         * @param defaultValue the default value if the array doesn't exist or has a different type
         * @param T the type of the array elements
         * @return the array value or the default value
         */
        fun <T : Any> getArray(
            key: String,
            defaultValue: List<T> = emptyList(),
            elementClass: KClass<T>
        ): List<T> {
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
         * Get String Value
         *
         * @param key          of value
         * @param defaultValue if absent
         */
        fun getInt(key: String, defaultValue: Int): Int {
            return mIntegers?.get(key)?.jsonPrimitive?.int ?: defaultValue
        }

        /**
         * Get String Value
         *
         * @param key          of value
         * @param defaultValue if absent
         */
        fun getString(key: String, defaultValue: String): String {
            return mStrings?.get(key)?.jsonPrimitive?.content ?: defaultValue
        }

        /**
         * Get Boolean Value
         *
         * @param key          of value
         * @param defaultValue if absent
         */
        fun getBoolean(key: String, defaultValue: Boolean): Boolean {
            return mBooleans?.get(key)?.jsonPrimitive?.boolean ?: defaultValue
        }

        /**
         * Get Float Value
         *
         * @param key          of value
         * @param defaultValue if absent
         */
        fun getFloat(key: String, defaultValue: Float): Float {
            return mFloats?.get(key)?.jsonPrimitive?.float ?: defaultValue
        }

        /**
         * Get Double Value
         *
         * @param key          of value
         * @param defaultValue if absent
         */
        fun getDouble(key: String, defaultValue: Double): Double {
            return mDoubles?.get(key)?.jsonPrimitive?.double ?: defaultValue
        }

        /**
         * Get Long Value
         *
         * @param key          of value
         * @param defaultValue if absent
         */
        fun getLong(key: String, defaultValue: Long): Long {
            return mLongs?.get(key)?.jsonPrimitive?.long ?: defaultValue
        }

        /**
         * Get Object Value
         *
         * @param key          of value
         * @param defaultValue if absent
         */
        @Suppress("UNCHECKED_CAST")
        fun <T> getObject(key: String, defaultValue: T): T {
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
    }
}
