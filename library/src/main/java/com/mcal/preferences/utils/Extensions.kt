package com.mcal.preferences.utils

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlin.reflect.KClass

object Extensions {
    private val mJson = Json { prettyPrint = true }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> JsonPrimitive.tryDeserialize(elementClass: KClass<T>): T? {
        return try {
            when (elementClass) {
                String::class -> content
                Char::class -> content.firstOrNull()
                Boolean::class -> content.toBoolean()
                Byte::class -> content.toByteOrNull()
                Short::class -> content.toShortOrNull()
                Int::class -> content.toIntOrNull()
                Long::class -> content.toLongOrNull()
                Float::class -> content.toFloatOrNull()
                Double::class -> content.toDoubleOrNull()
                else -> null
            } as? T
        } catch (e: Exception) {
            null
        }
    }

    fun String.formatJson(): String {
        return try {
            mJson.encodeToString(Json.parseToJsonElement(this))
        } catch (e: Exception) {
            ""
        }
    }
}
