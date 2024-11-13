package com.experience.greatdumper

import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.experience.greatdumper.hprofutils.HprofViewer
import com.experience.greatdumper.ui.theme.GreatDumperTheme
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.main_activity)
        val btn = findViewById<Button>(R.id.myBtn)
        btn.setOnClickListener {
            val heapFilaPath = "${applicationInfo.dataDir}/hprof/memory-20241103T222928.hprof"
            val heapFilaFile = File(heapFilaPath);
            HprofViewer.readHprofFile(heapFilaFile);
        }
//        setContent {
//            GreatDumperTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
//            }
//        }
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
    GreatDumperTheme {
        Greeting("Android")
    }
}