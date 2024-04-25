package com.github.lucasaguiar11.kotlin_baselime_logger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.lucasaguiar11.kotlin_baselime_logger.ui.theme.KotlinbaselimeloggerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        BaselimeConfig.new(
            baseUrl = BuildConfig.BASE_URL,
            apiKey = BuildConfig.API_KEY,
            serviceName = "kotlin-baselime-logger-sample",
            defaultData = mapOf(
                "app_version" to BuildConfig.VERSION_NAME,
                "build_type" to BuildConfig.BUILD_TYPE,
                "serial_number" to "SN1234567"
            ),
            isDebug = true
        )


        Logger.i("MainActivity", "onCreate")
        Logger.i(
            "MainActivity",
            "onCreate",
            mapOf("timestamp" to System.currentTimeMillis().toString())
        )

        try {
            throw Exception("This is a test exception")
        } catch (e: Exception) {
            Logger.e(
                "MainActivity",
                "onCreate with exception",
                throwable = e
            )
        }

        Logger.d("MainActivity", "onCreate", mapOf("duration" to "1000"))
        Logger.w("MainActivity", "onCreate", mapOf("duration" to "2000"))


        for (i in 1..50) {
            Logger.i("MainActivity - LOOP", "i => $i", mapOf("iteration" to i.toString()))
            Thread.sleep(10)
        }

        super.onCreate(savedInstanceState)
        setContent {
            KotlinbaselimeloggerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KotlinbaselimeloggerTheme {
        Greeting("Android")
    }
}