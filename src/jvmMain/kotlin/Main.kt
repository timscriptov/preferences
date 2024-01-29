import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.mcal.preferences.PreferencesManager
import java.awt.Dimension
import java.io.File
import java.util.*

fun getWorkingDir(): File {
    return File(PreferencePrimitiveDelegate::class.java.protectionDomain.codeSource.location.toURI().path).parentFile
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    MaterialTheme {
        Scaffold {
            Text("Hello, World!")
        }
    }
}

fun main() = application {
    println("\nDOUBLE:")
    doubleTest()
    println("\nFLOAT:")
    floatTest()
    println("\nLONG:")
    longTest()
    println("\nINT:")
    intTest()
    println("\nARRAY:")
    arrayTest()
    println("\nSTRING:")
    stringTest()
    println("\nCHAR:")
    charTest()
    println("\nBYTE:")
    byteTest()
    println("\nBOOLEAN:")
    booleanTest()
//    println("\nObject:")
//    objectTest()
    println("\nITERATOR:")
    testIterator()

    Window(
        title = "Ex Preferences",
        state = rememberWindowState(width = 800.dp, height = 600.dp),
        onCloseRequest = ::exitApplication,
    ) {
        window.minimumSize = Dimension(350, 600)
        App()
    }
}

fun testIterator() {
    val manager = PreferencesManager(getWorkingDir(), "preferences.json")

    println("   DOUBLE:")
    val doubleIterator = manager.iterator(Double::class.java)
    doubleIterator?.forEach { item ->
        println("       Key: ${item?.key}, Value: ${item?.value}")
    }
    println("   FLOAT:")
    val floatIterator = manager.iterator(Float::class.java)
    floatIterator?.forEach { item ->
        println("       Key: ${item?.key}, Value: ${item?.value}")
    }
    println("   BOOLEAN:")
    val booleanIterator = manager.iterator(Boolean::class.java)
    booleanIterator?.forEach { item ->
        println("       Key: ${item?.key}, Value: ${item?.value}")
    }

    println("   LIST:")
    val listIterator = manager.iterator(List::class.java)
    listIterator?.forEach { item ->
        println("       Key: ${item?.key}, Value: ${item?.value}")
    }
    println("   STRING:")
    val stringIterator = manager.iterator(String::class.java)
    stringIterator?.forEach { item ->
        println("       Key: ${item?.key}, Value: ${item?.value}")
    }
    println("   CHAR:")
    val charIterator = manager.iterator(Char::class.java)
    charIterator?.forEach { item ->
        println("       Key: ${item?.key}, Value: ${item?.value}")
    }
    println("   BYTE:")
    val byteIterator = manager.iterator(Byte::class.java)
    byteIterator?.forEach { item ->
        println("       Key: ${item?.key}, Value: ${item?.value}")
    }
    println("   INT:")
    val intIterator = manager.iterator(Int::class.java)
    intIterator?.forEach { item ->
        println("       Key: ${item?.key}, Value: ${item?.value}")
    }
    println("   LONG:")
    val longIterator = manager.iterator(Long::class.java)
    longIterator?.forEach { item ->
        println("       Key: ${item?.key}, Value: ${item?.value}")
    }
    println("   SHORT:")
    val shortIterator = manager.iterator(Short::class.java)
    shortIterator?.forEach { item ->
        println("       Key: ${item?.key}, Value: ${item?.value}")
    }
}

class Test {
    val x = 123
}

fun objectTest() {
    var test by objectDelegate("date", Date())
    println(test.time)
    test = Date()
    println(test.time)

    val manager = PreferencesManager(getWorkingDir(), "preferences.json")
    val date = manager.getObject("date2", Date())
    println(date.time)

    manager.putObject("test", Test()::class.java)
    val test2 = manager.getObject("test", Test())
    println(test2.x)
}

fun booleanTest() {
    var test by booleanDelegate("test", false)
    println(test)
    test = true
    println(test)
    val test2 by booleanDelegate("test", false)
    println(test2)
}

fun floatTest() {
    var test by floatDelegate("test", 0f)
    println(test)
    test = 1999f
    println(test)
    val test2 by floatDelegate("test", 0f)
    println(test2)
}

fun doubleTest() {
    var test by doubleDelegate("test", 0.0)
    println(test)
    test = 4499.0
    println(test)
    val test2 by doubleDelegate("test", 0.0)
    println(test2)
}

fun longTest() {
    var test by longDelegate("test", 0L)
    println(test)
    test = 1000L
    println(test)
    val test2 by longDelegate("test", 0L)
    println(test2)
}

fun intTest() {
    var test by intDelegate("test", 0)
    println(test)
    test = 6
    println(test)
    val test2 by intDelegate("test", 0)
    println(test2)
}

fun arrayTest() {
    var test by listDelegate("test2", listOf(1, 2, 3))
    println(test)
    test = listOf(4, 5, 6)
    println(test)
    val test2 by listDelegate("test", listOf("1", "2", "3"))
    println(test2)
}

fun byteTest() {
    var test by byteDelegate("test", 'a'.code.toByte())
    println(test)
    test = 'b'.code.toByte()
    println(test)
    val test2 by byteDelegate("test", 'a'.code.toByte())
    println(test2)
}

fun charTest() {
    var test by charDelegate("test", 'a')
    println(test)
    test = 'b'
    println(test)
    val test2 by charDelegate("test", 'a')
    println(test2)
}

fun stringTest() {
    var test by stringDelegate("test", "def")
    println(test)
    test = "lol"
    println(test)
    val test2 by stringDelegate("test", "def")
    println(test2)
}
