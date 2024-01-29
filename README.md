[![](https://jitpack.io/v/TimScriptov/preferences.svg)](https://jitpack.io/#TimScriptov/preferences)

# Preferences library Multiplatform

## Add it in your root build.gradle at the end of repositories:
```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

## Add the dependency
```groovy
dependencies {
    implementation 'com.github.timscriptov:preferences:Tag'
}
```

## Init:
```kotlin
    val manager = PreferencesManager(getWorkingDir(), "preferences.json")
```

## Get:
```kotlin
    manager.getChar("key", defaultValue = 'a')
    manager.getByte("key", defaultValue = 'a'.code.toByte())
    manager.getString("key", defaultValue = "value")
    manager.getList("key", defaultValue = emptyList())
    manager.getBoolean("key", defaultValue = false)
    manager.getFloat("key", defaultValue = 0f)
    manager.getInt("key", defaultValue = 0)
    manager.getLong("key", defaultValue = 0L)
    manager.getObject("key", defaultValue = Date())
    manager.getDouble("key", defaultValue = 0.0)
    manager.getShort("key", defaultValue = 0)
```

## Put:
```kotlin
    manager.putChar("key", value = 'a')
    manager.putByte("key", value = 'a'.code.toByte())
    manager.putString("key", value = "value")
    manager.putList("key", value = listOf("1", "2", "3"))
    manager.putBoolean("key", value = false)
    manager.putFloat("key", value = 0f)
    manager.putInt("key", value = 0)
    manager.putLong("key", value = 0L)
    manager.putObject("key", value = Date())
    manager.putDouble("key", value = 0.0)
    manager.putShort("key", value = 0.0)
```
