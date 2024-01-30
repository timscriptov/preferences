package com.mcal.preferences

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import kotlinx.serialization.serializer
import java.io.*
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.reflect.KClass

open class PreferencesManager(
    private val dir: File,
    private val name: String
) {
    companion object {
        private const val ARRAYS = "arrays"
        private const val STRINGS = "strings"
        private const val CHARS = "chars"
        private const val BYTES = "bytes"
        private const val BOOLEANS = "booleans"
        private const val LONGS = "longs"
        private const val INTEGERS = "integers"
        private const val FLOATS = "floats"
        private const val DOUBLES = "doubles"
        private const val SHORTS = "shorts"
        private const val OBJECTS = "objects"
    }

    private val mJson = Json { prettyPrint = true }

    private var mJsonObject = buildJsonObject { }

    private var mArrays: JsonObject? = null
    private var mStrings: JsonObject? = null
    private var mChars: JsonObject? = null
    private var mBytes: JsonObject? = null
    private var mBooleans: JsonObject? = null
    private var mLongs: JsonObject? = null
    private var mIntegers: JsonObject? = null
    private var mFloats: JsonObject? = null
    private var mDoubles: JsonObject? = null
    private var mShorts: JsonObject? = null
    private var mObjects: JsonObject? = null


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
            runBlocking(Dispatchers.IO) {
                val content = file.inputStream().readBytes().toString(StandardCharsets.UTF_8)
                Json.parseToJsonElement(content) as JsonObject
            }
        }.getOrElse {
            buildJsonObject { }
        }
    }

    /**
     * Check json keys
     */
    private fun checkJson() {
        mArrays = mArrays ?: ifNotExist(ARRAYS)
        mStrings = mStrings ?: ifNotExist(STRINGS)
        mChars = mChars ?: ifNotExist(CHARS)
        mBytes = mBytes ?: ifNotExist(BYTES)
        mBooleans = mBooleans ?: ifNotExist(BOOLEANS)
        mLongs = mLongs ?: ifNotExist(LONGS)
        mIntegers = mIntegers ?: ifNotExist(INTEGERS)
        mFloats = mFloats ?: ifNotExist(FLOATS)
        mDoubles = mDoubles ?: ifNotExist(DOUBLES)
        mShorts = mShorts ?: ifNotExist(SHORTS)
        mObjects = mObjects ?: ifNotExist(OBJECTS)
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
        runBlocking(Dispatchers.IO) {
            File(dir, name).writeBytes(
                mJson.encodeToString(Json.parseToJsonElement(mJsonObject.toString())).toByteArray(
                    StandardCharsets.UTF_8
                )
            )
        }
    }

    /**
     * Updates the JSON object associated with a specific key.
     * If the key already exists in the object, it updates the value;
     * otherwise, it adds the key-value pair to the object.
     *
     * @param key The key within the JSON object to be updated or added.
     * @param newObject The new JsonObject to be associated with the specified key.
     */
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
     * Iterate a kind of values (String, Boolean , Integer ..)
     */
    fun <T : Any> iterator(c: Class<T>): MutableIterator<Item<T>?>? {
        val obj: JsonObject? = when (c) {
            List::class.java -> mArrays
            String::class.java -> mStrings
            Char::class.java -> mChars
            Byte::class.java -> mBytes
            Boolean::class.java -> mBooleans
            Long::class.java -> mLongs
            Int::class.java -> mIntegers
            Float::class.java -> mFloats
            Double::class.java -> mDoubles
            Short::class.java -> mShorts
            else -> mObjects
        }

        return if (!obj.isNullOrEmpty()) {
            val keysIterator = obj.keys.iterator()
            object : MutableIterator<Item<T>?> {
                override fun hasNext(): Boolean {
                    return keysIterator.hasNext()
                }

                override fun next(): Item<T> {
                    val key = keysIterator.next()
                    val value = when (c) {
                        List::class.java -> getList(key, emptyList<String>()) as? T
                        String::class.java -> getString(key, "") as? T
                        Char::class.java -> getChar(key, ' ') as? T
                        Byte::class.java -> getByte(key, "0".toByte()) as? T
                        Boolean::class.java -> getBoolean(key, false) as? T
                        Long::class.java -> getLong(key, 0L) as? T
                        Int::class.java -> getInt(key, 0) as? T
                        Float::class.java -> getFloat(key, 0f) as? T
                        Double::class.java -> getDouble(key, 0.0) as? T
                        Short::class.java -> getShort(key, 0) as? T
                        else -> getObject(key, null)
                    }
                    return Item(key, value as T)
                }

                override fun remove() {
                    throw UnsupportedOperationException()
                }
            }
        } else {
            null
        }
    }


    /**
     * Get the value of a key as an array
     *
     * @param key the key of the array
     * @param defaultValue the default value if the array doesn't exist or has a different type
     * @return the array value or the default value
     */
    fun <T> getList(key: String, defaultValue: List<T>): List<T> {
        val array = mArrays?.get(key)?.jsonArray
        return if (array != null) {
            try {
                array.mapNotNull { element ->
                    element.jsonPrimitive.content as? T
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
    fun <T : Any> putList(key: String, value: List<T>) {
        val newArray = buildJsonObject {
            val arrays = mArrays
            if (arrays != null) {
                arrays.forEach { (k, v) ->
                    put(k, v)
                }
            } else {
                mArrays = buildJsonObject {}
            }
            put(key, buildJsonArray {
                value.forEach { element ->
                    add(Json.encodeToJsonElement(String::class.serializer(), element.toString()))
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
     * @param T the type of the array elements
     * @return the array value or the default value
     */
    fun <T : Any> getList(key: String, defaultValue: List<T> = emptyList(), elementClass: KClass<T>): List<T> {
        val array = mArrays?.get(key)?.jsonArray
        return if (array != null) {
            try {
                array.mapNotNull { element ->
                    when (elementClass) {
                        String::class -> element.jsonPrimitive.content
                        Char::class -> element.jsonPrimitive.content.firstOrNull()
                        Byte::class -> element.jsonPrimitive.content.toByteOrNull()
                        Boolean::class -> element.jsonPrimitive.content.toBoolean()
                        Long::class -> element.jsonPrimitive.content.toLongOrNull()
                        Int::class -> element.jsonPrimitive.content.toIntOrNull()
                        Float::class -> element.jsonPrimitive.content.toFloatOrNull()
                        Double::class -> element.jsonPrimitive.content.toDoubleOrNull()
                        Short::class -> element.jsonPrimitive.content.toShortOrNull()
                        else -> null
                    } as? T
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
    fun <T : Any> putList(key: String, value: List<T>, elementClass: KClass<T>) {
        val newArray = buildJsonObject {
            val arrays = mArrays
            if (arrays != null) {
                arrays.forEach { (k, v) ->
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
     * Get String Value
     *
     * @param key          of value
     * @param defaultValue if absent
     */
    fun getString(key: String, defaultValue: String): String {
        return mStrings?.get(key)?.jsonPrimitive?.content ?: defaultValue
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
     * Get Char Value
     *
     * @param key          of value
     * @param defaultValue if absent
     */
    fun getChar(key: String, defaultValue: Char): Char {
        return mChars?.get(key)?.jsonPrimitive?.content?.first() ?: defaultValue
    }

    /**
     * Save Char Value
     *
     * @param key   of value
     * @param value to store
     */
    fun putChar(key: String, value: Char) {
        mChars = putValue(mChars, key, value).also {
            updateJson(CHARS, it)
        }
    }

    /**
     * Get Byte Value
     *
     * @param key          of value
     * @param defaultValue if absent
     */
    fun getByte(key: String, defaultValue: Byte): Byte {
        return mBytes?.get(key)?.jsonPrimitive?.content?.toByte() ?: defaultValue
    }

    /**
     * Save Byte Value
     *
     * @param key   of value
     * @param value to store
     */
    fun putByte(key: String, value: Byte) {
        mBytes = putValue(mBytes, key, value).also {
            updateJson(BYTES, it)
        }
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
     * Get Long Value
     *
     * @param key          of value
     * @param defaultValue if absent
     */
    fun getLong(key: String, defaultValue: Long): Long {
        return mLongs?.get(key)?.jsonPrimitive?.long ?: defaultValue
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
     * Get String Value
     *
     * @param key          of value
     * @param defaultValue if absent
     */
    fun getInt(key: String, defaultValue: Int): Int {
        return mIntegers?.get(key)?.jsonPrimitive?.int ?: defaultValue
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
     * Get Float Value
     *
     * @param key          of value
     * @param defaultValue if absent
     */
    fun getFloat(key: String, defaultValue: Float): Float {
        return mFloats?.get(key)?.jsonPrimitive?.float ?: defaultValue
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
     * Get Double Value
     *
     * @param key          of value
     * @param defaultValue if absent
     */
    fun getDouble(key: String, defaultValue: Double): Double {
        return mDoubles?.get(key)?.jsonPrimitive?.double ?: defaultValue
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
     * Get Short Value
     *
     * @param key          of value
     * @param defaultValue if absent
     */
    fun getShort(key: String, defaultValue: Short): Short {
        return mShorts?.get(key)?.jsonPrimitive?.content?.toShortOrNull() ?: defaultValue
    }

    /**
     * Save Short Value
     *
     * @param key   of value
     * @param value to store
     */
    fun putShort(key: String, value: Short) {
        mShorts = putValue(mShorts, key, value).also {
            updateJson(SHORTS, it)
        }
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
                    val file = File(filePath)
                    if (file.exists()) {
                        ObjectInputStream(FileInputStream(filePath)).use {
                            result = it.readObject() as T
                        }
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
    fun <T : Serializable> putObject(key: String, value: T) {
        val outputName = File(dir, File.separator + key.lowercase(Locale.getDefault()) + ".data")
        try {
            ObjectOutputStream(FileOutputStream(outputName)).use {
                it.writeObject(value)
            }
            mObjects = putValue(mObjects, key, outputName).also {
                updateJson(OBJECTS, it)
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
