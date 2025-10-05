package dev.mkeeda.orbitalSimulator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.mkeeda.orbitalSimulator.ui.OrbitalSimulator
import dev.mkeeda.orbitalSimulator.ui.theme.OrbitalSimulatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OrbitalSimulatorTheme {
                OrbitalSimulator()
            }
        }
    }
}