package dev.mkeeda.planetsimulator.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

class CoordinateConverter(
    canvasSize: Size,
) {
    private val scaleX = canvasSize.width / SIMULATION_WIDTH
    private val scaleY = canvasSize.height / SIMULATION_HEIGHT
    private val scale = minOf(scaleX, scaleY)

    private val offsetX = (canvasSize.width - SIMULATION_WIDTH * scale) / 2
    private val offsetY = (canvasSize.height - SIMULATION_HEIGHT * scale) / 2

    fun simToScreen(x: Double, y: Double): Offset {
        return Offset(
            x = (x * scale + offsetX).toFloat(),
            y = (y * scale + offsetY).toFloat()
        )
    }

    fun scaleToScreen(value: Double): Float {
        return (value * scale).toFloat()
    }

    companion object {
        const val SIMULATION_WIDTH = 1000
        const val SIMULATION_HEIGHT = 1000
    }
}