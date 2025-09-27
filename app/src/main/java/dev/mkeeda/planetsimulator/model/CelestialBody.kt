package dev.mkeeda.planetsimulator.model

import androidx.compose.ui.graphics.Color

data class CelestialBody(
    val id: Int,
    val x: Double,
    val y: Double,
    val velocityX: Double,
    val velocityY: Double,
    val mass: Double = 1.0,
    val radius: Float = 20f,
    val color: Color = Color.Blue,
    val name: String = "Body"
) {
    fun updatePosition(deltaTime: Double): CelestialBody {
        return copy(
            x = x + velocityX * deltaTime,
            y = y + velocityY * deltaTime
        )
    }

    fun applyForce(fx: Double, fy: Double, deltaTime: Double): CelestialBody {
        // 加速度を計算 a = f * m
        // 加速度から速度vを計算 v = a * t
        return copy(
            velocityX = velocityX + (fx / mass) * deltaTime,
            velocityY = velocityY + (fy / mass) * deltaTime
        )
    }
}