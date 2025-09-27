package dev.mkeeda.planetsimulator.model

import androidx.compose.ui.graphics.Color

data class CelestialBody(
    val id: Int,
    val x: Double,
    val y: Double,
    val vx: Double,
    val vy: Double,
    val mass: Double = 1.0,
    val radius: Float = 20f,
    val color: Color = Color.Blue,
    val name: String = "Body"
) {
    fun updatePosition(deltaTime: Double): CelestialBody {
        return copy(
            x = x + vx * deltaTime,
            y = y + vy * deltaTime
        )
    }

    fun applyForce(fx: Double, fy: Double, deltaTime: Double): CelestialBody {
        return copy(
            vx = vx + (fx / mass) * deltaTime,
            vy = vy + (fy / mass) * deltaTime
        )
    }
}