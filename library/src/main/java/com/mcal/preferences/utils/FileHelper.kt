package com.mcal.preferences.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.charset.StandardCharsets

object FileHelper {
    fun readText(file: File): String {
        return file.inputStream().readBytes().toString(StandardCharsets.UTF_8)
    }

    suspend fun writeTextAsync(file: File, content: String) = withContext(Dispatchers.IO) {
        file.writeBytes(
            content.toByteArray(
                StandardCharsets.UTF_8
            )
        )
    }
}
