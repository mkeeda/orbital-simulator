package dev.mkeeda.orbitalSimulator.util

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

    fun simToScreen(x: Float, y: Float): Offset {
        return Offset(
            x = x * scale + offsetX,
            y = y * scale + offsetY
        )
    }

    fun scaleToScreen(value: Float): Float {
        return value * scale
    }

    companion object {
        const val SIMULATION_WIDTH = 1000f
        const val SIMULATION_HEIGHT = 1000f
    }
}