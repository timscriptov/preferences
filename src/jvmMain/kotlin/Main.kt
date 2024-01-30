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
    println("\nARRAY:")
    arrayTest()
    println("\nARRAY TYPE:")
    arrayTypeTest()
    println("\nSTRING:")
    stringTest()
    println("\nCHAR:")
    charTest()
    println("\nBYTE:")
    byteTest()
    println("\nBOOLEAN:")
    booleanTest()
    println("\nLONG:")
    longTest()
    println("\nINT:")
    intTest()
    println("\nFLOAT:")
    floatTest()
    println("\nDOUBLE:")
    doubleTest()
    println("\nSHORT:")
    shortTest()
    println("\nOBJECT:")
    runCatching {
        objectTest()
    }.onFailure {
        println(it)
    }
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

fun shortTest() {
    var test by shortDelegate("test", 0)
    println(test)
    test = 2
    println(test)
    val test2 by shortDelegate("test", 0)
    println(test2)
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
    println("   OBJECT:")
    runCatching {
        val objectIterator = manager.iterator(Date::class.java)
        objectIterator?.forEach { item ->
            println("       Key: ${item?.key}, Value: ${item?.value}")
        }
    }.onFailure {
        println(it)
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
    var test by booleanDelegate("boolean", false)
    println(test)
    test = true
    println(test)
}

fun floatTest() {
    var test by floatDelegate("float", 0f)
    println(test)
    test = 1999f
    println(test)
}

fun doubleTest() {
    var test by doubleDelegate("double", 0.0)
    println(test)
    test = 4499.0
    println(test)
}

fun longTest() {
    var test by longDelegate("long", 0L)
    println(test)
    test = 1000L
    println(test)
}

fun intTest() {
    var test by intDelegate("int", 0)
    println(test)
    test = 6
    println(test)
}

fun arrayTypeTest() {
    val manager = PreferencesManager(getWorkingDir(), "preferences.json")

    val str = manager.getList("string", listOf("1", "2", "3"), String::class)
    println(str)
    manager.putList("string", listOf("4", "8", "9", "10"), String::class)
    println(str)

    val ch = manager.getList("char", listOf('1', '2', '3'), Char::class)
    println(ch)
    manager.putList("char", listOf('4', '8', '9', 'A'), Char::class)
    println(ch)

    val b = manager.getList("byte", listOf('1'.code.toByte(), '2'.code.toByte(), '3'.code.toByte()), Byte::class)
    println(b)
    manager.putList("byte", listOf('4'.code.toByte(), '8'.code.toByte(), 'A'.code.toByte()), Byte::class)
    println(b)

    val bool = manager.getList("bool", listOf(true, true, false), Boolean::class)
    println(bool)
    manager.putList("bool", listOf(false, false, false, true), Boolean::class)
    println(bool)

    val l = manager.getList("long", listOf(1, 2, 3L), Long::class)
    println(l)
    manager.putList("long", listOf(4, 8, 90L, 1000), Long::class)
    println(l)

    val test = manager.getList("int", listOf(1, 2, 3), Int::class)
    println(test)
    manager.putList("int", listOf(4, 8, 9, 10), Int::class)
    println(test)

    val f = manager.getList("float", listOf(1f, 2.1f, 3f), Float::class)
    println(f)
    manager.putList("float", listOf(4.8f, 8f, 9f, 10f), Float::class)
    println(f)

    val d = manager.getList("double", listOf(1.2, 2.1, 3.0), Double::class)
    println(d)
    manager.putList("double", listOf(4.8 + 2.0, 8.2, 9.23, 10.999), Double::class)
    println(d)

    val test3 = manager.getList("short", listOf(0, 1, 2), Short::class)
    println(test3)
    manager.putList("short", listOf(3, 4, 5, 6), Short::class)
    println(test3)
}

fun arrayTest() {
    var str by listDelegate("string1", listOf("1", "2", "3"))
    println(str)
    str = listOf("12342342")

    var c by listDelegate("char1", listOf('1', '2', '3'))
    println(c)
    c = listOf('4', '5', '6')
    println(c)

    var b by listDelegate("byte1", listOf('1'.code.toByte(), '2'.code.toByte(), '3'.code.toByte()))
    println(b)
    b = listOf('4'.code.toByte(), '8'.code.toByte(), 'A'.code.toByte())
    println(b)

    var bool by listDelegate("bool1", listOf(true, true, false))
    println(bool)
    bool = (listOf(false, false, false, true))
    println(bool)

    var l by listDelegate("long1", listOf(1, 2, 3L))
    println(l)
    l = (listOf(4, 8, 90L, 1000))
    println(l)

    var i by listDelegate("int1", listOf(1, 2, 3))
    println(i)
    i = listOf(4, 5, 6)
    println(i)

    var f by listDelegate("float1", listOf(1f, 2.1f, 3f))
    println(f)
    f = (listOf(4.8f, 8f, 9f, 10f))
    println(f)

    var d by listDelegate("double1", listOf(1.2, 2.1, 3.0))
    println(d)
    d = (listOf(4.8 + 2.0, 8.2, 9.23, 10.999))
    println(d)

    var s by listDelegate("short1", listOf(0.toShort(), 1, 2))
    println(s)
    s = (listOf(3.toShort(), 4, 5, 6))
    println(s)
}

fun byteTest() {
    var test by byteDelegate("byte", 'a'.code.toByte())
    println(test)
    test = 'b'.code.toByte()
    println(test)
}

fun charTest() {
    var test by charDelegate("char", 'a')
    println(test)
    test = 'b'
    println(test)
}

fun stringTest() {
    var test by stringDelegate("string", "def")
    println(test)
    test = "lol"
    println(test)
}
