package com.mcal.preferences.utils

import java.io.File
import java.nio.charset.StandardCharsets

object FileHelper {
    fun readFile(file: File) = file.inputStream().readBytes().toString(StandardCharsets.UTF_8)
    fun writeToFile(file: File, content: String) = file.writeBytes(content.toByteArray(
        StandardCharsets.UTF_8
    ))
}
