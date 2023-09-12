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
    implementation 'com.github.TimScriptov:preferences:Tag'
}
```

## Init:
```kotlin
    Preferences(File("dirPath"), "prefName").init()
```

## Get:
```kotlin
    Preferences.getString("key", defaultValue = "value")
    Preferences.getArray("key", defaultValue = emptyList(), String::class)
    Preferences.getBoolean("key", defaultValue = false)
    Preferences.getFloat("key", defaultValue = 0f)
    Preferences.getInt("key", defaultValue = 0)
    Preferences.getLong("key", defaultValue = 0L)
    Preferences.getObject("key", defaultValue = Date())
    Preferences.getDouble("key", defaultValue = 0.0)
```

## Put:
```kotlin
    Preferences.putString("key", value = "value")
    Preferences.putArray("key", value = listOf("1", "2", "3"), String::class)
    Preferences.putBoolean("key", value = false)
    Preferences.putFloat("key", value = 0f)
    Preferences.putInt("key", value = 0)
    Preferences.putLong("key", value = 0L)
    Preferences.putObject("key", value = Date())
    Preferences.putDouble("key", value = 0.0)
```
