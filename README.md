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
    implementation("com.github.timscriptov:preferences:Tag")
}
```

## Init:
```kotlin
val manager = PreferencesManager(getWorkingDir(), "preferences.json")
```

## Get:
```kotlin
manager.getList("key", defaultValue = emptyList())
manager.getList("key", defaultValue = emptyList(), String::class.java)
manager.getString("key", defaultValue = "value")
manager.getChar("key", defaultValue = 'a')
manager.getByte("key", defaultValue = 'a'.code.toByte())
manager.getBoolean("key", defaultValue = false)
manager.getLong("key", defaultValue = 0L)
manager.getInt("key", defaultValue = 0)
manager.getFloat("key", defaultValue = 0f)
manager.getDouble("key", defaultValue = 0.0)
manager.getShort("key", defaultValue = 0)
manager.getObject("key", defaultValue = Date())
```

## Put:
```kotlin
manager.putList("key", value = listOf("1", "2", "3"))
manager.putList("key", value = emptyList(), String::class.java)
manager.putString("key", value = "value")
manager.putChar("key", value = 'a')
manager.putByte("key", value = 'a'.code.toByte())
manager.putBoolean("key", value = false)
manager.putLong("key", value = 0L)
manager.putInt("key", value = 0)
manager.putFloat("key", value = 0f)
manager.putDouble("key", value = 0.0)
manager.putShort("key", value = 0.0)
manager.putObject("key", value = Date())
```
## Iterator:
```kotlin
manager.iterator(List::class.java)
manager.iterator(String::class.java)
manager.iterator(Char::class.java)
manager.iterator(Byte::class.java)
manager.iterator(Boolean::class.java)
manager.iterator(Long::class.javaL)
manager.iterator(Int::class.java)
manager.iterator(Float::class.java)
manager.iterator(Double::class.java)
manager.iterator(Short::class.java)
manager.iterator(Date::class.java)
```
