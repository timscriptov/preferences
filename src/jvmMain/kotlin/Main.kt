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
import java.awt.Dimension
import java.io.File

fun getWorkingDir(): File {
    return File(PreferenceDelegate::class.java.protectionDomain.codeSource.location.toURI().path).parentFile
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
    var test by stringDelegate("test", "def")
    println(test)
    test = "lol"
    println(test)
    val test2 by stringDelegate("test", "def")
    println(test2)

    Window(
        title = "Ex Preferences",
        state = rememberWindowState(width = 800.dp, height = 600.dp),
        onCloseRequest = ::exitApplication,
    ) {
        window.minimumSize = Dimension(350, 600)
        App()
    }
}
