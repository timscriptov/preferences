import com.mcal.preferences.PreferencesManager
import java.io.Serializable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <T> listDelegate(key: String, defaultValue: List<T>) = PreferencePrimitiveDelegate(key, defaultValue)
fun stringDelegate(key: String, defaultValue: String) = PreferencePrimitiveDelegate(key, defaultValue)
fun charDelegate(key: String, defaultValue: Char) = PreferencePrimitiveDelegate(key, defaultValue)
fun byteDelegate(key: String, defaultValue: Byte) = PreferencePrimitiveDelegate(key, defaultValue)
fun booleanDelegate(key: String, defaultValue: Boolean) = PreferencePrimitiveDelegate(key, defaultValue)
fun intDelegate(key: String, defaultValue: Int) = PreferencePrimitiveDelegate(key, defaultValue)
fun longDelegate(key: String, defaultValue: Long) = PreferencePrimitiveDelegate(key, defaultValue)
fun floatDelegate(key: String, defaultValue: Float) = PreferencePrimitiveDelegate(key, defaultValue)
fun doubleDelegate(key: String, defaultValue: Double) = PreferencePrimitiveDelegate(key, defaultValue)
fun shortDelegate(key: String, defaultValue: Short) = PreferencePrimitiveDelegate(key, defaultValue)
fun <T : Serializable> objectDelegate(key: String, defaultValue: T) = PreferencePrimitiveDelegate(key, defaultValue)

class PreferencePrimitiveDelegate<T : Any>(
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
            is List<*> -> getList(key, defaultValue) as T
            is String -> getString(key, defaultValue) as T
            is Char -> getChar(key, defaultValue) as T
            is Byte -> getByte(key, defaultValue) as T
            is Boolean -> getBoolean(key, defaultValue) as T
            is Int -> getInt(key, defaultValue) as T
            is Long -> getLong(key, defaultValue) as T
            is Float -> getFloat(key, defaultValue) as T
            is Double -> getDouble(key, defaultValue) as T
            is Short -> getShort(key, defaultValue) as T
            else -> getObject(key, defaultValue)
        }
    }

    private fun <T : Any> setValueToPreferences(value: T) {
        when (value) {
            is List<*> -> putList(key, value as List<String>)
            is String -> putString(key, value)
            is Char -> putChar(key, value)
            is Byte -> putByte(key, value)
            is Boolean -> putBoolean(key, value)
            is Int -> putInt(key, value)
            is Long -> putLong(key, value)
            is Float -> putFloat(key, value)
            is Double -> putDouble(key, value)
            is Short -> putShort(key, value)
            else -> putObject(key, value::class.java)
        }
    }
}
