package com.mcal.preferences.template

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mcal.preferences.Preferences
import com.mcal.preferences.R
import java.util.Date

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Preferences.putString("a", value = "Hello, World!")
        Preferences.putArray("b", value = listOf("1", "2", "3"), String::class)
        Preferences.putBoolean("c", value = false)
        Preferences.putFloat("d", value = 45f)
        Preferences.putInt("e", value = 2325)
        Preferences.putLong("f", value = 767L)
        Preferences.putObject("g", value = Date())
        Preferences.putDouble("k", value = 43.86)

        MaterialAlertDialogBuilder(this).apply {
            setMessage(buildString {
                append(Preferences.getString("a", defaultValue = ""))
                append("\n")
                append(Preferences.getArray("b", defaultValue = emptyList(), String::class))
                append("\n")
                append(Preferences.getBoolean("c", defaultValue = false))
                append("\n")
                append(Preferences.getFloat("d", defaultValue = 0f))
                append("\n")
                append(Preferences.getInt("e", defaultValue = 0))
                append("\n")
                append(Preferences.getLong("f", defaultValue = 0L))
                append("\n")
                append(Preferences.getDouble("k", defaultValue = 0.0))
                append("\n")
                @Suppress("CAST_NEVER_SUCCEEDS")
                append((Preferences.getObject("g", defaultValue = null) as Date).time)
            })
        }.show()
    }
}