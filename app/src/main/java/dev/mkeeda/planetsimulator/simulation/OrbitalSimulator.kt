package dev.mkeeda.planetsimulator.simulation

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import dev.mkeeda.planetsimulator.model.CelestialBody
import kotlin.math.pow
import kotlin.math.sqrt

class OrbitalSimulator {
    var bodies by mutableStateOf(listOf<CelestialBody>())
        private set

    init {
        reset()
    }

    fun update(deltaTime: Double) {
        bodies = bodies.map { body ->
            body.updatePosition(deltaTime)
        }
    }

    fun reset() {
        bodies = listOf(
            CelestialBody(
                id = 1,
                x = 500.0,
                y = 500.0,
                vx = 0.0,
                vy = 0.0,
                mass = 1.0,
                radius = 30f,
                color = Color.Yellow,
                name = "Sun"
            ),
            CelestialBody(
                id = 2,
                x = 700.0,
                y = 500.0,
                vx = 0.0,
                vy = 30.0,
                mass = 0.1,
                radius = 15f,
                color = Color.Blue,
                name = "Planet"
            )
        )
    }
}