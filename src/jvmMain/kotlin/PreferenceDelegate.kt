import com.mcal.preferences.PreferencesManager
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun listDelegate(key: String, defaultValue: List<String>) = PreferenceDelegate(key, defaultValue)
fun stringDelegate(key: String, defaultValue: String) = PreferenceDelegate(key, defaultValue)
fun booleanDelegate(key: String, defaultValue: Boolean) = PreferenceDelegate(key, defaultValue)
fun intDelegate(key: String, defaultValue: Int) = PreferenceDelegate(key, defaultValue)
fun longDelegate(key: String, defaultValue: Long) = PreferenceDelegate(key, defaultValue)
fun floatDelegate(key: String, defaultValue: Float) = PreferenceDelegate(key, defaultValue)

class PreferenceDelegate<T>(
    private val key: String,
    private val defaultValue: T
) : PreferencesManager(getWorkingDir(), "preferences.json"), ReadWriteProperty<T?, T> {

    override fun getValue(thisRef: T?, property: KProperty<*>): T {
        return getValueFromPreferences()
    }

    override operator fun setValue(thisRef: T?, property: KProperty<*>, value: T) {
        setValueToPreferences(value)
    }

    private fun getValueFromPreferences(): T {
        return when (defaultValue) {
            is List<*> -> getList(key, defaultValue as List<String>) as T
            is String -> getString(key, defaultValue as String) as T
            is Int -> getInt(key, defaultValue as Int) as T
            is Long -> getLong(key, defaultValue as Long) as T
            is Float -> getFloat(key, defaultValue as Float) as T
            is Boolean -> getBoolean(key, defaultValue as Boolean) as T
            is Double -> getDouble(key, defaultValue as Double) as T
            else -> throw IllegalArgumentException("Unsupported type")
        }
    }

    private fun setValueToPreferences(value: T) {
        when (value) {
            is List<*> -> putList(key, value as List<String>)
            is String -> putString(key, value)
            is Int -> putInt(key, value)
            is Long -> putLong(key, value)
            is Float -> putFloat(key, value)
            is Boolean -> putBoolean(key, value)
            is Double -> putDouble(key, value)
            else -> throw IllegalArgumentException("Unsupported type")
        }
    }
}
